package com.roomies.android.informations.users;

import org.json.JSONException;
import org.json.JSONObject;

public class MembersInfo {
    // TODO Members must be private, with getters and setters
    public String firstName;
    public String lastName;
    public String city;
    public String state;
    public String createdAt;
    public String email;
    public String id;

    public MembersInfo() {
    }

    public MembersInfo(JSONObject json) throws JSONException {
        JSONObject profile = json.getJSONObject("profile");
        firstName = profile.getString("firstName");
        lastName = profile.getString("lastName");
        city = profile.getString("city");
        state = profile.getString("state");

        createdAt = json.getString("createdAt");
        email = json.getJSONObject("local").getString("email");
        id = json.getString("id");
        // TODO createdAt
    }
}
