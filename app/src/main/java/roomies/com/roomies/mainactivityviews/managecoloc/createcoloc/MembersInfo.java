package roomies.com.roomies.mainactivityviews.managecoloc.createcoloc;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xila on 05/02/2017.
 */

public class MembersInfo {
    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String createdAt;
    private String email;
    private String id;

    public MembersInfo(JSONObject json) throws JSONException
    {
        try {
            JSONObject profile = json.getJSONObject("profile");
            this.firstName = profile.getString("firstName");
            this.lastName = profile.getString("lastName");
            this.city = profile.getString("city");
            this.state = profile.getString("state");


            this.createdAt = json.getString("createdAt");
            this.email = json.getJSONObject("local").getString("email");
            this.id = json.getString("id");

        }
        catch (JSONException e)
        {

        }
        // TODO createdAt
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
