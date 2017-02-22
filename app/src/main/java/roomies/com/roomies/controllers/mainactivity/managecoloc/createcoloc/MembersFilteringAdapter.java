package roomies.com.roomies.controllers.mainactivity.managecoloc.createcoloc;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.models.users.MembersInfo;


class MembersFilteringAdapter extends BaseAdapter implements Filterable {

    private List<MembersInfo> mMembers;
    private List<MembersInfo> mMembersDisplayed;
    private Filter mMembersFilter;
    private String mColocId;
    private String mToken;
    private Context mContext;
    private Activity mActivity;

    MembersFilteringAdapter(Activity actitiy, Context context) {
        this.mMembersFilter = new MembersFilter();
        this.mMembersDisplayed = new ArrayList<>();
        this.mMembers = new ArrayList<>();
        mActivity = actitiy;
        mContext = context;
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
        }
        // Vue recyclée
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MembersInfo member = mMembersDisplayed.get(position);
        holder.memberLastName.setText(member.lastName);
        holder.memberFirstName.setText(member.firstName);
        holder.memberCity.setText(member.city);

        holder.memberInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("MemberAdapter", "Envoi d'une invitation");
                Log.e("MemberAdapter", "Id = " + member.id);
                mColocId = ManageObjects.readColocInfosInPrefs("colocInfos", mActivity).id;
                mToken = ManageObjects.readUserInfosInPrefs("userInfos", mActivity).token;
                holder.memberInvite.setVisibility(View.INVISIBLE);
                holder.memberInvite.setClickable(false);
                createRequest(member.id);
            }
        });
        return convertView;
    }

    private void createRequest(String userId) {
        String url = mContext.getString(R.string.url_base) + "/api/roomies-group/" + mColocId + "/requests";
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userId);
        } catch (JSONException e) {
            Log.e("Volley", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("createRequest", "ça a marché !");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AddMember", error.getMessage());
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
        requestQueue.add(jsonObjectRequest);
    }

    public void setData(List<MembersInfo> tmpMembers, Context context, Activity activity) {
        if (this.mMembers.size() != 0)
            this.mMembers.clear();
        if (this.mMembersDisplayed.size() != 0)
            this.mMembersDisplayed.clear();
        this.mMembers = new ArrayList<>(tmpMembers);
        this.mMembersDisplayed = new ArrayList<>(tmpMembers);
        this.mContext = context;
        this.mActivity = activity;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mMembersFilter;
    }

    public class MembersFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MembersInfo> tmpList = new ArrayList<>();
            if (constraint.length() == 0)
                tmpList = new ArrayList<>(mMembers);
            else {
                for (MembersInfo tmpMember : mMembers) {
                    if (tmpMember.firstName.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                        tmpList.add(tmpMember);
                    } else if (tmpMember.lastName.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mMembersDisplayed.clear();
            mMembersDisplayed = (List<MembersInfo>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView memberLastName;
        TextView memberFirstName;
        TextView memberCity;
        ImageView memberPhoto;
        Button memberInvite;

        ViewHolder(View v) {
            memberLastName = (TextView) v.findViewById(R.id.nom_membre);
            memberFirstName = (TextView) v.findViewById(R.id.prenom_membre);
            memberCity = (TextView) v.findViewById(R.id.ville_membre);
            memberPhoto = (ImageView) v.findViewById(R.id.photo_membre);
            memberInvite = (Button) v.findViewById(R.id.add_members_button);
        }
    }

    public List<MembersInfo> getmMembers() {
        return (this.mMembersDisplayed);
    }
}
