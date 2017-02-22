package roomies.com.roomies.controllers.secondactivity.lists.edit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import roomies.com.roomies.controllers.secondactivity.lists.display.ManageListsFragment;
import roomies.com.roomies.models.lists.ListsItemsInfos;


public class EditListFragment extends Fragment {

    private TextView mListName;
    private ListView mListView;
    private Button mEditList;
    private Button mRemoveList;
    private List<ListsItemsInfos> mListItems;
    private TextView mMessage;
    private String mListNameVal;
    private String mToken;
    private String mColocId;
    private String mListId;
    private EditListAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("EditListFragment", "onCreate !");
        mListItems = new ArrayList<>();
        mAdapter = new EditListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edit_list, container, false);
        mListView = (ListView) v.findViewById(R.id.listview_edit_listitems);
        mEditList = (Button) v.findViewById(R.id.edit_list_button);
        mRemoveList = (Button) v.findViewById(R.id.remove_list_button);
        mListName = (TextView) v.findViewById(R.id.list_name);
        mMessage = (TextView) v.findViewById(R.id.edit_list_message);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("EditListFragment", "onResume !");

        mMessage.setVisibility(View.INVISIBLE);

        mColocId = ManageObjects.readColocInfosInPrefs("colocInfos", getActivity()).id;
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mListId = prefs.getString("listId", "");

        getActivity().setTitle("Manage your list");
        mListItems.clear();


        getListItems();

        mEditList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateListItems();
            }
        });

        mRemoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteList();
            }
        });
    }

    private void deleteList() {
        // HTTP DELETE
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/lists/" + mListId;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "The list has been removed", Toast.LENGTH_LONG).show();
                        Fragment newFragment = new ManageListsFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Remove List", error.getMessage());
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

    private void getListItems() {
        // HTTP GET

        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/lists/" + mListId + "/items";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray lists = new JSONArray(response);
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject item = lists.getJSONObject(i);
                            boolean done = item.getBoolean("done");
                            String title = item.getString("title");
                            String id = item.getString("id");

                            ListsItemsInfos tmpList = new ListsItemsInfos();
                            tmpList.setTitle(title);
                            tmpList.setDone(done);
                            tmpList.setId(id);
                            mListItems.add(tmpList);
                        }
                        mAdapter.setLists(mListItems);
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

    private void updateListItems() {
        mMessage.setVisibility(View.VISIBLE);
        for (int i = 0; i < mListItems.size(); i++) {
            updateItem(mListItems.get(i));
        }
        //mMessage.setText("The list has been updated");
        //mMessage.setTextColor(getResources().getColor(R.color.messageSuccess));
        Toast.makeText(getContext(), "The list has been updated", Toast.LENGTH_LONG).show();
        Fragment newFragment = new ManageListsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void updateItem(ListsItemsInfos item) {
        // HTTP PUT
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/lists/" + mListId + "/items/" + item.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", item.getTitle());
            jsonObject.put("done", item.isDone());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("EditListFragment", "SUCCESS");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update Profile", error.getMessage());
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
