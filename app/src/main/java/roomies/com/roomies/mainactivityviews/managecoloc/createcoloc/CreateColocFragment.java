package roomies.com.roomies.mainactivityviews.managecoloc.createcoloc;


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
import roomies.com.roomies.Requests.RequestPost;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateColocFragment extends Fragment
{
    private EditText coloc_name;
    private EditText coloc_description;
    private ImageButton coloc_image;
    private Button coloc_button;
    private String token;
    private static int RESULT_LOAD_IMAGE = 1;
    private String imagePath = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_create_coloc, container, false);
        coloc_name = (EditText) v.findViewById(R.id.coloc_name);
        coloc_description = (EditText) v.findViewById(R.id.coloc_description);
        coloc_image = (ImageButton) v.findViewById(R.id.create_coloc_image);
        coloc_button = (Button) v.findViewById(R.id.create_coloc_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        token = prefs.getString("token", "");
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coloc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coloc_name_val = coloc_name.getText().toString();
                String coloc_description_val = coloc_description.getText().toString();
                checkFields(coloc_name_val, coloc_description_val);
            }
        });

        coloc_image.setOnClickListener(new View.OnClickListener() {
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

        Bitmap bmp = null;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();



                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            coloc_image.setImageBitmap(bmp);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Create a Roomies group");
    }

    private void checkFields(final String coloc_name_val, final String coloc_desc_val)
    {
        boolean cancel = false;
        boolean name_problem = false;

        // Check for a valid coloc_name, if the user entered one.
        if (TextUtils.isEmpty(coloc_name_val)) {
            coloc_name.setError("Le nom de la colocation n'est pas indiquée");
            name_problem = true;
            cancel = true;
        }

        // Check for a valid coloc description
        if (TextUtils.isEmpty(coloc_desc_val)) {
            coloc_description.setError("La description de la colocation n'est pas indiquée");
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (name_problem)
                coloc_name.requestFocus();
            else
                coloc_description.requestFocus();
        }
        else {
                /*final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                JSONObject body = new JSONObject();
                try {
                    body.put("title", coloc_name_val);
                    body.put("description", coloc_desc_val);
                    body.put("picturePath", imagePath);
                }
                catch(JSONException e) {
                    Log.e("Volley", e.getMessage());
                }
                RequestPost requestPost = new RequestPost("/api/roomies-group", getActivity(), headers, body);
                requestPost.postDatas();
                JSONObject response = requestPost.getResponse();
                this.getDatas(response);*/

                createColoc(coloc_name_val, coloc_desc_val);
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

    private void createColoc(final String coloc_name_val, final String coloc_desc_val)
    {
        // HTTP POST
        Log.e("createColoc", "début de fonction");
        Log.e("createColoc token", token);
        String url = getString(R.string.url_base) +  "/api/roomies-group";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", coloc_name_val);
            jsonObject.put("description", coloc_desc_val);
            jsonObject.put("picturePath", imagePath);
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
                            Log.e("createColoc", "ça marche !");
                            String coloc_id = response.getString("id");
                            final SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            prefs.putString("coloc_id", coloc_id);
                            prefs.apply();
                        }
                        catch (JSONException e)
                        {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Create Colloc", error.getMessage());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
