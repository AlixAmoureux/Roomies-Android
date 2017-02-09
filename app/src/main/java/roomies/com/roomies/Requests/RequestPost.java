package roomies.com.roomies.Requests;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import roomies.com.roomies.R;

public class RequestPost
{
    private String m_url;
    private Activity m_activity;
    private Map<String, String> m_headers;
    private JSONObject m_body;
    private JSONObject m_response;

    public RequestPost(String url, Activity activity, Map<String, String> headers, JSONObject body)
    {
        this.m_url = url;
        this.m_activity = activity;
        this.m_headers = headers;
        this.m_body = body;
        this.m_response = null;
    }

    public JSONObject getResponse()
    {
        return (m_response);
    }

    public void postDatas()
    {
        String url = m_activity.getString(R.string.url_base) +  m_url;
        RequestQueue requestQueue = Volley.newRequestQueue(m_activity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, m_body,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                            Log.e("RequestPost", "onResponse yeah!");

                        try {
                            Log.e("createColoc", "ça marche !");
                            String coloc_id = response.getString("id");
                            final SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(m_activity).edit();
                            prefs.putString("coloc_id", coloc_id);
                            prefs.apply();
                        }
                        catch (JSONException e)
                        {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Create Colloc", error.getMessage());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return m_headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
