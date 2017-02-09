package roomies.com.roomies.informations.users;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectedUserInfo extends MembersInfo {

    public String token;

    public ConnectedUserInfo(JSONObject json) throws JSONException {
        super(json.getJSONObject("user"));
    }
}
