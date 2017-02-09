package com.roomies.android.informations;

import org.json.JSONException;
import org.json.JSONObject;

public class ColocsInfos
{
    public String title;
    public String description;
    public String picturePath;
    public String createdAt;
    public String id;

    public ColocsInfos(JSONObject json) throws JSONException
    {
        try {

            this.picturePath = json.getString("picturePath");

        }
        catch (JSONException e)
        {
            this.picturePath = "@drawable/unknown_user.png";
        }
        this.title = json.getString("title");
        this.id = json.getString("id");
        this.description = json.getString("description");

        // TODO createdAt
    }


}
