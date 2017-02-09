package com.roomies.android.mainactivityviews.signinup;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.ManageObjects;
import com.roomies.android.R;
import com.roomies.android.SecondActivity;
import com.roomies.android.informations.users.ConnectedUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";

    private Button mSignUpButton;
    private Button mSignInButton;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mErrorMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignUpButton = (Button) view.findViewById(R.id.sign_up_button);
        mSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        mEmailField = (EditText) view.findViewById(R.id.email);
        mPasswordField = (EditText) view.findViewById(R.id.password);
        mErrorMessage = (TextView) view.findViewById(R.id.error_message);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new SignUpFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorMessage.setVisibility(View.INVISIBLE);
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                checkFields(password, email);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sign-In");
    }

    private void checkFields(final String password, final String email) {
        boolean cancel = false;
        boolean emailProblem = false;

        // Check for a valid password, if the user entered one.
        /*if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Le mot de passe n'est pas indiqué");
            cancel = true;
        }
        else if (isPasswordValid(password) == false)
        {
            mPasswordField.setError("Le mot de passe doit contenir au moins 6 caractères");
            cancel = true;
        }
        // Check for a valid mEmailField address.
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("L'mEmailField n'est pas indiqué");
            emailProblem = true;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            mEmailField.setError("Adresse mail incorrecte");
            emailProblem = true;
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (emailProblem == true)
                mEmailField.requestFocus();
            else
                 mPasswordField.requestFocus();
        }
        else {*/
        extractData("alix.amoureux@epitech.eu", "123456");
        //}
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private void extractData(final String user, final String pass) {
        String url = getString(R.string.url_base) + "/api/auth/login";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject postData = new JSONObject();

        try {
            postData.put("username", user);
            postData.put("mPasswordField", pass);
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create POST data", e);
            return;
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            ConnectedUserInfo user = new ConnectedUserInfo(response);
                            user.token = token;
                            ManageObjects.writeObjectInPrefs(getContext(), user, "userInfo");
                            Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
                            startActivity(toSecondActivity);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error while parsing JSON response.", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: server responded with status "
                                + response.statusCode);

                        if (response.statusCode == 401) {
                            mErrorMessage.setVisibility(View.VISIBLE);
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
        requestQueue.add(postRequest);
    }
}