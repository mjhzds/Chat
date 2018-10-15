package com.elvis.example.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elvis.example.chat.ChatApplication;
import com.elvis.example.chat.ChatHelper;
import com.elvis.example.chat.R;
import com.elvis.example.chat.adapter.GroupAdapter;
import com.elvis.example.chat.bean.Hint;
import com.elvis.example.chat.bean.Message;
import com.elvis.example.chat.bean.Pid;
import com.elvis.example.chat.bean.Signature;
import com.elvis.example.chat.db.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import org.apache.commons.lang.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

public class GroupFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "GroupFragment";

    private ListView lv;
    private GroupAdapter groupAdapter;
    private ArrayList<String> list;
    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    private Button btn_selectAll;
    private Button btn_confirm;
    private Button btn_deselectAll;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                List<EMGroup> grouplist = null;
                String groupName = (String) msg.obj;
                try {
                    grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    for (int i = 0; i < grouplist.size(); i++) {
                        if (grouplist.get(i).getGroupName().equals(groupName)) {
                            String groupId = grouplist.get(i).getGroupId();
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("username", groupId);
                            startActivity(intent);
                        }
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        /* 实例化各个控件 */
        lv = view.findViewById(R.id.lv);
        tv_show = view.findViewById(R.id.tv);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_selectAll = view.findViewById(R.id.btn_selectall);
        btn_deselectAll = view.findViewById(R.id.btn_deselectall);
        btn_confirm.setOnClickListener(this);
        btn_selectAll.setOnClickListener(this);
        btn_deselectAll.setOnClickListener(this);
        list = new ArrayList<>();
        initDate();

        // 实例化自定义的MyAdapter
        groupAdapter = new GroupAdapter(list, this.getContext());
        // 绑定Adapter
        lv.setAdapter(groupAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                GroupAdapter.ViewHolder holder = (GroupAdapter.ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                holder.cb.toggle();
                // 将CheckBox的选中状况记录下来
                GroupAdapter.getIsSelected().put(arg2, holder.cb.isChecked());
                // 调整选定条目
                if (holder.cb.isChecked() == true) {
                    checkNum++;
                } else {
                    checkNum--;
                }
                // 用TextView显示
                tv_show.setText("已选中" + checkNum + "项");
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_selectall:
                for (int i = 0; i < list.size(); i++) {
                    GroupAdapter.getIsSelected().put(i, true);
                }
                // 数量设为list的长度
                checkNum = list.size();
                // 刷新listview和TextView的显示
                dataChanged();break;
            case R.id.btn_confirm:
                // 遍历list的长度，将已选的加入list
                final ArrayList<String> pids = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (GroupAdapter.getIsSelected().get(i)) {
                        pids.add(list.get(i));
                    }
                }
                pids.add(ChatHelper.getInstance().getUsername());
                final String[] array=new String[pids.size()];
                //测试广播HMAC
                ChatHelper.getInstance().setLauncher(ChatHelper.getInstance().getUsername());
                String sid = getRandomString(8);
                Log.d(TAG, "onClick: sid " + sid);
                String action="exchange";//action可以自定义
                Session session = new Session();
                session.setSid(sid);
                session.setPids(pids);
                session.setMsk(ChatHelper.getInstance().getMsk().getAlpha().toBytes());
                Log.d(TAG, "DataBase: msk : " + ChatHelper.getInstance().getMsk().getAlpha().toBytes());
                ChatApplication.getInstance().getDaoSession().insert(session);
                int position = 0;
                for (int i = 0; i < pids.size(); i++) {
                    if (!pids.get(i).equals(ChatHelper.getInstance().getUsername())) {
                        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
                        cmdMsg.addBody(cmdBody);
                        cmdMsg.setAttribute("sid", sid);
                        cmdMsg.setAttribute("pids", new Gson().toJson(pids));
                        cmdMsg.setAttribute("pid", ChatHelper.getInstance().getUsername());
                        Log.d(TAG, "onClick: pids" + pids);
                        try {
                            cmdMsg.setAttribute("msk", new String(ChatHelper.getInstance().getMsk().getAlpha().toBytes(), "ISO-8859-1"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "----------------------" + pids.get(i));
                        cmdMsg.setTo(pids.get(i));
                        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                        Hint hint = new Hint();
                        hint.setBeta(ChatHelper.getInstance().getParams().getE().getZr().newRandomElement());
                        hint.setHint_first(ChatHelper.getInstance().getParams().getG().powZn(hint.getBeta()));
                        Message message = ChatHelper.getInstance().createMessage(sid, hint);
                        Signature signature = ChatHelper.getInstance().createSignature(1, message, hint);
                        try {
                            ChatHelper.getInstance().roundOne(message, pids, signature, i);
                            Log.d(TAG, "exchange sent : " + pids.get(i));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "SLEEP throw e: "+ e.toString());
                                    e.printStackTrace();
                                }
                                if (ChatHelper.getInstance().isFinished()) {
                                    android.os.Message msg = android.os.Message.obtain();
                                    msg.what = 1;
                                    msg.obj = ChatHelper.getInstance().getGroupName();
                                    handler.sendMessage(msg);
                                }
                            }
                        }).start();
//                        try {
//                            long startTime = System.currentTimeMillis(); //起始时间
//                            ChatHelper.getInstance().roundOne(message,pids,signature,i - 1);
//                            ChatHelper.getInstance().roundOne(message,pids,signature,i + 1);
//                            long endTime = System.currentTimeMillis(); //结束时间
//                            long runTime = endTime - startTime;
//                            Log.i("timer", String.format("方法使用时间 %d ms", runTime));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    } else {
                        position = i;
                    }

                }
                break;
            case R.id.btn_deselectall:
                // 遍历list的长度，将已选的按钮设为未选
                for (int i = 0; i < list.size(); i++) {
                    if (GroupAdapter.getIsSelected().get(i)) {
                        GroupAdapter.getIsSelected().put(i, false);
                        checkNum--;// 数量减1
                    }
                }
                // 刷新listview和TextView的显示
                dataChanged();break;
        }
    }

    // 初始化数据
    private void initDate() {
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        String pidString = bundle.getString("pids");
        List<Pid> pidList = new Gson().fromJson(pidString, new TypeToken<List<Pid>>() {}.getType());
        for (int i = 0; i < pidList.size(); i++) {
            list.add(pidList.get(i).getPid());
        }
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    // 刷新listview和TextView的显示
    private void dataChanged() {
        // 通知listView刷新
        groupAdapter.notifyDataSetChanged();
        // TextView显示最新的选中数目
        tv_show.setText("已选中" + checkNum + "项");
    }
}
