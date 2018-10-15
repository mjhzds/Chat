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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.elvis.example.chat.Constant;
import com.elvis.example.chat.ChatHelper;
import com.elvis.example.chat.R;
import com.elvis.example.chat.bean.Pid;
import com.hyphenate.exceptions.HyphenateException;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * register screen
 * 
 */
public class RegisterActivity extends AppCompatActivity {

	String TAG = "RegisterActivity";
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private String pid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
	}

	public void register(View view) {
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {

			new Thread(new Runnable() {
				public void run() {
					// call method in SDK
					getValue(username,pwd);
				}
			}).start();

		}
	}

	public void back(View view) {
		finish();
	}

	private void getValue(final String account, final String password) {

		new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... strings) {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(Constant.URL_REGISTER);
				List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
				list.add(new BasicNameValuePair("account", account));
				list.add(new BasicNameValuePair("password", password));
				try {
					post.setEntity(new UrlEncodedFormEntity(list));
					HttpResponse response = client.execute(post);
					String value = EntityUtils.toString(response.getEntity());
					Log.d(TAG, value);
					JSONObject jsonObject = new JSONObject(value);
					Gson gson = new Gson();
					int msg = jsonObject.getInt("msg");
					String id = jsonObject.getString("pid");
					final Pid pid = gson.fromJson(id, Pid.class);
					if (msg==Constant.SUCCESSCODE_REGISTER) {
						Log.d(TAG, "---------------------注册成功!------------------------" + account + password);
						EMClient.getInstance().createAccount(pid.getPid(), password);
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
								// save current user
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
								finish();
							}
						});
					} else {
						Log.d(TAG, "注册失败！ 代码：" + msg);
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(Constant.URL_REGISTER);
	}
}
