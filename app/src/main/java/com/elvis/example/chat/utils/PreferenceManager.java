package com.elvis.example.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class PreferenceManager {

    private static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static PreferenceManager mPreferencemManager;
    private static SharedPreferences.Editor editor;

    private PreferenceManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt){
        if(mPreferencemManager == null){
            mPreferencemManager = new PreferenceManager(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @param
     * @return
     */
    public synchronized static PreferenceManager getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }

        return mPreferencemManager;
    }

    public void setCurrentUserName(String username){
        editor.putString("currentUsername", username);
        editor.apply();
    }

    public String getCurrentUsername(){
        return mSharedPreferences.getString("currentUsername", null);
    }

    public void setSeeds(String seeds) {
        editor.putString("seeds", seeds);
        editor.apply();
    }

    public String getSeeds() {
        return mSharedPreferences.getString("seeds", null);
    }
}
