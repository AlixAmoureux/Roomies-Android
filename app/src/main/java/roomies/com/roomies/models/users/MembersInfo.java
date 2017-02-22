package roomies.com.roomies.models.users;

import org.json.JSONException;
import org.json.JSONObject;

public class MembersInfo {
    public String firstName;
    public String lastName;
    public String city;
    public String state;
    public String createdAt;
    public String email;
    public String id;


    public MembersInfo()
    {

    }

    public MembersInfo(JSONObject json) throws JSONException
    {
        try {
            JSONObject profile = json.getJSONObject("profile");
            this.firstName = profile.getString("firstName");
            this.lastName = profile.getString("lastName");
            this.createdAt = json.getString("createdAt");
            this.id = json.getString("id");

            try {
                this.city = profile.getString("city");
            }
            catch (JSONException e)
            {
                this.city = "";
            }
            try
            {
                this.state = profile.getString("state");
            }
            catch (JSONException e)
            {
                this.state = "";
            }
            try
            {
                this.email = json.getJSONObject("local").getString("email");
            }
            catch (JSONException e)
            {
                this.email = json.getJSONObject("facebook").getString("email");
            }

        }
        catch (JSONException e)
        {

        }
        // TODO createdAt
    }
}
