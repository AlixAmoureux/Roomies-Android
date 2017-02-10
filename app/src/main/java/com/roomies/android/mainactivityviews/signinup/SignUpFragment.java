package com.roomies.android.mainactivityviews.signinup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.R;
import com.roomies.android.mainactivityviews.managecoloc.JoinOrCreateColocFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SignUpFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "SignUpFragment";

    private Button mSignUpButton;
    private ImageButton mContactPhoto;

    private EditText mLastnameField;
    private EditText mFirstnameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPwdConfirmField;
    private EditText mCityField;
    private EditText mStateField;

    private String mImagePath = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {Media.DATA};

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mImagePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mContactPhoto.setImageBitmap(bmp);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignUpButton = (Button) view.findViewById(R.id.sign_up_finish_button);
        mContactPhoto = (ImageButton) view.findViewById(R.id.signup_photo);
        mLastnameField = (EditText) view.findViewById(R.id.signup_lastname);
        mFirstnameField = (EditText) view.findViewById(R.id.signup_fistname);
        mEmailField = (EditText) view.findViewById(R.id.signup_email);
        mPasswordField = (EditText) view.findViewById(R.id.signup_password);
        mPwdConfirmField = (EditText) view.findViewById(R.id.signup_password_confirm);
        mCityField = (EditText) view.findViewById(R.id.signup_city);
        mStateField = (EditText) view.findViewById(R.id.signup_state);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (/*checkFields() == */true) {

                    // TODO Ne plus hardcoder les valeurs de connexion
                    mFirstnameField.setText("alix");
                    mLastnameField.setText("Amoureux");
                    mEmailField.setText("alix.amoureux@epitech.eu");
                    mPasswordField.setText("123456");
                    mPwdConfirmField.setText("123456");
                    mCityField.setText("Lille");
                    mStateField.setText("France");

                    getDatas();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new JoinOrCreateColocFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        mContactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePictureActivity = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(choosePictureActivity, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sign-Up");
    }

    private boolean checkFields() {
        CharSequence lastname = mLastnameField.getText();
        CharSequence firstname = mFirstnameField.getText();
        CharSequence email = mEmailField.getText();
        CharSequence password = mPasswordField.getText();
        CharSequence confirmation = mPwdConfirmField.getText();
        CharSequence city = mCityField.getText();
        CharSequence state = mStateField.getText();

        if (TextUtils.isEmpty(firstname)) {
            mFirstnameField.setError("Le nom n'est pas indiqué");
        } else if (TextUtils.isEmpty(lastname)) {
            mLastnameField.setError("Le prénom n'est pas indiqué");
        } else if (TextUtils.isEmpty(email)) {
            mEmailField.setError("L'adresse mail n'est pas indiquée");
        } else if (!isEmailValid(email)) {
            mEmailField.setError("Adresse mail incorrecte");
        } else if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Le mot de passe n'est pas indiqué");
        } else if (!isPasswordValid(password)) {
            mPasswordField.setError("Le mot de passe doit contenir au moins 6 caractères");
        } else if (TextUtils.isEmpty(confirmation)) {
            mPwdConfirmField.setError("Le mot de passe n'est pas indiqué");
        } else if (!isPasswordValid(confirmation)) {
            mPwdConfirmField.setError("Le mot de passe doit contenir au moins 6 caractères");
        } else if (TextUtils.equals(password, confirmation)) {
            mPwdConfirmField.setError("Le mot de passe n'est pas identique");
        } else if (TextUtils.isEmpty(city)) {
            mCityField.setError("La ville n'est pas indiquée");
        } else if (TextUtils.isEmpty(state)) {
            mStateField.setError("Le pays n'est pas indiqué");
        } else
            return true;
        return false;
    }

    private boolean isEmailValid(CharSequence email) {
        //TODO: Replace this with your own logic
        return email != null && email.toString().contains("@");
    }

    private boolean isPasswordValid(CharSequence password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void getDatas() {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = getString(R.string.url_base) + "/api/users";

        // HTTP POST
        JSONObject postData = new JSONObject();
        try {
            JSONObject local = new JSONObject();
            local.put("email", mEmailField.getText());
            local.put("password", mPasswordField.getText());
            // FIXME Password confirmation should operate on client side only.
            local.put("password_confirmation", mPwdConfirmField.getText());

            JSONObject profile = new JSONObject();
            profile.put("firstName", mFirstnameField.getText());
            profile.put("lastName", mLastnameField.getText());
            profile.put("city", mCityField.getText());
            profile.put("state", mStateField.getText());

            // FIXME Plutôt ajouter chaque objet en tant que JSONObject au lieu de String ?
            postData.put("profile", profile.toString());
            postData.put("local", local.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create POST data.", e);
            return;
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            prefs.edit().putString("token", token).apply();
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: cannot parse JSON response.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                Log.e(TAG, "onErrorResponse: server returned an error with code "
                        + response.statusCode, error);
                if (response.statusCode == 500) {
                    // FIXME HTTP Code 500 is never expected. Fix the server code !
                    mEmailField.setError("Veuillez choisir une autre adresse email");
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
