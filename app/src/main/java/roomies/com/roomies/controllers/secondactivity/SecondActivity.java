package roomies.com.roomies.controllers.secondactivity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.mainactivity.managecoloc.createcoloc.CreateColocFragment;
import roomies.com.roomies.controllers.mainactivity.managecoloc.joincoloc.listcolocs.ListColocFragment;
import roomies.com.roomies.controllers.secondactivity.parameters.ParametersFragment;
import roomies.com.roomies.models.ColocsInfos;
import roomies.com.roomies.models.users.ConnectedUserInfo;

public class SecondActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        Log.e("Second Activity", "onCreate 1 !");

        mToken = ManageObjects.readUserInfosInPrefs("userInfos", this).token;
        getRoomieInfos();

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ListColocFragment()).commit();
        }
        Log.e("Second Activity", "onCreate 1 !");
        mBottomBar = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment f = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        f = new ListColocFragment();
                        break;
                    case R.id.action_dettes:
                        f = new CreateColocFragment();
                        break;
                    case R.id.action_sous_menu:
                        f = new ParametersFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, f)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                return false;
            }
        });
    }




    private void getRoomieInfos() {
        // HTTP POST
        String url = getString(R.string.url_base) + "/api/roomies-group/me";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray roomies = new JSONArray(response);
                        for (int i = 0; i < roomies.length(); i++) {
                            JSONObject roomie = roomies.getJSONObject(i);
                            String status = roomie.getString("status");
                            if (status.compareTo("confirmed") == 0)
                            {
                                Log.d("Second Activity", "C'est la bonne roomie !");
                                ColocsInfos colocInfos = new ColocsInfos(roomie);
                                ManageObjects.writeColocInfosInPrefs(colocInfos, "colocInfos", SecondActivity.this);
                                break;
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
