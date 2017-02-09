package roomies.com.roomies.controlers.mainactivity.managecoloc.joincoloc.colocinfos;


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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roomies.com.roomies.controlers.ManageObjects;
import roomies.com.roomies.R;
import roomies.com.roomies.models.users.MembersInfo;

public class ColocInfosFragment extends Fragment {

    private SharedPreferences m_prefs;
    private String m_colocId;
    private List<MembersInfo> m_listMembers;
    private String m_token;
    private MembersAdapter adapter;
    private ListView m_listView;
    private RequestQueue m_requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.e("Members", "onCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_acoloc_infos, container, false);
        m_listView = (ListView) v.findViewById(R.id.listview_coloc_members);
        return (v);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Log.e("Members", "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("Members", "onResume");

        m_listMembers = new ArrayList<>();
        m_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        m_colocId = m_prefs.getString("coloc_id", "");
        m_token = (ManageObjects.readUserInfosInPrefs("userInfos", getContext())).token;
        m_requestQueue = Volley.newRequestQueue(getActivity());
        adapter = new MembersAdapter();
        new Thread(new Runnable() {
            public void run() {
                getColocMembersFromRequest();
            }
        }).start();

        getActivity().setTitle("Roomies Group Name");
        adapter.members = m_listMembers;
        m_listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        //Log.e("Members", "onActivityCreated");
    }

    private void getColocMembersFromRequest() {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/roomies-group/" + m_colocId + "/requests";
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
                                String memberId = new String(json.getString("user"));
                                /*
                                Nouvelle requête si la condition est vérifiée
                                 */
                                getMemberInformations(memberId);
                            }
                        }
                    } catch (JSONException e) {
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
                    headers.put("Authorization", "Bearer " + m_token);
                    return headers;
                }
            };
            m_requestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMemberInformations(final String memberId) {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/users/" + memberId;
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject member = new JSONObject(response);
                        String firstName = member.getJSONObject("profile").getString("firstName");
                        String lastName = member.getJSONObject("profile").getString("lastName");
                        MembersInfo tmpMember = new MembersInfo();
                        tmpMember.firstName = firstName;
                        tmpMember.lastName = lastName;
                        tmpMember.id = memberId;


                        /*


                        Voilà la vilaine crapule qui ne veut pas ajouter mon objet tmpMember à la liste
                        

                         */


                        m_listMembers.add(tmpMember);
                    } catch (JSONException e) {
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
                    headers.put("Authorization", "Bearer " + m_token);
                    return headers;
                }
            };
            m_requestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
