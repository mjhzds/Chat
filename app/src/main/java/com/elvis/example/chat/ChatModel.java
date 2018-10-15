package com.elvis.example.chat;

import android.content.Context;

import com.elvis.example.chat.utils.PreferenceManager;

public class ChatModel {
    private Context context = null;

    public ChatModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }
}
