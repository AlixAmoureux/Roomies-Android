package com.roomies.android.informations.users;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectedUserInfo extends MembersInfo {

    // TODO Member must be private
    public String token;

    public ConnectedUserInfo(JSONObject json) throws JSONException {
        super(json.getJSONObject("user"));
    }
}
