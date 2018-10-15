package com.elvis.example.chat.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.elvis.example.chat.ChatHelper;
import com.elvis.example.chat.R;
import com.elvis.example.chat.bean.Pid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class MainActivity extends FragmentActivity {

    private Fragment fragment[] = new Fragment[3];
    private static final String TAG = "MainActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment[0] = new ChatFragment();
        fragment[1] = new GroupFragment();
        fragment[2] = new OtherFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment[0])
                .add(R.id.container, fragment[1])
                .add(R.id.container, fragment[2]).commit();

        chatFragment(null);

        requestPower();
    }

    public void chatFragment(View view) {
        getSupportFragmentManager().beginTransaction()
                .hide(fragment[1])
                .hide(fragment[2])
                .show(fragment[0]).commit();
    }

    public void groupFragment(View view){
        getSupportFragmentManager().beginTransaction()
                .hide(fragment[0])
                .hide(fragment[2])
                .show(fragment[1]).commit();
    }

    public void otherFragment(View view){
        getSupportFragmentManager().beginTransaction()
                .hide(fragment[1])
                .hide(fragment[0])
                .show(fragment[2]).commit();
    }

    public void requestPower() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
