package roomies.com.roomies.mainactivityviews;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
            if (name_problem == true)
                coloc_name.requestFocus();
            else
                coloc_description.requestFocus();
        }
        else {
            createColoc(coloc_name_val, coloc_desc_val);
            /*Fragment newFragment = new AddMembersFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();*/
        }
    }


    /*
    lorsqu'on a créé une coloc
{
  "title": "Ma super colocation !",
  "description": "C'est une colocation super cool :)",
  "picturePath": "https://appartmap.com/wp-content/uploads/2014/04/83493-comment-organiser-la-cuisine-en-colocation-_x450.jpg",
  "createdAt": "2016-12-08T02:36:19.490Z",
  "id": "5848c72368ba2431ccbd0a39"
}
     */
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
            jsonObject.put("picturePath", "https://appartmap.com/wp-content/uploads/2014/04/83493-comment-organiser-la-cuisine-en-colocation-_x450.jpg");
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
