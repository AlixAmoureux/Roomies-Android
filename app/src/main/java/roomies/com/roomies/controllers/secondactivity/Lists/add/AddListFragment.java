package roomies.com.roomies.controllers.secondactivity.lists.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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

public class AddListFragment extends Fragment {

    private String mToken;
    private List<ListsItemsInfos> mListItemsInfos;
    private ListView mListView;
    private AddListAdapter mAdapter;
    private Button mCreateList;
    private EditText mListName;
    private TextView mMessage;
    private String mListNameVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AddListFragment", "onCreate !");
        mListItemsInfos = new ArrayList<>();
        ListsItemsInfos tmpList = new ListsItemsInfos();
        tmpList.setTitle("");
        mListItemsInfos.add(tmpList);
        mAdapter = new AddListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_list, container, false);
        mListView = (ListView) v.findViewById(R.id.listview_add_listitems);
        mCreateList = (Button) v.findViewById(R.id.create_list_button);
        mListName = (EditText) v.findViewById(R.id.list_name);
        mMessage = (TextView) v.findViewById(R.id.add_list_message);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();

        final String colocId = ManageObjects.readColocInfosInPrefs("colocInfos", getActivity()).id;
        Log.e("AddListFragment", "onResume !");
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;

        getActivity().setTitle("Manage your list");

        mAdapter.setLists(mListItemsInfos);
        mListView.setAdapter(mAdapter);

        mCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListItemsInfos = mAdapter.getList();
                if (checkFields() != -1)
                {
                    createList(colocId);
                    Fragment newFragment = new ManageListsFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    private int checkFields()
    {
        mMessage.setVisibility(View.INVISIBLE);
        mListNameVal = mListName.getText().toString().trim();
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mListNameVal)) {
            mListName.setError("You have to set a name for the list");
            mListName.requestFocus();
            return (-1);
        }
        if (mListItemsInfos.size() == 1 && mListItemsInfos.get(0).getTitle().compareTo("") == 0)
        {
            mMessage.setText("You have to enter at least 1 item");
            mMessage.setTextColor(getResources().getColor(R.color.messageError));
            mMessage.setVisibility(View.VISIBLE);
            return (-1);
        }
        return (1);
    }

    private void createList(final String colocId)
    {
        String url = getString(R.string.url_base) +  "/api/roomies-group/" + colocId + "/lists";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", mListNameVal);
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
                        try {
                            String listId = response.getString("id");
                            for (int i = 0; i < (mListItemsInfos.size() - 1); i++) {
                                addItems(mListItemsInfos.get(i).getTitle(), listId, colocId);
                            }
                            Toast.makeText(getContext(), "List created!", Toast.LENGTH_LONG).show();
                        }
                        catch(JSONException e)
                        {
                            Log.e("AddListFragment", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AddMember", error.getMessage());
                mMessage.setText("You can't create the list");
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

    private void addItems(final String itemName, final String listId, final String colocId)
    {
        String url = getString(R.string.url_base) +  "/api/roomies-group/" + colocId + "/lists/" + listId + "/items";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", itemName);
            jsonObject.put("done", false);
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AddMember", error.getMessage());
                mMessage.setText("You can't create the list");
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
}
