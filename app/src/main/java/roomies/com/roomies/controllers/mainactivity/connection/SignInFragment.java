package roomies.com.roomies.controllers.mainactivity.connection;


import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.secondactivity.SecondActivity;
import roomies.com.roomies.models.users.ConnectedUserInfo;

public class SignInFragment extends Fragment {
    private Button sign_up;
    private Button sign_in;
    EditText email;
    EditText password;
    TextView error_message;
    private ImageButton m_logInFb;
    private CallbackManager callbackManager;
    private Fragment curFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        callbackManager = CallbackManager.Factory.create();
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        sign_up = (Button) v.findViewById(R.id.sign_up_button);
        sign_in = (Button) v.findViewById(R.id.sign_in_button);

        email = (EditText) v.findViewById(R.id.email);
        password = (EditText) v.findViewById(R.id.password);

        error_message = (TextView) v.findViewById(R.id.error_message);

        m_logInFb = (ImageButton) v.findViewById(R.id.sign_in_facebook);
        curFragment = this;
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        m_logInFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login Callback registration
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String token = loginResult.getAccessToken().getToken();
                        connectFb(token);
                    }
                    @Override
                    public void onCancel() {
                        Log.e("onCancel", "login canceled");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("onError fb", exception.getMessage() + exception.getLocalizedMessage());
                    }
                });
                LoginManager.getInstance().logInWithReadPermissions(curFragment, Arrays.asList("public_profile", "email")); //Log in to FB
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SignUpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_message.setVisibility(View.INVISIBLE);
                String email_value = email.getText().toString();
                String password_value = password.getText().toString();
                checkFields(password_value, email_value);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void checkFields(final String password_value, final String email_value)
    {
        boolean cancel = false;
        boolean email_problem = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password_value)) {
            password.setError("Le mot de passe n'est pas indiqué");
            cancel = true;
        }
        else if (isPasswordValid(password_value) == false)
        {
            password.setError("Le mot de passe doit contenir au moins 6 caractères");
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email_value)) {
            email.setError("L'email n'est pas indiqué");
            email_problem = true;
            cancel = true;
        }
        else if (!isEmailValid(email_value))
        {
            email.setError("Adresse mail incorrecte");
            email_problem = true;
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (email_problem == true)
                email.requestFocus();
            else
                 password.requestFocus();
        }
        else {
            connect(email_value, password_value);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sign-In");
    }

    private void connect(final String user, final String pass)
    {
        // HTTP POST
        String url = getString(R.string.url_base) +  "/api/auth/login";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user);
            jsonObject.put("password", pass);
        }
        catch(JSONException e) {
            Log.e("Volley", e.getMessage());
        }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>()
                {

                @Override
                public void onResponse(JSONObject response)
                {
                    try {
                        String token = response.getString("token");
                        ConnectedUserInfo user = new ConnectedUserInfo(response);
                        user.token = token;
                        ManageObjects.writeUserInfosInPrefs(user, "userInfos", getActivity());
                        Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
                        startActivity(toSecondActivity);
                    }
                    catch (JSONException e)
                    {
                        Log.e("ERROR VOLLEY", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.statusCode == 401) {
                            //error_message.setVisibility(View.VISIBLE);
                    }
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        }

    private void connectFb(final String token)
    {
        // HTTP POST
        String url = getString(R.string.url_base) +  "/api/auth/facebook/token";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", token);
        }
        catch(JSONException e) {
            Log.e("Volley", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            String token = response.getString("token");
                            JSONObject user = response.getJSONObject("user");
                            String firstName = user.getJSONObject("profile").getString("firstName");
                            String lastName = user.getJSONObject("profile").getString("lastName");
                            String createdAt = user.getString("createdAt");
                            String email = user.getJSONObject("facebook").getString("email");
                            String id = user.getString("id");

                            ConnectedUserInfo tmpUser = new ConnectedUserInfo();
                            tmpUser.token = token;
                            tmpUser.firstName = firstName;
                            tmpUser.lastName = lastName;
                            tmpUser.email = email;
                            tmpUser.id = id;
                            tmpUser.createdAt = createdAt;

                            ManageObjects.writeUserInfosInPrefs(tmpUser, "userInfos", getActivity());
                            Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
                            startActivity(toSecondActivity);
                        }
                        catch (JSONException e)
                        {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    error_message.setVisibility(View.VISIBLE);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    }