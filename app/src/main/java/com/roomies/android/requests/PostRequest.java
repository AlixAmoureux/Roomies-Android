package com.roomies.android.requests;

import android.content.Context;
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
import com.roomies.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PostRequest {
    private static final String TAG = "PostRequest";

    private String mUrl;
    private Context mContext;
    private Map<String, String> mHeaders;
    private JSONObject mBody;
    private JSONObject mResponse;

    public PostRequest(Context context, String url, Map<String, String> headers, JSONObject body) {
        mUrl = url;
        mContext = context;
        mHeaders = headers;
        mBody = body;
        mResponse = null;
    }

    public JSONObject getResponse() {
        return (mResponse);
    }

    public void postDatas() {
        String url = mContext.getString(R.string.url_base) + mUrl;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse yeah!");

                        try {
                            Log.d("createColoc", "ça marche !");
                            String colocId = response.getString("id");
                            final SharedPreferences prefs = PreferenceManager
                                    .getDefaultSharedPreferences(mContext);
                            prefs.edit().putString("coloc_id", colocId).apply();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error while parsing JSON.", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Server returned HTTP code " + error.networkResponse.statusCode, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
