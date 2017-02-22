package roomies.com.roomies.controllers.secondactivity.parameters;

import android.os.Bundle;
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

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.models.Requests;

public class InvitationsFragment extends Fragment {

    private ListView mListView;
    private String mToken;
    private List<Requests> mListRequests;
    private InvitationsAdapter mAdapter;
    private String mColocId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("InvitationsFragment", "onCreate !");
        mListRequests = new ArrayList<>();
        mColocId = ManageObjects.readColocInfosInPrefs("colocInfos", getActivity()).id;
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
        mAdapter = new InvitationsAdapter(mColocId, mToken, getContext(), getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("InvitationsFragment", "onCreateView !");
        View v = inflater.inflate(R.layout.fragment_listview, container, false);
        mListView = (ListView) v.findViewById(R.id.listview);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("InvitationsFragment", "onCreonResume !");
        mListRequests.clear();
        getActivity().setTitle("Requests");
        getRequesters();
    }

    private void getRequesters() {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/requests";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray lists = new JSONArray(response);
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject request = lists.getJSONObject(i);
                            String status = request.getString("status");
                            String user = request.getString("user");
                            String requestBy = request.getString("requestBy");
                            if (status.compareTo("pending") == 0 && user.trim().compareTo(requestBy.trim()) == 0) {
                                String id = request.getString("id");
                                Requests tmpRequest = new Requests();
                                tmpRequest.setId(id);
                                tmpRequest.setRequestBy(requestBy);
                                tmpRequest.setUser(user);
                                mListRequests.add(tmpRequest);
                            }
                        }
                        for (int i = 0; i < mListRequests.size(); i++) {
                            getNameAndFirstName(i);
                        }


                    } catch (JSONException e) {
                        Log.e("ManageListFragment", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            requestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNameAndFirstName(final int position) {
        // HTTP GET
        String url = getString(R.string.url_base) + "/api/users/" + mListRequests.get(position).getRequestBy();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject profile = new JSONObject(response).getJSONObject("profile");
                        String lastName = profile.getString("lastName");
                        String firstName = profile.getString("firstName");
                        mListRequests.get(position).setRequesterFirstName(firstName);
                        mListRequests.get(position).setRequesterLastName(lastName);

                        mAdapter.setLists(mListRequests);
                        mListView.setAdapter(mAdapter);

                    } catch (JSONException e) {
                        Log.e("ManageListFragment", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            requestQueue.add(jsonobject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
