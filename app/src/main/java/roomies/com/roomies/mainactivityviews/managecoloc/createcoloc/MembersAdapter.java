package roomies.com.roomies.mainactivityviews.managecoloc.createcoloc;

import android.media.Image;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;

/**
 * Created by xila on 05/02/2017.
 */

public class MembersAdapter extends BaseAdapter implements Filterable{

    protected List<MembersInfo> members;
    private List<MembersInfo> membersDisplayed;
    private Filter membersFilter;

    MembersAdapter() {
        this.membersFilter = new MembersFilter();
        this.membersDisplayed = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return membersDisplayed.size();
    }

    @Override
    public Object getItem(int position) {
        return membersDisplayed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MembersAdapter.ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.add_members_info, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final MembersInfo member = membersDisplayed.get(position);
        holder.memberLastName.setText(member.getLastName());
        holder.memberFirstName.setText(member.getFirstName());
        holder.memberCity.setText(member.getCity());

        holder.memberInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("MemberAdapter", "Envoi d'une invitation");
                Log.e("MemberAdapter", "Id = " + member.getId());

                // Envoi d'une requête pour ajouter le membre
            }
        });

        /*Picasso.with(parent.getContext())
                .load(member.getPicturePath())
                .error(R.drawable.unknown_user)
                .into(holder.colocPhoto);*/

        return convertView;
    }

    public void setData(List<MembersInfo> tmpMembers)
    {
        if (this.members.size() != 0)
            this.members.clear();
        if (this.membersDisplayed.size() != 0)
            this.membersDisplayed.clear();
        this.members = new ArrayList<>(tmpMembers);
        this.membersDisplayed = new ArrayList<>(tmpMembers);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return membersFilter;
    }

    public class MembersFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<MembersInfo> tmpList = new ArrayList<>();
            if (constraint.length() == 0)
                tmpList = new ArrayList<>(members);
            else {
                for (MembersInfo tmpMember : members)
                {
                    if (tmpMember.getFirstName().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase()))
                    {
                        tmpList.add(tmpMember);
                    }
                    else if (tmpMember.getLastName().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase()))
                    {
                        tmpList.add(tmpMember);
                    }
                    else
                    {

                    }
                }
            }
            FilterResults results = new FilterResults();
            results.count = tmpList.size();
            results.values = tmpList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults)
        {
            membersDisplayed.clear();
            membersDisplayed = (List<MembersInfo>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder
    {
        TextView memberLastName;
        TextView memberFirstName;
        TextView memberCity;
        ImageView memberPhoto;
        Button memberInvite;

        ViewHolder(View v)
        {
            memberLastName = (TextView)v.findViewById(R.id.nom_membre);
            memberFirstName = (TextView)v.findViewById(R.id.prenom_membre);
            memberCity = (TextView)v.findViewById(R.id.ville_membre);
            memberPhoto = (ImageView) v.findViewById(R.id.photo_membre);
            memberInvite = (Button) v.findViewById(R.id.add_members_button);
        }
    }
    public List<MembersInfo> getMembers()
    {
        return (this.membersDisplayed);
    }
}
