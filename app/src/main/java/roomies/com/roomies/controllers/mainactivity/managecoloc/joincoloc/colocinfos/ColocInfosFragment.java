package roomies.com.roomies.controllers.mainactivity.managecoloc.joincoloc.colocinfos;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.models.users.MembersInfo;

public class ColocInfosFragment extends Fragment {

    private static final String TAG = "ColocDetailsFragment";

    private String mColocId;
    private List<MembersInfo> mMembers;
    private String mToken;
    private MembersAdapter mAdapter;
    private ListView ListView;
    private RequestQueue mRequestQueue;
    private TextView mDescription;
    private Button mJoinColoc;
    private String mUserId;
    private TextView mMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembers = new ArrayList<>();
        mAdapter = new MembersAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coloc_infos, container, false);
        mDescription = (TextView) v.findViewById(R.id.coloc_description);
        mJoinColoc = (Button) v.findViewById(R.id.button_join_coloc);
        mMessage = (TextView) v.findViewById(R.id.join_coloc_request_message);
        return (v);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView = (ListView) view.findViewById(R.id.listview_coloc_members);
    }


    @Override
    public void onResume() {
        super.onResume();

        mMessage.setVisibility(View.INVISIBLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mColocId = prefs.getString("coloc_id", "");
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
        mUserId = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).id;
        mRequestQueue = Volley.newRequestQueue(getActivity());

        mJoinColoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });

        getColocInformations();

        getColocMembersFromRequest();




        ListView.setAdapter(mAdapter);
    }

    private void createRequest()
    {
        mMessage.setVisibility(View.VISIBLE);
        String url = getString(R.string.url_base) +  "/api/roomies-group/" + mColocId + "/requests";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", mUserId);
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
                        mMessage.setText("A request has been sent to join this Roomie !");
                        mMessage.setTextColor(getResources().getColor(R.color.messageSuccess));
                        Log.e("createRequest", "ça a marché !");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AddMember", error.getMessage());
                mMessage.setText("Error : you can't join this Roomie!");
                mMessage.setTextColor(getResources().getColor(R.color.messageError));
            }
        })
        {
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

    private void getColocInformations() {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId;
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                        try {
                            JSONObject coloc = new JSONObject(response);
                            String title = coloc.getString("title");
                            String description = coloc.getString("description");
                            mDescription.setText(description);
                            getActivity().setTitle(title);
                        }
                        catch (JSONException e)
                        {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR VOLLEY", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "Application/json");
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            mRequestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getColocMembersFromRequest() {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/requests";
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray members = new JSONArray(response);
                        List<String> membersId = new ArrayList<>();
                        for (int i = 0; i < members.length(); i++) {
                            JSONObject json = members.getJSONObject(i);

                            String memberStatus = json.getString("status");
                            if (memberStatus.compareTo("confirmed") == 0) {
                                String memberId = json.getString("user");
                                membersId.add(memberId);
                            }
                        }
                        for (String id : membersId) {
                            getMemberInformations(id);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR VOLLEY", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "Application/json");
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            mRequestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMemberInformations(final String memberId)
    {

        // HTTP GET
        String url = getString(R.string.url_base) + "/api/users/" + memberId;
        try {
            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonMember = new JSONObject(response);
                                String firstName = jsonMember.getJSONObject("profile").getString("firstName");
                                String lastName = jsonMember.getJSONObject("profile").getString("lastName");

                                MembersInfo member = new MembersInfo();
                                member.firstName = firstName;
                                member.lastName = lastName;
                                member.id = memberId;

                                // FIXME Voilà la vilaine crapule qui ne veut pas ajouter mon objet member à la liste

                                mMembers.add(member);
                                mAdapter.setMembers(mMembers);
                            } catch (JSONException e) {
                                Log.e(TAG, "Error while parsing JSON", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Erreur Volley", error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "Application/json");
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            mRequestQueue.add(getRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
