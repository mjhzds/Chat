package com.elvis.example.chat;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.elvis.example.chat.db.DataMsg;
import com.elvis.example.chat.db.DataMsgDao;
import com.elvis.example.chat.bean.Hint;
import com.elvis.example.chat.bean.KeyMaterial;
import com.elvis.example.chat.bean.KeyPair;
import com.elvis.example.chat.bean.Message;
import com.elvis.example.chat.bean.Msk;
import com.elvis.example.chat.bean.Params;
import com.elvis.example.chat.db.Seeds;
import com.elvis.example.chat.db.SeedsDao;
import com.elvis.example.chat.db.Session;
import com.elvis.example.chat.db.SessionDao;
import com.elvis.example.chat.bean.Signature;
import com.elvis.example.chat.utils.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class ChatHelper {

    private static ChatHelper instance = null;
    private ChatModel demoModel = null;

    private static final String TAG = "ChatHelper";
    private Params params;
    private Msk msk;
    private KeyPair keyPair;
    private String username;
    private EMMessageListener messageListener;
    private boolean isFinished = false;
    private String groupName;
    private String launcher;
    private Element[] elements = new Element[3];
    private Element[] rightKeys = new Element[3];
    private Element localRight = null;
    private Element localLeft = null;
    private String localSeed = null;
    private int count = 0;
    private int same = 0;
    private int round2 = 0;

    public synchronized static ChatHelper getInstance() {
        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getLauncher() {
        return launcher;
    }

    public void setLauncher(String launcher) {
        this.launcher = launcher;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Msk getMsk() {
        return msk;
    }

    public void setMsk(Msk msk) {
        this.msk = msk;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {

            Message messageLeft = null;
            Message messageRight = null;
            Signature sigLeft = null;
            Signature sigRight = null;
            Hint hint = null;
            KeyMaterial keyMaterial = null;
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                // in background, do not refresh UI, notify it in notification bar
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                hint = new Hint();
                hint.setBeta(params.getE().getZr().newRandomElement());
                hint.setHint_first(params.getG().powZn(hint.getBeta()));
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    //获取扩展属性
                    switch (action) {
                        case "exchange":
                            //round1,向相邻的用户发送HMAC
                            try {
                                Log.d(TAG, "exchange: msg received!");
                                String sid = message.getStringAttribute("sid");
                                Log.d(TAG, "-------------sid" + sid);
                                String pidString = message.getStringAttribute("pids");
                                Log.d(TAG, "-------------pids" + pidString);
                                String mskString = message.getStringAttribute("msk");
                                Log.d(TAG, "-------------msk" + element_from_string(mskString));
                                launcher = message.getStringAttribute("pid");
                                Log.d(TAG, "-------------launcher" + launcher);
                                //generate first key hint
                                Message msg = createMessage(sid, hint);
                                Signature signature = createSignature(1, msg, hint);
                                List<String> pids = new Gson().fromJson(pidString, new TypeToken<List<String>>() {}.getType());
                                Session session = new Session();
                                session.setSid(sid);
                                session.setPids(pids);
                                session.setMsk(mskString.getBytes("ISO-8859-1"));
                                ChatApplication.getInstance().getDaoSession().insert(session);
                                int i;
                                for (i = 0; i < pids.size(); i++) {
                                    if (pids.get(i).equals(getUsername())) {
                                        break;
                                    }
                                }
                                roundOne(msg, pids, signature, i - 1);
                                Log.d(TAG, "-----------only " + pids.size() + " users");
                                if (pids.size() > 2) {
                                    roundOne(msg, pids, signature, i + 1);
                                    Log.d(TAG, "-----------left" + pids.get((i - 1 + pids.size()) % pids.size()));
                                    Log.d(TAG, "-----------right" + pids.get((i + 1 + pids.size()) % pids.size()));
                                }
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "round1":
                            Log.d(TAG, "round1 received");
                            try {
                                //查询数据库有无相同sid的message，无则写入有则分左右
                                byte[] hash = message.getStringAttribute("hash").getBytes("ISO-8859-1");
                                int size = message.getIntAttribute("size");
                                String msg_from = message.getStringAttribute("msg_from");
                                String sid = message.getStringAttribute("msg_sid");
                                Log.i(TAG, "round 1: sid: " + sid);
                                String pid = message.getStringAttribute("msg_pid");
                                int index = message.getIntAttribute("msg_index");
                                int u_field = message.getIntAttribute("u_field");
                                int h_field = message.getIntAttribute("h_field");
                                int v_field = message.getIntAttribute("v_field");
                                int hint_field = message.getIntAttribute("hint_field");
                                byte[] hint_first = message.getStringAttribute("msg_hint").getBytes("ISO-8859-1");
                                byte[] sig_u = message.getStringAttribute("msg_u").getBytes("ISO-8859-1");
                                byte[] sig_h = message.getStringAttribute("msg_h").getBytes("ISO-8859-1");
                                byte[] sig_v = message.getStringAttribute("msg_v").getBytes("ISO-8859-1");
                                Log.d(TAG, "onCmdMessageReceived: hash:" + Arrays.toString(hash) + "from:" + pid + "hint:" + Arrays.toString(hint_first) + "from:" + msg_from);
                                List<DataMsg> dataMsgs = ChatApplication.getInstance().getDaoSession().getDataMsgDao().queryBuilder().where(DataMsgDao.Properties.Sid.eq(sid)).list();
                                List<Session> sessions = ChatApplication.getInstance().getDaoSession().getSessionDao().queryBuilder().where(SessionDao.Properties.Sid.eq(sid)).list();
                                for (Session session : sessions) {
                                    Log.i(TAG, "test: sessions sid: " + session.getSid());
                                    Log.i(TAG, "test: sessions msk: " + getParams().getE().getZr().newElementFromBytes(session.getMsk()));
                                    Log.i(TAG, "test: sessions pids: " + session.getPids());
                                }
                                Session session = sessions.get(0);
                                if (0 == dataMsgs.size()) {
                                    DataMsg dataMsg = new DataMsg();
                                    dataMsg.setFrom(msg_from);
                                    dataMsg.setSize(size);
                                    dataMsg.setSid(sid);
                                    dataMsg.setPid(pid);
                                    dataMsg.setIndex(index);
                                    dataMsg.setHint_first(hint_first);
                                    dataMsg.setU(sig_u);
                                    dataMsg.setH(sig_h);
                                    dataMsg.setV(sig_v);
                                    dataMsg.setH_field(h_field);
                                    dataMsg.setU_field(u_field);
                                    dataMsg.setV_field(v_field);
                                    dataMsg.setHint_field(hint_field);
                                    dataMsg.setHash(hash);
                                    ChatApplication.getInstance().getDaoSession().getDataMsgDao().insert(dataMsg);
                                    Log.d(TAG, "datamsg only one received ");
                                } else {
                                    Log.d(TAG, "datamsg all received ");
                                    byte[] leftHash = null;
                                    byte[] rightHash = null;
                                    Pairing pairing = getParams().getE();
                                    if (msg_from.equals("left")) {
                                        rightHash = hash;
                                        messageLeft = new Message();
                                        messageRight = new Message();
                                        sigLeft = new Signature();
                                        sigRight = new Signature();
                                        messageRight.setSid(sid);
                                        messageRight.setPid(pid);
                                        messageRight.setIndex(index);
                                        messageRight.setHint_first(pairing.getFieldAt(hint_field).newElementFromBytes(hint_first));
                                        sigRight.setU(pairing.getFieldAt(u_field).newElementFromBytes(sig_u));
                                        sigRight.setH(pairing.getFieldAt(h_field).newElementFromBytes(sig_h));
                                        sigRight.setV(pairing.getFieldAt(v_field).newElementFromBytes(sig_v));
                                        DataMsg dataMsg = ChatApplication.getInstance().getDaoSession().getDataMsgDao().queryBuilder().where(DataMsgDao.Properties.Sid.eq(sid)).unique();
                                        leftHash = dataMsg.getHash();
                                        messageLeft.setSid(dataMsg.getSid());
                                        messageLeft.setPid(dataMsg.getPid());
                                        messageLeft.setIndex(dataMsg.getIndex());
                                        messageLeft.setHint_first(pairing.getFieldAt(dataMsg.getHint_field()).newElementFromBytes(dataMsg.getHint_first()));
                                        sigLeft.setU(pairing.getFieldAt(dataMsg.getU_field()).newElementFromBytes(dataMsg.getU()));
                                        sigLeft.setH(pairing.getFieldAt(dataMsg.getH_field()).newElementFromBytes(dataMsg.getH()));
                                        sigLeft.setV(pairing.getFieldAt(dataMsg.getV_field()).newElementFromBytes(dataMsg.getV()));
                                    } else {
                                        leftHash = hash;
                                        messageLeft = new Message();
                                        messageRight = new Message();
                                        sigLeft = new Signature();
                                        sigRight = new Signature();
                                        messageLeft.setSid(sid);
                                        messageLeft.setPid(pid);
                                        messageLeft.setIndex(index);
                                        messageLeft.setHint_first(pairing.getFieldAt(hint_field).newElementFromBytes(hint_first));
                                        sigLeft.setU(pairing.getFieldAt(u_field).newElementFromBytes(sig_u));
                                        sigLeft.setH(pairing.getFieldAt(h_field).newElementFromBytes(sig_h));
                                        sigLeft.setV(pairing.getFieldAt(v_field).newElementFromBytes(sig_v));
                                        DataMsg dataMsg = ChatApplication.getInstance().getDaoSession().getDataMsgDao().queryBuilder().where(DataMsgDao.Properties.Sid.eq(sid)).unique();
                                        rightHash = dataMsg.getHash();
                                        messageRight.setSid(dataMsg.getSid());
                                        messageRight.setPid(dataMsg.getPid());
                                        messageRight.setIndex(dataMsg.getIndex());
                                        messageRight.setHint_first(pairing.getFieldAt(dataMsg.getHint_field()).newElementFromBytes(dataMsg.getHint_first()));
                                        sigRight.setU(pairing.getFieldAt(dataMsg.getU_field()).newElementFromBytes(dataMsg.getU()));
                                        sigRight.setH(pairing.getFieldAt(dataMsg.getH_field()).newElementFromBytes(dataMsg.getH()));
                                        sigRight.setV(pairing.getFieldAt(dataMsg.getV_field()).newElementFromBytes(dataMsg.getV()));
                                    }
                                    localRight = messageRight.getHint_first();
                                    localLeft = messageLeft.getHint_first();
                                    byte[] leftResult = element_from_string(messageLeft.getPid()).toBytes();
                                    Log.i(TAG, "-----------left hash :" + Arrays.toString(leftHash));
                                    Log.i(TAG, "-----------left hash result:" + Arrays.toString(leftResult));
                                    byte[] rightResult = element_from_string(messageRight.getPid()).toBytes();
                                    Log.i(TAG, "-----------right hash :" + Arrays.toString(rightHash));
                                    Log.i(TAG, "-----------right hash result:" + Arrays.toString(rightResult));
                                    if (!(Arrays.equals(leftHash,leftResult) && Arrays.equals(rightHash,rightResult))) {
                                        Log.d(TAG, "The signature verification is failed!");
                                    } else {
                                        Log.d(TAG, "The signature verification is successful!");
                                        keyMaterial = new KeyMaterial();
                                        Message msg = new Message();
                                        keyMaterial.setLeft_key(getParams().getE().getG1().newRandomElement());
                                        keyMaterial.setRight_key(getParams().getE().getG1().newRandomElement());
                                        keyMaterial.setHint_plus(getParams().getE().getG1().newRandomElement());
                                        msg.setHint_first(getParams().getE().getG1().newRandomElement());
                                        msg.setSid(messageLeft.getSid());
                                        msg.setPid(getUsername());
                                        msg.setIndex(2);
                                        keyMaterial.setLeft_key(messageLeft.getHint_first().powZn(hint.getBeta()));
                                        keyMaterial.setRight_key(messageRight.getHint_first().powZn(hint.getBeta()));
                                        keyMaterial.setHint_plus(keyMaterial.getRight_key().div(keyMaterial.getLeft_key()));
                                        msg.setHint_first(keyMaterial.getHint_plus());
                                        Signature signature = createSignature(2, msg, hint);
                                        List<String> pids = session.getPids();
                                        int position = 0;
                                        for (int i = 0; i < pids.size(); i++) {
                                            if (pids.get(i).equals(getUsername())) {
                                                position = i;
                                            }
                                        }
                                        for (int i = 0; i < pids.size(); i++) {
                                            if (pids.get(i).equals(getUsername())) {
                                                continue;
                                            } else {
                                                EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
                                                emMessage.setAttribute("size",size);
                                                emMessage.setAttribute("position",position);
                                                Log.d(TAG, "round2 send " + pids.get(i) + " : position : " + position);
                                                emMessage.setAttribute("hash", new String(element_from_string(msg.getPid()).toBytes(), "ISO-8859-1"));
                                                emMessage.setAttribute("sid", sid);
                                                emMessage.setAttribute("pid", pid);
                                                emMessage.setAttribute("index", 2);
                                                emMessage.setAttribute("hint_field", pairing.getFieldIndex(msg.getHint_first().getField()));
                                                emMessage.setAttribute("hint", new String(msg.getHint_first().toBytes(), "ISO-8859-1"));
                                                emMessage.setAttribute("right_field",pairing.getFieldIndex(messageRight.getHint_first().getField()));
                                                emMessage.setAttribute("rightKey", new String(messageRight.getHint_first().toBytes(), "ISO-8859-1"));
                                                EMCmdMessageBody cmdBody = new EMCmdMessageBody("round2");
                                                emMessage.addBody(cmdBody);
                                                emMessage.setTo(pids.get(i));
                                                EMClient.getInstance().chatManager().sendMessage(emMessage);
                                                Log.d(TAG, "onCmdMessageSent: success " + pids.get(i));
                                            }
                                        }
                                    }
                                }
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "round2":
                            try {
                                round2++;
                                Element sum = getParams().getG().setToOne();
                                Element ssk = getParams().getG().setToOne();
                                String sid = message.getStringAttribute("sid");
                                String pid = message.getStringAttribute("pid");
                                int size = message.getIntAttribute("size");
                                int position = message.getIntAttribute("position");
                                Log.d(TAG, "round2 received from " + pid + " position: " + position + "round: " + round2);
                                byte[] hash = message.getStringAttribute("hash").getBytes("ISO-8859-1");
                                int index = message.getIntAttribute("index");
                                int hint_field = message.getIntAttribute("hint_field");
                                byte[] hint_plus = message.getStringAttribute("hint").getBytes("ISO-8859-1");
                                int right_field = message.getIntAttribute("right_field");
                                byte[] rightKey = message.getStringAttribute("rightKey").getBytes("ISO-8859-1");
                                elements[position] = getParams().getE().getFieldAt(hint_field).newElementFromBytes(hint_plus);
                                rightKeys[position] = getParams().getE().getFieldAt(right_field).newElementFromBytes(rightKey);
                                List<Session> sessions = ChatApplication.getInstance().getDaoSession().getSessionDao().queryBuilder().where(SessionDao.Properties.Sid.eq(sid)).list();
                                Session session = sessions.get(0);
                                List<String> pids = session.getPids();
                                int currentPosition = 0;                     //本机用户所处位置
                                for (int i = 0; i < pids.size(); i++) {
                                    if (pids.get(i).equals(getUsername())) {
                                        currentPosition = i;
                                        break;
                                    }
                                }
                                elements[currentPosition] = localRight.getImmutable();
                                rightKeys[currentPosition] = localRight.getImmutable();
                                for (int i = 0; i < size + 1; i++) {
                                    Log.d(TAG, "elements----------------------------------------" + elements[i]);
                                    Log.d(TAG, "rightKeys----------------------------------------" + rightKeys[i]);
                                }
                                if (round2 == 2) {
                                    round2 = 0;
                                    for (Element element : elements) {
                                        sum = sum.mul(element);
                                    }
                                    if (sum.isEqual(localLeft)) {
                                        for (Element element : rightKeys) {
                                            ssk = ssk.mul(element);
                                        }
                                    }
                                    Log.d(TAG, "successfully examed!");
                                    //计算seed，建立群组
                                    byte[] result = element_from_string(new String(ssk.toBytes(), "ISO-8859-1")).toBytes();
                                    Log.d(TAG, "seed result: " + Arrays.toString(result));
                                    String seed = new String(result, "ISO-8859-1");
                                    Log.d(TAG, "seed string: " + seed);
                                    localSeed = seed;
                                    Log.d(TAG, "local seed: " + localSeed);
                                    EMMessage emMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
                                    emMessage.setAttribute("seed", seed);
                                    emMessage.setAttribute("sid", sid);
                                    emMessage.setAttribute("pid", getUsername());
                                    EMCmdMessageBody cmdBody = new EMCmdMessageBody("final");
                                    emMessage.addBody(cmdBody);
                                    emMessage.setTo(launcher);
                                    EMClient.getInstance().chatManager().sendMessage(emMessage);
                                } else {
                                    Log.d(TAG, "is equal: not ");
                                }
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "final":
                            Log.d(TAG, "final round received!");
                            try {
                                count++;
                                String seed = message.getStringAttribute("seed");
                                String sid = message.getStringAttribute("sid");
                                String pid = message.getStringAttribute("pid");
                                Log.d(TAG, "group name: " + seed);
                                Log.d(TAG, "session sid: " + sid);
                                Log.d(TAG, "session from: " + pid);
                                List<Session> sessions = ChatApplication.getInstance().getDaoSession().getSessionDao().queryBuilder().where(SessionDao.Properties.Sid.eq(sid)).list();
                                Session session = sessions.get(0);
                                List<String> pids = session.getPids();
                                if (seed.equals(localSeed)) {
                                    same++;
                                }
                                if (same == count && count == pids.size() - 1) {
                                    same = 0;
                                    count = 0;
                                    String[] array = new String[pids.size()];
                                    for (int i = 0; i < pids.size(); i++) {
                                        array[i] = pids.get(i);
                                    }
                                    EMGroupOptions option = new EMGroupOptions();
                                    option.maxUsers = 200;
                                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                                    EMClient.getInstance().groupManager().createGroup(seed, "", array, "", option);
                                    Log.d(TAG, "group successfully created!");
                                    groupName = seed;
                                    isFinished = true;
                                } else {
                                    Log.d(TAG, "group creation failed : seed doesn't match -----same is " + same + ",count is " + count);
                                }

                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                    EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public Message createMessage(String sid, Hint hint) {
        Message message = new Message();
        message.setSid(sid);
        message.setPid(getUsername());
        message.setIndex(1);
        message.setHint_first(hint.getHint_first());
        //test
        System.out.println("round-1--output: Session ID is " + message.getSid());
        System.out.println("round-1--output: pid is " + message.getPid());
        System.out.println("round_1--output: beta is " + hint.getBeta());
        System.out.println("round_1--output: hint_1 is " + hint.getHint_first());
        System.out.println("*****************************************************************************");
        return message;
    }

    public void roundOne(Message msg, List<String> pids, Signature signature, int position) throws UnsupportedEncodingException {
        EMMessage cmdMsg1 = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg1.setAttribute("hash", new String(element_from_string(msg.getPid()).toBytes(), "ISO-8859-1"));
        Log.d(TAG, "roundOne: " + Arrays.toString(element_from_string(msg.getPid()).toBytes()));
        cmdMsg1.setAttribute("size", pids.size() - 1);
        cmdMsg1.setAttribute("launcher", launcher);
        cmdMsg1.setAttribute("msg_from", "left");
        cmdMsg1.setAttribute("msg_sid", msg.getSid());
        cmdMsg1.setAttribute("msg_pid", msg.getPid());
        cmdMsg1.setAttribute("msg_index", msg.getIndex());
        cmdMsg1.setAttribute("msg_hint", new String(msg.getHint_first().toBytes(), "ISO-8859-1"));
        cmdMsg1.setAttribute("hint_field", getParams().getE().getFieldIndex(msg.getHint_first().getField()));
        cmdMsg1.setAttribute("u_field", getParams().getE().getFieldIndex(signature.getU().getField()));
        cmdMsg1.setAttribute("h_field", getParams().getE().getFieldIndex(signature.getH().getField()));
        cmdMsg1.setAttribute("v_field", getParams().getE().getFieldIndex(signature.getV().getField()));
        cmdMsg1.setAttribute("msg_u", new String(signature.getU().toBytes(), "ISO-8859-1"));
        cmdMsg1.setAttribute("msg_h", new String(signature.getH().toBytes(), "ISO-8859-1"));
        cmdMsg1.setAttribute("msg_v", new String(signature.getV().toBytes(), "ISO-8859-1"));
        Log.d(TAG, "msg package successfully");
        EMCmdMessageBody cmdBody1 = new EMCmdMessageBody("round1");
        cmdMsg1.addBody(cmdBody1);
        cmdMsg1.setTo(pids.get((position + pids.size()) % pids.size()));
        Log.d(TAG, "roundOne: msg to " + pids.get((position + pids.size()) % pids.size()));
        EMClient.getInstance().chatManager().sendMessage(cmdMsg1);
    }

    public Signature createSignature(int flag, Message message, Hint hint) {
        Signature signature = new Signature();
        Element temp1,temp2;
        if (1 == flag) {
            signature.setU(getParams().getE().getG1().newRandomElement());
            signature.setH(getParams().getE().getG1().newRandomElement());
            signature.setV(getParams().getE().getG1().newRandomElement());
            signature.setH(element_from_string(message.getPid()));
            signature.setU(message.getHint_first());
            temp1 = getKeyPair().getPub().powZn(hint.getBeta());
            temp2 = getKeyPair().getPrv().powZn(signature.getH());
            signature.setV(temp1.mul(temp2));
        } else {
            Element y;
            signature.setU(getParams().getE().getG1().newRandomElement());
            signature.setH(getParams().getE().getG1().newRandomElement());
            signature.setV(getParams().getE().getG1().newRandomElement());
            temp1 = getParams().getE().getG1().newRandomElement().getImmutable();
            temp2 = getParams().getE().getG1().newRandomElement().getImmutable();
            y = getParams().getE().getZr().newRandomElement().getImmutable();
            signature.setU(getParams().getG().powZn(y));
            byte[] u_to_char = signature.getU().toBytes();
            byte[] msg_to_char = message.getPid().getBytes();
            byte[] newbyte = byteMerger(u_to_char, msg_to_char);
            signature.setH(element_from_string(new String(newbyte)));
            temp1 = getKeyPair().getPub().powZn(y);
            temp2 = getKeyPair().getPrv().powZn(signature.getH());
            signature.setV(temp1.mul(temp2));
        }
        return signature;
    }

    private boolean verify_r1(Message messageLeft, Message messageRight, Signature sigLeft, Signature sigRight) {
        Element publicKey1 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element publicKey2 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element h1 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element h2 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element sign_mul = getParams().getE().getG1().newRandomElement().getImmutable();
        Element left = getParams().getE().getGT().newRandomElement().getImmutable();
        Element right = getParams().getE().getGT().newRandomElement().getImmutable();

        Element temp1 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element temp2 = getParams().getE().getG1().newRandomElement().getImmutable();
        Element temp_e_1 = getParams().getE().getGT().newRandomElement().getImmutable();
        Element temp_e_2 = getParams().getE().getGT().newRandomElement().getImmutable();

        publicKey1.set(element_from_string(messageLeft.getPid()));
        publicKey2.set(element_from_string(messageRight.getPid()));

        System.out.println("Left:Used in verify in roudn 2, sid:" + messageLeft.getSid());
        System.out.println("Left:Used in verify in roudn 2, pid:" + messageLeft.getPid());
        System.out.println("Left:Used in verify in round2, hint 1:" + messageLeft.getHint_first());
        System.out.println("Left: Used in verify in round2, sign, u:" + sigLeft.getU());
        System.out.println("Left: Used in verify in round2, sign, h:" + sigLeft.getH());
        System.out.println("Left: Used in verify in round2, sign, v:" + sigLeft.getV());

        System.out.println("Round 2: Verify: Right: msg: sid" + messageRight.getSid());
        System.out.println("Round 2: Verify: Right: msg: pid" + messageRight.getPid());
        System.out.println("Round 2: Verify: Right: msg: hint 1" + messageRight.getHint_first());
        System.out.println("Round 2: Verify: Right: sign: u" + sigRight.getU());
        System.out.println("Round 2: Verify: Right: sign: h" + sigRight.getH());
        System.out.println("Round 2: Verify: Right: sign: v" + sigRight.getV());

        h1 = element_from_string(messageLeft.getPid());
        h2 = element_from_string(messageRight.getPid());

        //left
        sign_mul = sigLeft.getV().mul(sigRight.getV());
        left = getParams().getE().pairing(params.getG(), sign_mul);
        Log.d("left", "---------------" + left);
        //right
        temp1 = getParams().getHat_alpha().powZn(h1);
        temp2 = getParams().getHat_alpha().powZn(h2);
        temp1 = messageLeft.getHint_first().mul(temp1);
        temp2 = messageRight.getHint_first().mul(temp2);
        temp_e_1 = getParams().getE().pairing(publicKey1, temp1);
        temp_e_2 = getParams().getE().pairing(publicKey2, temp2);
        right = temp_e_1.mul(temp_e_2);
        Log.d("right", "---------------" + right);
        return left.isEqual(right);  //same return true, else false
    }

    private Element element_from_string(String string) {
        Element e = null;
        try {
            e = getParams().getE().getG1().newElementFromBytes(string.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return e;
    }

    private byte[] hash_exam(String s,byte[] element) {
        byte[] result = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            Element e = getParams().getE().getZr().newElementFromBytes(element);
            SecretKey secretKey = new SecretKeySpec(e.toBytes(),"HmacSHA1");
            mac.init(secretKey);
            result = mac.doFinal(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

        private byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public String getGroupName() {
        return groupName;
    }
}
