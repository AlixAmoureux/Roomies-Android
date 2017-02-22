package roomies.com.roomies.controllers.secondactivity.parameters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

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
import roomies.com.roomies.models.Requests;

public class InvitationsAdapter extends BaseAdapter {

    private List<Requests> lists;
    private String mColocId;
    private String mToken;
    private String mUserId;
    private Context mContext;
    private Activity mActivity;

    InvitationsAdapter(String colocId, String token, Context context, Activity activity) {
        this.lists = new ArrayList<>();
        mColocId = colocId;
        mToken = token;
        mContext = context;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.invitations_infos, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Requests request = lists.get(position);
        holder.requesterFirstName.setText(request.getRequesterFirstName());
        holder.requesterLastName.setText(request.getRequesterLastName());
        holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.acceptRequest.setEnabled(false);
                holder.acceptRequest.setVisibility(View.INVISIBLE);
                acceptRequest(position);
            }
        });
        return convertView;
    }

    private void acceptRequest(int position) {
        String url = mContext.getString(R.string.url_base) +  "/api/roomies-group/" + mColocId + "/requests/" + lists.get(position).getId();
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", "confirmed");
        }
        catch(JSONException e) {
            Log.e("Volley", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("InvitationsAdapter", error.getMessage());
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

    public void setLists(List<Requests> tmpList) {
        lists = tmpList;
        notifyDataSetChanged();
    }

    public List<Requests> getList()
    {
        return (lists);
    }


    private class ViewHolder {
        TextView requesterFirstName;
        TextView requesterLastName;
        Button acceptRequest;

        ViewHolder(View v) {
            requesterFirstName = (TextView) v.findViewById(R.id.requester_first_name);
            requesterLastName = (TextView) v.findViewById(R.id.requester_last_name);
            acceptRequest = (Button) v.findViewById(R.id.accept_request);
        }
    }
}
