package roomies.com.roomies.controllers.mainactivity.managecoloc.joincoloc.listcolocs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
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
import roomies.com.roomies.controllers.MySearchView;
import roomies.com.roomies.controllers.mainactivity.managecoloc.joincoloc.colocinfos.ColocInfosFragment;
import roomies.com.roomies.models.ColocsInfos;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListColocFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private SharedPreferences mPrefs;
    private List<ColocsInfos> mColocsInfos;
    private String mToken;
    private ListView mListView;
    private SearchView mSearchView;

    private static final String TAG = "ListColocFragment";

    private ColocsFilteringAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ListColoc", "onCreate !");
        mAdapter = new ColocsFilteringAdapter();
        mColocsInfos = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("ListColoc", "onCreateView !");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_coloc, container, false);
        mListView = (ListView) v.findViewById(R.id.listview_join_coloc);
        mListView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("ListColoc", "onActivityCreated !");
    }

    @Override
    public void onResume() {
        super.onResume();

        this.mColocsInfos.clear();
        Log.e("ListColoc", "onResume !");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
        getDatasFromRequest();
        getActivity().setTitle("Join a Roomies group");
    }

    private void getDatasFromRequest()
    {
        // HTTP POST
        String url = getString(R.string.url_base) +  "/api/roomies-group";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try
        {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    try {
                        JSONArray users = new JSONArray(response);
                        for (int i = 0; i < users.length(); i++)
                        {
                            JSONObject json = users.getJSONObject(i);
                            ColocsInfos tmpColocs = new ColocsInfos(json);
                            mColocsInfos.add(tmpColocs);
                        }
                        mAdapter.setData(mColocsInfos);
                        mListView.setAdapter(mAdapter);
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
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "Application/json");
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            requestQueue.add(jsonobject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id)
    {
        String coloc_id = mColocsInfos.get(position).id;

        SharedPreferences.Editor editor;
        editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("coloc_id", coloc_id);
        editor.apply();

        Fragment newFragment = new ColocInfosFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchAction = menu.add(0, 0, 0 , getString(R.string.action_search));
        searchAction.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        mSearchView = new MySearchView(getContext());
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Log.e(TAG, "onQueryTextChange: " + newText);
                mAdapter.getFilter().filter(newText);
                mColocsInfos.clear();
                mColocsInfos = mAdapter.getColocations();
                return true;
            }
        }
        );
        mSearchView.setIconifiedByDefault(true);
        searchAction.setActionView(mSearchView);
    }
}
