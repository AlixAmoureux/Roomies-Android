package roomies.com.roomies.models.users;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectedUserInfo extends MembersInfo {

    public String token;

    public ConnectedUserInfo(JSONObject json) throws JSONException {
        super(json.getJSONObject("user"));
    }

    public ConnectedUserInfo()
    {

    }
}
