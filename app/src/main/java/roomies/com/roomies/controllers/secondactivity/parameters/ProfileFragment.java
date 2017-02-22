package roomies.com.roomies.controllers.secondactivity.parameters;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.models.users.ConnectedUserInfo;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    private static int RESULT_LOAD_IMAGE = 1;


    private Button m_updateButton;
    private EditText m_lastName;
    private EditText m_firstName;
    private EditText m_email;
    private EditText m_city;
    private EditText m_state;
    private TextView m_message;

    private String m_valLastName;
    private String m_valFirstName;
    private String m_valEmail;
    private String m_valCity;
    private String m_valState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        m_updateButton = (Button) v.findViewById(R.id.update_profile_button);
        m_lastName = (EditText) v.findViewById(R.id.update_lastname);
        m_firstName = (EditText) v.findViewById(R.id.update_fistname);
        m_email = (EditText) v.findViewById(R.id.update_email);
        m_city = (EditText) v.findViewById(R.id.update_city);
        m_state = (EditText) v.findViewById(R.id.update_state);
        m_message = (TextView) v.findViewById(R.id.update_profile_message);

        Log.e("Profile", "onCreateView");
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e("Profile", "onActivityCreated");

        setDatasAndGetEvents();
    }

    private void setDatasAndGetEvents() {
        final ConnectedUserInfo user = ManageObjects.readUserInfosInPrefs("userInfos", getActivity());

        m_lastName.setText(user.lastName);
        m_firstName.setText(user.firstName);
        m_email.setText(user.email);
        m_state.setText(user.state);
        m_city.setText(user.city);

        m_updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields() != -1)
                    updateObject(user);
            }
        });
    }


    private void updateObject(ConnectedUserInfo oldUser) {
        ConnectedUserInfo newUser = new ConnectedUserInfo();
        newUser.token = oldUser.token;
        newUser.id = oldUser.id;
        newUser.city = m_valCity;
        newUser.createdAt = oldUser.createdAt;
        newUser.email = m_valEmail;
        newUser.firstName = m_valFirstName;
        newUser.lastName = m_valLastName;
        newUser.state = m_valState;
        ManageObjects.writeUserInfosInPrefs(newUser, "userInfos", getActivity());
        updateProfile(newUser.id, newUser.token);
    }

    private int checkFields() {
        m_valLastName = this.m_lastName.getText().toString();
        m_valFirstName = this.m_firstName.getText().toString();
        m_valEmail = this.m_email.getText().toString();
        m_valCity = this.m_city.getText().toString();
        m_valState = this.m_state.getText().toString();

        if (TextUtils.isEmpty(m_valFirstName)) {
            m_firstName.setError("The firstname is not set");
        } else if (TextUtils.isEmpty(m_valLastName)) {
            m_lastName.setError("The lastname is not set");
        } else if (TextUtils.isEmpty(m_valEmail)) {
            m_email.setError("The email is not set");
        } else if (!isEmailValid(m_valEmail)) {
            m_email.setError("The email is not correct");
        } else if (TextUtils.isEmpty(m_valCity)) {
            m_city.setError("The city is not set");
        } else if (TextUtils.isEmpty(m_valState)) {
            m_state.setError("The state is not set");
        } else
            return (1);
        return (-1);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Profile", "onResume");
        getActivity().setTitle("Sign-Up");
    }

    private void updateProfile(String userId, final String userToken) {
        // HTTP POST
        String url = getString(R.string.url_base) + "/api/users/" + userId;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject local = new JSONObject();
            local.put("email", m_valEmail);
            JSONObject profile = new JSONObject();
            profile.put("firstName", m_valFirstName);
            profile.put("lastName", m_valLastName);
            profile.put("city", m_valCity);
            profile.put("state", m_valState);
            jsonObject.put("profile", profile.toString());
            jsonObject.put("local", local.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        m_message.setText(getString(R.string.update_profile_success));
                        m_message.setTextColor(getResources().getColor(R.color.messageSuccess));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update Profile", error.getMessage());
                m_message.setText(getString(R.string.update_profile_error));
                m_message.setTextColor(getResources().getColor(R.color.messageError));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + userToken);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
