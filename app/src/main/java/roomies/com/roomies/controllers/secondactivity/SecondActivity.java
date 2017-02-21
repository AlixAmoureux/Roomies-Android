package roomies.com.roomies.controllers.secondactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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
import roomies.com.roomies.controllers.mainactivity.managecoloc.JointOrCreateColocFragment;
import roomies.com.roomies.models.ColocsInfos;

public class SecondActivity extends AppCompatActivity {

    private String mToken;
    private boolean mHasRoomies = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        Log.e("Second Activity", "onCreate 1 !");

        mToken = ManageObjects.readUserInfosInPrefs("userInfos", this).token;
        getRoomieInfos(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(toolbar);
    }




    private void getRoomieInfos(final Bundle savedInstanceState) {
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
                                mHasRoomies = true;
                                break;
                            }
                        }
                        if (savedInstanceState == null)
                        {
                            if (mHasRoomies)
                                getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
                            else
                                getSupportFragmentManager().beginTransaction().add(R.id.container, new JointOrCreateColocFragment()).commit();
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
