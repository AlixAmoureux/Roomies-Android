package com.roomies.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.roomies.android.informations.users.ConnectedUserInfo;


public class ManageObjects {

    private static Gson sGson = new GsonBuilder().create();

    public static boolean writeObjectInPrefs(Context context, ConnectedUserInfo object, String objName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        String jsonString = sGson.toJson(object);
        editor.putString(objName, jsonString);
        editor.apply();
        return true;
    }

    public static ConnectedUserInfo readUserInfosInPrefs(Context context, String objName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = prefs.getString(objName, "");
        ConnectedUserInfo value = sGson.fromJson(jsonString, ConnectedUserInfo.class);
        return value;
    }

}
