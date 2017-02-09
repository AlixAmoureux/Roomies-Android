package roomies.com.roomies.controlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import roomies.com.roomies.models.users.ConnectedUserInfo;


public class ManageObjects {

    // ConnectedUserInfo
    public static boolean writeObjectInPrefs(ConnectedUserInfo object, String objName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
    public static ConnectedUserInfo readUserInfosInPrefs(String objName, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        GsonBuilder gsonB = new GsonBuilder();
        Gson gson = gsonB.create();

        String loadValue = prefs.getString(objName, "");
        ConnectedUserInfo value = gson.fromJson(loadValue, ConnectedUserInfo.class);
        return (value);
    }

}
