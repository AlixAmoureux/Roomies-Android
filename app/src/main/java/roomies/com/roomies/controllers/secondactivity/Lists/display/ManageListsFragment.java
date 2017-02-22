package roomies.com.roomies.controllers.secondactivity.lists.display;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import roomies.com.roomies.controllers.secondactivity.lists.add.AddListFragment;
import roomies.com.roomies.controllers.secondactivity.lists.edit.EditListFragment;
import roomies.com.roomies.models.lists.ListInfos;

public class ManageListsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView mListView;
    private String mToken;
    private List<ListInfos> mListsInfos;
    private ListsAdapter mAdapter;
    private String mColocId;
    private int mNbItems;
    private int mNbItemsDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ManageList", "onCreate !");
        mListsInfos = new ArrayList<>();
        mAdapter = new ListsAdapter(getContext());
        setHasOptionsMenu(true);
        mColocId = ManageObjects.readColocInfosInPrefs("colocInfos", getActivity()).id;
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("ManageList", "onCreateView !");
        View v = inflater.inflate(R.layout.fragment_listview, container, false);
        mListView = (ListView) v.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ManageList", "onResume !");
        mListsInfos.clear();
        getActivity().setTitle("Lists");
        getListsFromRequest();
    }


    private void getListsFromRequest() {

        // HTTP GET
        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/lists";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray lists = new JSONArray(response);
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject list = lists.getJSONObject(i);
                            ListInfos tmpList = new ListInfos(list);
                            mListsInfos.add(tmpList);
                        }
                        for (int i = 0; i < mListsInfos.size(); i++) {
                            getListItems(i);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add) {
            Fragment newFragment = new AddListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getListItems(final int position) {
        // HTTP GET

        String url = getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/lists/" + mListsInfos.get(position).getId() + "/items";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        mNbItems = 0;
                        mNbItemsDone = 0;
                        JSONArray lists = new JSONArray(response);
                        for (int i = 0; i < lists.length(); i++) {
                            JSONObject item = lists.getJSONObject(i);
                            boolean done = item.getBoolean("done");
                            if (done)
                                mNbItemsDone++;
                            mNbItems++;
                        }
                        mListsInfos.get(position).setItemsDone(mNbItemsDone);
                        mListsInfos.get(position).setNbItems(mNbItems);

                        mAdapter.setLists(mListsInfos);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        String listId = mListsInfos.get(position).getId();

        SharedPreferences.Editor editor;
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("listId", listId);
        editor.apply();

        Fragment newFragment = new EditListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
