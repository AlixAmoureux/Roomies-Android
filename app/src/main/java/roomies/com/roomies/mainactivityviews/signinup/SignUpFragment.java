package roomies.com.roomies.mainactivityviews.signinup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.ImageView;

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

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.SecondActivity;
import roomies.com.roomies.mainactivityviews.managecoloc.JointOrCreateColocFragment;

import static android.app.Activity.RESULT_OK;
import static roomies.com.roomies.R.id.error_message;


public class SignUpFragment extends Fragment {

    private Button sign_up_finish_button;
    private ImageButton signup_image;
    private static int RESULT_LOAD_IMAGE = 1;

    private String imagePath;
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

        signup_image = (ImageButton) v.findViewById(R.id.signup_photo);
        lastName = (EditText) v.findViewById(R.id.signup_lastname);
        firstName = (EditText) v.findViewById(R.id.signup_fistname);
        email = (EditText) v.findViewById(R.id.signup_email);
        pass = (EditText) v.findViewById(R.id.signup_password);
        passConfirmed = (EditText) v.findViewById(R.id.signup_password_confirm);
        city = (EditText ) v.findViewById(R.id.signup_city);
        state = (EditText) v.findViewById(R.id.signup_state);

        imagePath = null;
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sign_up_finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (/*checkFields() == */true) {

                    valFirstName = "alix";
                    valLastName = "Amoureux";
                    valEmail = "alix.amoureux@epitech.eu";
                    valPass = "123456";
                    valPassConfirmed = "123456";
                    valCity = "Lille";
                    valState = "France";

                    getDatas();
                    Fragment newFragment = new JointOrCreateColocFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        signup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
                //startActivity(intent);
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            signup_image.setImageBitmap(bmp);

        }

    }


    private int checkFields()
    {
        valLastName =  this.lastName.getText().toString();
        valFirstName = this.firstName.getText().toString();
        valEmail = this.email.getText().toString();
        valPass = this.pass.getText().toString();
        valPassConfirmed = this.passConfirmed.getText().toString();
        valCity = this.city.getText().toString();
        valState = this.state.getText().toString();

        if (TextUtils.isEmpty(valFirstName))
        {
        firstName.setError("Le nom n'est pas indiqué");
        }
        else if (TextUtils.isEmpty(valLastName)) {
            lastName.setError("Le prénom n'est pas indiqué");
        }
        else if (TextUtils.isEmpty(valEmail)) {
            email.setError("L'adresse mail n'est pas indiquée");
        }
        else if (!isEmailValid(valEmail))
        {
            email.setError("Adresse mail incorrecte");
        }
        else if (TextUtils.isEmpty(valPass))
        {
            pass.setError("Le mot de passe n'est pas indiqué");
        }
        else if (isPasswordValid(valPass) == false)
        {
            pass.setError("Le mot de passe doit contenir au moins 6 caractères");
        }
        else if (TextUtils.isEmpty(valPassConfirmed)) {
            passConfirmed.setError("Le mot de passe n'est pas indiqué");
        }
        else if (isPasswordValid(valPassConfirmed) == false)
        {
            passConfirmed.setError("Le mot de passe doit contenir au moins 6 caractères");
        }
        else if (valPass.compareTo(valPassConfirmed) != 0)
        {
            passConfirmed.setError("Le mot de passe n'est pas identique");
        }
        else if (TextUtils.isEmpty(valCity))
        {
            city.setError("La ville n'est pas indiquée");
        }
        else if (TextUtils.isEmpty(valState))
        {
            state.setError("Le pays n'est pas indiqué");
        }
        else
            return (1);
        return (-1);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
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

    private void getDatas()
    {
        // HTTP POST

        String url = null;
        RequestQueue requestQueue = null;
        try {
            url = getString(R.string.url_base) + "/api/users";
            requestQueue = Volley.newRequestQueue(getActivity());
        }
        catch (Exception e)
        {
            Log.e("Error",  e.getMessage());
        }
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject local =  new JSONObject();
            local.put("email", valEmail);
            local.put("password", valPass);
            local.put("password_confirmation", valPassConfirmed);


            JSONObject profile =  new JSONObject();
            profile.put("firstName", valFirstName);
            profile.put("lastName", valLastName);
            profile.put("city", valCity);
            profile.put("state", valState);

            jsonObject.put("profile", profile.toString());
            jsonObject.put("local", local.toString());
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            String token = response.getString("token");
                            final SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            prefs.putString("token", token);
                            prefs.apply();
                        }
                        catch (JSONException e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 500) {
                    email.setError("Veuillez choisir une autre adresse email");
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Sign-Up");
    }
}
