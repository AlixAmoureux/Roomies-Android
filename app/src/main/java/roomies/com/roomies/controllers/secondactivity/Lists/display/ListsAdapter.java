package roomies.com.roomies.controllers.secondactivity.lists.display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;
import roomies.com.roomies.models.lists.ListInfos;

public class ListsAdapter extends BaseAdapter {

    private List<ListInfos> lists;
    private Context mContext;

    ListsAdapter(Context context) {
        this.lists = new ArrayList<>();
        mContext = context;
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
        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.lists_infos, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ListInfos list = lists.get(position);
        holder.listName.setText(list.getTitle());

        String progression = list.getItemsDone() + "/" + list.getNbItems();
        if (list.getItemsDone() == 0)
        {
            holder.listProgress.setTextColor(mContext.getResources().getColor(R.color.messageError));
        }
        else if (list.getItemsDone() == list.getNbItems())
        {
            holder.listProgress.setTextColor(mContext.getResources().getColor(R.color.messageSuccess));
        }
        else
        {
            holder.listProgress.setTextColor(mContext.getResources().getColor(R.color.messageWarning));
        }
        holder.listProgress.setText(progression);

        return convertView;
    }

    public void setLists(List<ListInfos> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    private class ViewHolder
    {
        TextView listName;
        TextView listProgress;

        ViewHolder(View v)
        {
            listName = (TextView)v.findViewById(R.id.list_name);
            listProgress = (TextView)v.findViewById(R.id.list_progress);
        }
    }
}
