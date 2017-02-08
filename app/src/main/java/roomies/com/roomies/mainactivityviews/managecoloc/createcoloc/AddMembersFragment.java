package roomies.com.roomies.mainactivityviews.managecoloc.createcoloc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.SecondActivity;
import roomies.com.roomies.mainactivityviews.managecoloc.joincoloc.MySearchView;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment {

    private List<MembersInfo> listMembers;
    private ListView listView;
    private SearchView searchView;
    private String token;
    private MembersAdapter adapter;
    private SharedPreferences prefs;
    private Button finish;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMembers = new ArrayList<>();
        adapter = new MembersAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_members, container, false);
        listView = (ListView) v.findViewById(R.id.listview_add_members);
        finish = (Button) v.findViewById(R.id.addMembersFinish);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMembersFromRequest();
        getActivity().setTitle("Add Members");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        token = prefs.getString("token", "");

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSecondActivity = new Intent(getActivity(), SecondActivity.class);
                startActivity(toSecondActivity);
                getActivity().finish();
            }
        });
    }

    private void getMembersFromRequest() {

        // HTTP POST
        String url = getString(R.string.url_base) + "/api/users";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray users = new JSONArray(response);
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject json = users.getJSONObject(i);
                            MembersInfo tmpColocs = new MembersInfo(json);
                            listMembers.add(tmpColocs);
                        }
                        adapter.setData(listMembers);
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
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
                    headers.put("Authorization", "Bearer " + token);
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

        MenuItem searchAction = menu.add(0, 0, 0, getString(R.string.action_search));
        searchAction.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        searchView = new MySearchView(getContext());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
              adapter.getFilter().filter(newText);
              listMembers.clear();
              listMembers = adapter.getMembers();
              return true;
          }
      }
        );
        searchView.setIconifiedByDefault(true);
        searchAction.setActionView(searchView);
    }
}
