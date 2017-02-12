package roomies.com.roomies.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ColocsInfos
{
    public String title;
    public String description;
    public String picturePath;
    public String createdAt;
    public String id;
    public String status;

    public ColocsInfos()
    {

    }

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
        this.status = json.getString("description");
        this.createdAt = json.getString("createdAt");

        // TODO createdAt
    }


}
