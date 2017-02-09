package com.roomies.android.mainactivityviews.managecoloc.joincoloc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.roomies.android.R;
import com.roomies.android.informations.ColocsInfos;

import java.util.ArrayList;
import java.util.List;

class ColocsFilteringAdapter extends BaseAdapter implements Filterable {
    private List<ColocsInfos> mColocations;
    private List<ColocsInfos> mDisplayedColocations;
    private Filter mFilter;

    ColocsFilteringAdapter() {
        mFilter = new ColocsFilter();
        mDisplayedColocations = new ArrayList<>();
        mColocations = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDisplayedColocations.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedColocations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.join_colocation_info, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ColocsInfos colocation = mDisplayedColocations.get(position);
        holder.colocName.setText(colocation.title);

        /*Picasso.with(parent.getContext())
                .load(colocation.picturePath)
                .error(R.drawable.unknown_user)
                .into(holder.colocPhoto);*/

        return convertView;
    }

    public void setData(List<ColocsInfos> tmpColocs) {
        if (mColocations.size() != 0)
            mColocations.clear();
        if (mDisplayedColocations.size() != 0)
            mDisplayedColocations.clear();
        mColocations = new ArrayList<>(tmpColocs);
        mDisplayedColocations = new ArrayList<>(tmpColocs);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public List<ColocsInfos> getColocations() {
        return (mDisplayedColocations);
    }

    private static class ViewHolder {
        final TextView colocName;
        final ImageView colocPhoto;

        ViewHolder(View v) {
            colocName = (TextView) v.findViewById(R.id.nom_coloc);
            colocPhoto = (ImageView) v.findViewById(R.id.image_coloc);
        }
    }

    private class ColocsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ColocsInfos> filteredList = new ArrayList<>();
            if (constraint.length() == 0)
                filteredList = new ArrayList<>(mColocations);
            else {
                for (ColocsInfos tmpColocation : mColocations) {
                    if (tmpColocation.title.trim().toLowerCase()
                            .contains(constraint.toString().trim().toLowerCase())) {
                        filteredList.add(tmpColocation);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mDisplayedColocations.clear();
            mDisplayedColocations = (List<ColocsInfos>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
