package roomies.com.roomies.controllers.mainactivity.connection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.mainactivity.managecoloc.JointOrCreateColocFragment;
import roomies.com.roomies.models.users.ConnectedUserInfo;


public class SignUpFragment extends Fragment {

    private Button sign_up_finish_button;

    private EditText lastName;
    private EditText firstName;
    private EditText email;
    private EditText pass;
    private EditText passConfirmed;
    private EditText city;
    private EditText state;

    private String valLastName;
    private String valFirstName;
    private String valEmail;
    private String valPass;
    private String valPassConfirmed;
    private String valCity;
    private String valState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        sign_up_finish_button = (Button) v.findViewById(R.id.sign_up_finish_button);

        lastName = (EditText) v.findViewById(R.id.signup_lastname);
        firstName = (EditText) v.findViewById(R.id.signup_fistname);
        email = (EditText) v.findViewById(R.id.signup_email);
        pass = (EditText) v.findViewById(R.id.signup_password);
        passConfirmed = (EditText) v.findViewById(R.id.signup_password_confirm);
        city = (EditText) v.findViewById(R.id.signup_city);
        state = (EditText) v.findViewById(R.id.signup_state);

        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sign_up_finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkFields()) {
                    getDatas();
                    Fragment newFragment = new JointOrCreateColocFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }


    private boolean checkFields() {
        valLastName = this.lastName.getText().toString();
        valFirstName = this.firstName.getText().toString();
        valEmail = this.email.getText().toString();
        valPass = this.pass.getText().toString();
        valPassConfirmed = this.passConfirmed.getText().toString();
        valCity = this.city.getText().toString();
        valState = this.state.getText().toString();

        if (TextUtils.isEmpty(valFirstName)) {
            firstName.setError("The firstname is not set");
        } else if (TextUtils.isEmpty(valLastName)) {
            lastName.setError("The lastname is not set");
        } else if (TextUtils.isEmpty(valEmail)) {
            email.setError("The email is not set");
        } else if (!isEmailValid(valEmail)) {
            email.setError("The email is not correct");
        } else if (TextUtils.isEmpty(valPass)) {
            pass.setError("The password is not set");
        } else if (isPasswordValid(valPass) == false) {
            pass.setError("The password should have at least 6 characters");
        } else if (TextUtils.isEmpty(valPassConfirmed)) {
            passConfirmed.setError("The password is not set");
        } else if (isPasswordValid(valPassConfirmed) == false) {
            passConfirmed.setError("The password should have at least 6 characters");
        } else if (valPass.compareTo(valPassConfirmed) != 0) {
            passConfirmed.setError("The password is not the same");
        } else if (TextUtils.isEmpty(valCity)) {
            city.setError("The city is not set");
        } else if (TextUtils.isEmpty(valState)) {
            state.setError("The country is not set");
        } else
            return (true);
        return (false);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }


    private void getDatas() {
        // HTTP POST

        String url = null;
        RequestQueue requestQueue = null;
        try {
            url = getString(R.string.url_base) + "/api/users";
            requestQueue = Volley.newRequestQueue(getActivity());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject local = new JSONObject();
            local.put("email", valEmail);
            local.put("password", valPass);
            local.put("password_confirmation", valPassConfirmed);


            JSONObject profile = new JSONObject();
            profile.put("firstName", valFirstName);
            profile.put("lastName", valLastName);
            profile.put("city", valCity);
            profile.put("state", valState);

            jsonObject.put("profile", profile.toString());
            jsonObject.put("local", local.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");


                            ConnectedUserInfo user = new ConnectedUserInfo(response);
                            user.token = token;
                            ManageObjects.writeUserInfosInPrefs(user, "userInfos", getActivity());
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 500) {
                    email.setError("Choose another email");
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sign-Up");
    }
}
