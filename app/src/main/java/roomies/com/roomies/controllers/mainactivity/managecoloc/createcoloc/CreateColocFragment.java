package roomies.com.roomies.controllers.mainactivity.managecoloc.createcoloc;


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
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.secondactivity.SecondActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateColocFragment extends Fragment {
    private EditText coloc_name;
    private EditText coloc_description;
    private Button coloc_button;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_coloc, container, false);
        coloc_name = (EditText) v.findViewById(R.id.coloc_name);
        coloc_description = (EditText) v.findViewById(R.id.coloc_description);
        coloc_button = (Button) v.findViewById(R.id.create_coloc_button);
        token = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
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

    private void checkFields(final String coloc_name_val, final String coloc_desc_val) {
        boolean cancel = false;
        boolean name_problem = false;

        // Check for a valid coloc_name, if the user entered one.
        if (TextUtils.isEmpty(coloc_name_val)) {
            coloc_name.setError("The name is not set");
            name_problem = true;
            cancel = true;
        }

        // Check for a valid coloc description
        if (TextUtils.isEmpty(coloc_desc_val)) {
            coloc_description.setError("The description is not set");
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (name_problem)
                coloc_name.requestFocus();
            else
                coloc_description.requestFocus();
        } else {
            createColoc(coloc_name_val, coloc_desc_val);

            Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
            startActivity(toSecondActivity);
            getActivity().finish();
                /*Fragment newFragment = new AddMembersFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
        }
    }

    private void createColoc(final String coloc_name_val, final String coloc_desc_val) {
        // HTTP POST
        Log.e("createColoc", "début de fonction");
        Log.e("createColoc token", token);
        String url = getString(R.string.url_base) + "/api/roomies-group";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", coloc_name_val);
            jsonObject.put("description", coloc_desc_val);
            jsonObject.put("picturePath", "picture de la mort");
        } catch (JSONException e) {
            Log.e("Volley", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Create Colloc", error.getMessage());
            }
        }) {
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
