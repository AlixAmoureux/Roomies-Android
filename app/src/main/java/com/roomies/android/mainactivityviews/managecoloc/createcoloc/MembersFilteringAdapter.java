package com.roomies.android.mainactivityviews.managecoloc.createcoloc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.roomies.android.R;
import com.roomies.android.informations.users.MembersInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MembersFilteringAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "MembersFilteringAdapter";

    private Context mContext;
    private List<MembersInfo> mMembers;
    private List<MembersInfo> mMembersDisplayed;
    private Filter mMembersFilter;
    private String mColocId;
    private String mToken;
    private String mUserId;

    MembersFilteringAdapter() {
        mMembersFilter = new MembersFilter();
        mMembersDisplayed = new ArrayList<>();
        mMembers = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMembersDisplayed.size();
    }

    @Override
    public Object getItem(int position) {
        return mMembersDisplayed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MembersFilteringAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.add_members_info, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MembersInfo member = mMembersDisplayed.get(position);
        holder.memberLastName.setText(member.lastName);
        holder.memberFirstName.setText(member.firstName);
        holder.memberCity.setText(member.city);

        // TODO Handle this in onItemSelected in AddMembersFragment
        holder.memberInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Envoi d'une invitation");
                Log.d(TAG, "Id = " + member.id);
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                mColocId = prefs.getString("coloc_id", "");
                mToken = prefs.getString("mToken", "");
                mUserId = member.id;
                holder.memberInvite.setVisibility(View.INVISIBLE);
                holder.memberInvite.setClickable(false);
                createRequest();

                // Envoi d'une requête pour ajouter le membre
            }
        });

        /*Picasso.with(parent.getContext())
                .load(member.getPicturePath())
                .error(R.drawable.unknown_user)
                .into(holder.colocPhoto);*/

        return convertView;
    }

    private void createRequest() {
        String url = mContext.getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/requests";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JSONObject postData = new JSONObject();
        try {
            postData.put("userID", mUserId);
        } catch (JSONException e) {
            Log.e(TAG, "Error while modifying JSONObject", e);
            return;
        }

        // FIXME Adapters shouldn't post network request. Sole purpose of an adapter is DISPLAY.

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("createRequest", "ça a marché !");
                       /* try {

                        }
                        catch (JSONException e)
                        {
                            Log.e("ERROR VOLLEY", e.getMessage());
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error", error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }

    public void setData(List<MembersInfo> tmpMembers, Context context, Activity activity) {
        if (mMembers.size() != 0)
            mMembers.clear();
        if (mMembersDisplayed.size() != 0)
            mMembersDisplayed.clear();
        mMembers = new ArrayList<>(tmpMembers);
        mMembersDisplayed = new ArrayList<>(tmpMembers);
        mContext = context;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mMembersFilter;
    }

    public List<MembersInfo> getMembers() {
        return (this.mMembersDisplayed);
    }

    private class MembersFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MembersInfo> tmpList = new ArrayList<>();
            if (constraint.length() == 0)
                tmpList = new ArrayList<>(mMembers);
            else {
                for (MembersInfo tmpMember : mMembers) {
                    if (tmpMember.firstName.trim().toLowerCase()
                            .contains(constraint.toString().trim().toLowerCase())) {
                        tmpList.add(tmpMember);
                    } else if (tmpMember.lastName.trim().toLowerCase()
                            .contains(constraint.toString().trim().toLowerCase())) {
                        tmpList.add(tmpMember);
                    } else {

                    }
                }
            }
            FilterResults results = new FilterResults();
            results.count = tmpList.size();
            results.values = tmpList;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mMembersDisplayed.clear();
            mMembersDisplayed = (List<MembersInfo>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        final TextView memberLastName;
        final TextView memberFirstName;
        final TextView memberCity;
        final ImageView memberPhoto;
        final Button memberInvite;

        ViewHolder(View v) {
            memberLastName = (TextView) v.findViewById(R.id.nom_membre);
            memberFirstName = (TextView) v.findViewById(R.id.prenom_membre);
            memberCity = (TextView) v.findViewById(R.id.ville_membre);
            memberPhoto = (ImageView) v.findViewById(R.id.photo_membre);
            memberInvite = (Button) v.findViewById(R.id.add_members_button);
        }
    }
}
