package com.roomies.android.mainactivityviews.managecoloc.joincoloc;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.ManageObjects;
import com.roomies.android.R;
import com.roomies.android.informations.users.MembersInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColocDetailsFragment extends Fragment {

    private static final String TAG = "ColocDetailsFragment";

    private String mColocId;
    private List<MembersInfo> mMembers;
    private String mToken;
    private MembersAdapter mAdapter;
    private ListView ListView;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMembers = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mColocId = prefs.getString("coloc_id", "");
        mToken = (ManageObjects.readUserInfosInPrefs(getContext(), "userInfos")).token;
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mAdapter = new MembersAdapter();

        new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "run: Thread is running.");
                getColocMembersFromRequest();
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coloc_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView = (ListView) view.findViewById(R.id.listview_coloc_members);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Roomies Group Name");
        mMembers = mAdapter.getMembers();
        ListView.setAdapter(mAdapter);
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
                        for (int i = 0; i < members.length(); i++) {
                            JSONObject json = members.getJSONObject(i);

                            String memberStatus = json.getString("status");
                            if (memberStatus.compareTo("confirmed") == 0) {
                                String memberId = json.getString("user");
                                // Nouvelle requête si la condition est vérifiée
                                getMemberInformations(memberId);
                            }
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

    private void getMemberInformations(final String memberId) {
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
