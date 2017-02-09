package roomies.com.roomies.mainactivityviews.managecoloc.joincoloc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;
import roomies.com.roomies.informations.users.MembersInfo;

class MembersAdapter extends BaseAdapter {

    public List<MembersInfo> members;

    MembersAdapter() {
        this.members = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.member_infos, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final MembersInfo member = members.get(position);
        holder.memberLastName.setText(member.lastName);
        holder.memberFirstName.setText(member.firstName);

        /*Picasso.with(parent.getContext())
                .load(member.getPicturePath())
                .error(R.drawable.unknown_user)
                .into(holder.colocPhoto);*/

        return convertView;
    }

    private class ViewHolder
    {
        TextView memberLastName;
        TextView memberFirstName;
        //ImageView memberPhoto;

        ViewHolder(View v)
        {
            memberLastName = (TextView)v.findViewById(R.id.member_last_name);
            memberFirstName = (TextView)v.findViewById(R.id.member_first_name);
            //memberPhoto = (ImageView) v.findViewById(R.id.member_photo);
        }
    }
}
