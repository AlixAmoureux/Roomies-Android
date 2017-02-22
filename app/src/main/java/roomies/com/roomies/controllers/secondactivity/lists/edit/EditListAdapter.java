package roomies.com.roomies.controllers.secondactivity.lists.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;
import roomies.com.roomies.models.lists.ListsItemsInfos;

public class EditListAdapter extends BaseAdapter {

    private List<ListsItemsInfos> lists;

    EditListAdapter() {
        this.lists = new ArrayList<>();
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
            convertView = inflater.inflate(R.layout.edit_list_items_infos, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListsItemsInfos tmpList = lists.get(position);
        holder.itemName.setText(tmpList.getTitle());
        if (tmpList.isDone())
            holder.itemChecked.setChecked(true);
        else
            holder.itemChecked.setChecked(false);
        holder.itemChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = holder.itemChecked.isChecked();
                if (isChecked)
                    lists.get(position).setDone(true);
                else
                    lists.get(position).setDone(false);
            }
        });
        return convertView;
    }

    public void setLists(List<ListsItemsInfos> tmpList) {
        lists = tmpList;
        notifyDataSetChanged();
    }

    public List<ListsItemsInfos> getList()
    {
        List<ListsItemsInfos> tmpList = new ArrayList<>(lists);
        return (tmpList);
    }


    private class ViewHolder {
        CheckBox itemChecked;
        TextView itemName;

        ViewHolder(View v) {
            itemChecked = (CheckBox) v.findViewById(R.id.item_checked);
            itemName = (TextView) v.findViewById(R.id.item_name);
        }
    }
}
