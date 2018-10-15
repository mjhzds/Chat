/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elvis.example.chat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.elvis.example.chat.ChatHelper;
import com.elvis.example.chat.Constant;
import com.elvis.example.chat.bean.KeyPair;
import com.elvis.example.chat.bean.Msk;
import com.elvis.example.chat.bean.Params;
import com.elvis.example.chat.bean.Pid;
import com.google.gson.Gson;
import com.elvis.example.chat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 * Login screen
 * 
 */
public class LoginActivity extends AppCompatActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private String currentUsername;
	private String currentPassword;

	private String value;

	private boolean progressShow;
	private boolean autoLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		usernameEditText = findViewById(R.id.username);
		passwordEditText = findViewById(R.id.password);

		// if user changed, clear the password
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * login
	 * 
	 * @param view
	 */
	public void login(View view) {
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		progressShow = true;

		getValue();
	}

	/**
	 * register
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void getValue() {
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... strings) {
				String urlString = strings[0];
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				List<BasicNameValuePair> list = new ArrayList<>();
				list.add(new BasicNameValuePair("account", currentUsername));
                Log.d(TAG, "---------------" + currentUsername);
				list.add(new BasicNameValuePair("password", currentPassword));
				try {
					post.setEntity(new UrlEncodedFormEntity(list));
					HttpResponse response = client.execute(post);
					String value = EntityUtils.toString(response.getEntity());
					Log.d(TAG, value);
					Gson gson = new Gson();
					JSONObject jsonObject = new JSONObject(value);
                    int g_field = jsonObject.getInt("g_field");
                    int h_field = jsonObject.getInt("h_field");
                    int alpha_field = jsonObject.getInt("alpha_field");
                    int pub_field = jsonObject.getInt("pub_field");
                    int prv_field = jsonObject.getInt("prv_field");
                    int msk_field = jsonObject.getInt("msk_field");
					int msg = jsonObject.getInt("msg");
					final Pid pid = gson.fromJson(jsonObject.getString("pid"),Pid.class);
					final String pidList = jsonObject.getString("pids");
					String pairing_desc = jsonObject.getString("pairing_desc");
					byte[] g = jsonObject.getString("g").getBytes("ISO-8859-1");
					byte[] h = jsonObject.getString("h").getBytes("ISO-8859-1");
					byte[] alpha = jsonObject.getString("alpha").getBytes("ISO-8859-1");
					byte[] pub = jsonObject.getString("key_pub").getBytes("ISO-8859-1");
					byte[] prv = jsonObject.getString("key_prv").getBytes("ISO-8859-1");
					byte[] mskString = jsonObject.getString("msk").getBytes("ISO-8859-1");
					Params params = new Params();
					Pairing pairing = PairingFactory.getPairing("assets/a.properties");
					PairingFactory.getInstance().setUsePBCWhenPossible(true);
					params.setPairing_desc(pairing_desc);
					params.setG(pairing.getFieldAt(g_field).newElementFromBytes(g));
					params.setH(pairing.getFieldAt(h_field).newElementFromBytes(h));
					params.setHat_alpha(pairing.getFieldAt(alpha_field).newElementFromBytes(alpha));
					params.setE(pairing);
					Msk msk = new Msk();
					msk.setAlpha(pairing.getFieldAt(msk_field).newElementFromBytes(mskString));
					KeyPair keyPair = new KeyPair();
					keyPair.setPub(pairing.getFieldAt(pub_field).newElementFromBytes(pub));
					keyPair.setPrv(pairing.getFieldAt(prv_field).newElementFromBytes(prv));
					ChatHelper.getInstance().setKeyPair(keyPair);
					ChatHelper.getInstance().setParams(params);
					ChatHelper.getInstance().setMsk(msk);
					System.out.println("*****************************************************************************");
					System.out.println("setup--output: pairing desc is " + params.getPairing_desc());
					System.out.println("setup--output: pairing g is " + params.getG());
					System.out.println("setup--output: pairing h is " + params.getH());
					System.out.println("setup--output: g hat alpha is " + params.getHat_alpha());
					System.out.println("setup--output: msk key is " + msk.getAlpha());
					System.out.println("*****************************************************************************");
//                    ActivityCompat.requestPermissions(LoginActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1);

					if (msg == Constant.SUCCESSCODE_LOGIN) {
						Log.d(TAG, "---------------------登录成功!------------------------");
						// reset current user name before login

						// call login method
						Log.d(TAG, "EMClient.getInstance().login");
						EMClient.getInstance().login(pid.getPid(), currentPassword, new EMCallBack() {

							@Override
							public void onSuccess() {
								Log.d(TAG, "login: onSuccess");


								// ** manually load all local groups and conversation
								EMClient.getInstance().groupManager().loadAllGroups();
								EMClient.getInstance().chatManager().loadAllConversations();
								ChatHelper.getInstance().setUsername(pid.getPid());
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("pids", pidList);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							}

							@Override
							public void onProgress(int progress, String status) {
								Log.d(TAG, "login: onProgress");
							}

							@Override
							public void onError(final int code, final String message) {
								Log.d(TAG, "login: onError: " + code);
								if (!progressShow) {
									return;
								}
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
					} else {
						Log.d(TAG, "登录失败！ 代码：" + msg);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(Constant.URL_LOGIN);
	}
}
