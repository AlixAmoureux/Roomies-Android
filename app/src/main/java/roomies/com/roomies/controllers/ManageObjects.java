package roomies.com.roomies.controllers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import roomies.com.roomies.models.ColocsInfos;
import roomies.com.roomies.models.users.ConnectedUserInfo;


public class ManageObjects {

    // ConnectedUserInfo
    public static boolean writeUserInfosInPrefs(ConnectedUserInfo object, String objName, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        GsonBuilder gsonB = new GsonBuilder();
        Gson gson = gsonB.create();

        try {
            String writeValue = gson.toJson(object);
            editor.putString(objName, writeValue);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ConnectedUserInfo
    public static ConnectedUserInfo readUserInfosInPrefs(String objName, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        GsonBuilder gsonB = new GsonBuilder();
        Gson gson = gsonB.create();

        String loadValue = prefs.getString(objName, "");
        ConnectedUserInfo value = gson.fromJson(loadValue, ConnectedUserInfo.class);
        return (value);
    }

    // ColocInfos
    public static ColocsInfos readColocInfosInPrefs(String objName, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        GsonBuilder gsonB = new GsonBuilder();
        Gson gson = gsonB.create();

        String loadValue = prefs.getString(objName, "");
        ColocsInfos value = gson.fromJson(loadValue, ColocsInfos.class);
        return (value);
    }

    // ColocInfos
    public static boolean writeColocInfosInPrefs(ColocsInfos object, String objName, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        GsonBuilder gsonB = new GsonBuilder();
        Gson gson = gsonB.create();

        try {
            String writeValue = gson.toJson(object);
            editor.putString(objName, writeValue);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}