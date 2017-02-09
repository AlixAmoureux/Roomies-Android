package roomies.com.roomies.controlers.mainactivity.managecoloc.joincoloc.listcolocs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;
import roomies.com.roomies.models.ColocsInfos;

class ColocsFilteringAdapter extends BaseAdapter implements Filterable {
    private List<ColocsInfos> colocations;
    private List<ColocsInfos> colocationsDisplayed;
    private Filter colocFilter;

    ColocsFilteringAdapter() {
        this.colocFilter = new ColocsFilter();
        this.colocationsDisplayed = new ArrayList<>();
        this.colocations = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return colocationsDisplayed.size();
    }

    @Override
    public Object getItem(int position) {
        return colocationsDisplayed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.join_colocation_info, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ColocsInfos colocation = colocationsDisplayed.get(position);
        holder.colocName.setText(colocation.title);

        /*Picasso.with(parent.getContext())
                .load(colocation.picturePath)
                .error(R.drawable.unknown_user)
                .into(holder.colocPhoto);*/

        return convertView;
    }

    public void setData(List<ColocsInfos> tmpColocs)
    {
        if (this.colocations.size() != 0)
            this.colocations.clear();
        if (this.colocationsDisplayed.size() != 0)
            this.colocationsDisplayed.clear();
        this.colocations = new ArrayList<>(tmpColocs);
        this.colocationsDisplayed = new ArrayList<>(tmpColocs);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return colocFilter;
    }

    private class ColocsFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<ColocsInfos> tmpList = new ArrayList<>();
            if (constraint.length() == 0)
                tmpList = new ArrayList<>(colocations);
            else {
                for (ColocsInfos tmpColocation : colocations)
                {
                    if (tmpColocation.title.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase()))
                    {
                        tmpList.add(tmpColocation);
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
            colocationsDisplayed.clear();
            colocationsDisplayed = (List<ColocsInfos>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder
    {
        TextView colocName;
        ImageView colocPhoto;

        ViewHolder(View v)
        {
            colocName = (TextView)v.findViewById(R.id.nom_coloc);
            colocPhoto = (ImageView)v.findViewById(R.id.image_coloc);
        }
    }
    public List<ColocsInfos> getColocations()
    {
        return (this.colocationsDisplayed);
    }
}
