package roomies.com.roomies.controllers.secondactivity.lists.add;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import roomies.com.roomies.R;
import roomies.com.roomies.models.lists.ListsItemsInfos;

public class AddListAdapter extends BaseAdapter {

    private List<ListsItemsInfos> lists;

    AddListAdapter() {
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
            convertView = inflater.inflate(R.layout.add_list_items_infos, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // Vue recyclée
        else {
            holder = (ViewHolder) convertView.getTag();
            if ((lists.size() - 1) > (position))
            {
                holder.itemAdd.setEnabled(false);
                holder.itemAdd.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.itemAdd.setEnabled(true);
                holder.itemAdd.setVisibility(View.VISIBLE);
            }
        }
        final ListsItemsInfos list = lists.get(position);
        holder.itemName.setText(list.getTitle());
        holder.itemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemTitle = holder.itemName.getText().toString().trim();
                if (itemTitle.length() > 0) {
                    lists.get(lists.size() - 1).setTitle(itemTitle);
                    ListsItemsInfos tmpItem = new ListsItemsInfos();
                    tmpItem.setTitle("");
                    lists.add(tmpItem);
                    notifyDataSetChanged();
                }
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
        return (lists);
    }


    private class ViewHolder {
        EditText itemName;
        Button itemAdd;

        ViewHolder(View v) {
            itemName = (EditText) v.findViewById(R.id.list_item_name);
            itemAdd = (Button) v.findViewById(R.id.list_item_add);
        }
    }
}
