package com.roomies.android.mainactivityviews.managecoloc.createcoloc;


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
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CreateColocFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "CreateColocFragment";

    private EditText mColocName;
    private EditText mColocDescription;
    private ImageButton mColocImageButton;
    private Button mColocButton;
    private String mToken;
    private String mImagePath = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bmp = null;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {Media.DATA};

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImagePath = cursor.getString(columnIndex);
                cursor.close();
            }
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                Log.e(TAG, "onActivityResult: cannot get image from Uri.", e);
            }
            mColocImageButton.setImageBitmap(bmp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_coloc, container, false);
        mColocName = (EditText) v.findViewById(R.id.coloc_name);
        mColocDescription = (EditText) v.findViewById(R.id.coloc_description);
        mColocImageButton = (ImageButton) v.findViewById(R.id.create_coloc_image);
        mColocButton = (Button) v.findViewById(R.id.create_coloc_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mToken = prefs.getString("token", "");
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mColocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String colocNameVal = mColocName.getText().toString();
                String colocDescriptionVal = mColocDescription.getText().toString();
                checkFields(colocNameVal, colocDescriptionVal);
            }
        });

        mColocImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
                //startActivity(intent);
                Intent i = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Create a Roomies group");
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void checkFields(final String colocNameVal, final String colocDescVal) {
        boolean cancel = false;
        boolean nameProblem = false;

        // Check for a valid mColocName, if the user entered one.
        if (TextUtils.isEmpty(colocNameVal)) {
            mColocName.setError("Le nom de la colocation n'est pas indiquée");
            nameProblem = true;
            cancel = true;
        }

        // Check for a valid coloc description
        if (TextUtils.isEmpty(colocDescVal)) {
            mColocDescription.setError("La description de la colocation n'est pas indiquée");
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (nameProblem) {
                mColocName.requestFocus();
            } else {
                mColocDescription.requestFocus();
            }
        } else {
                /*final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + mToken);
                JSONObject body = new JSONObject();
                try {
                    body.put("title", coloc_name_val);
                    body.put("description", coloc_desc_val);
                    body.put("picturePath", mImagePath);
                }
                catch(JSONException e) {
                    Log.e("Volley", e.getMessage());
                }
                PostRequest requestPost = new PostRequest("/api/roomies-group", getActivity(), headers, body);
                requestPost.postDatas();
                JSONObject response = requestPost.getResponse();
                this.getDatas(response);*/

            createColoc(colocNameVal, colocDescVal);
            Fragment newFragment = new AddMembersFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /*private void getDatas(JSONObject response)
    {
      try {
            String coloc_id = response.getString("id");
            final SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            prefs.putString("coloc_id", coloc_id);
            prefs.apply();
        }
        catch (JSONException e)
        {
            Log.e("ERROR VOLLEY", e.getMessage());
        }
    }*/

    private void createColoc(final String coloc_name_val, final String coloc_desc_val) {
        // HTTP POST
        Log.d("createColoc", "début de fonction");
        Log.d("createColoc mToken", mToken);
        String url = getString(R.string.url_base) + "/api/roomies-group";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", coloc_name_val);
            jsonObject.put("description", coloc_desc_val);
            jsonObject.put("picturePath", mImagePath);
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing JSON", e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("createColoc", "ça marche !");
                            String coloc_id = response.getString("id");
                            final SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            prefs.putString("coloc_id", coloc_id);
                            prefs.apply();
                        } catch (JSONException e) {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error", error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
