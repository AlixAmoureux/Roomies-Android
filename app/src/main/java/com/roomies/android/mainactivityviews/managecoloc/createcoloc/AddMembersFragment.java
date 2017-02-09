package com.roomies.android.mainactivityviews.managecoloc.createcoloc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.MySearchView;
import com.roomies.android.R;
import com.roomies.android.SecondActivity;
import com.roomies.android.informations.users.MembersInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment {

    private static final String TAG = "AddMembersFragment";

    private List<MembersInfo> mMembers;
    private ListView mListView;
    private String mToken;
    private MembersFilteringAdapter mAdapter;
    private SharedPreferences mPrefs;
    private Button mFinishButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembers = new ArrayList<>();
        mAdapter = new MembersFilteringAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_members, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.listview_add_members);
        mFinishButton = (Button) view.findViewById(R.id.addMembersFinish);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mToken = mPrefs.getString("token", "");

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
                startActivity(toSecondActivity);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getMembersFromRequest();
        getActivity().setTitle("Add Members");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchAction = menu.add(0, 0, 0, getString(R.string.action_search));
        searchAction.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        SearchView searchView = new MySearchView(getContext());
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mAdapter.getFilter().filter(newText);
                        mMembers.clear();
                        mMembers = mAdapter.getMembers();
                        return true;
                    }
                }
        );
        searchView.setIconifiedByDefault(true);
        searchAction.setActionView(searchView);
    }

    private void getMembersFromRequest() {
        // HTTP POST
        String url = getString(R.string.url_base) + "/api/users";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray users = new JSONArray(response);
                                for (int i = 0; i < users.length(); i++) {
                                    JSONObject json = users.getJSONObject(i);
                                    MembersInfo tmpColocs = new MembersInfo(json);
                                    mMembers.add(tmpColocs);
                                }
                                mAdapter.setData(mMembers, getContext(), getActivity());
                                mListView.setAdapter(mAdapter);
                            } catch (JSONException e) {
                                Log.e(TAG, "Exception occured while parsing JSON", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onErrorResponse: Volley error", error);
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
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
