package roomies.com.roomies.models.lists;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ListInfos {
    private String title;
    private String createdAt;
    private String id;
    private int itemsDone;
    private int nbItems;

    public ListInfos(JSONObject list)
    {
        try {
            title = list.getString("title");
            createdAt = list.getString("createdAt");
            id = list.getString("id");
        }
        catch (JSONException e)
        {
            Log.e("ListInfos", e.getMessage());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItemsDone() {
        return itemsDone;
    }

    public void setItemsDone(int itemsDone) {
        this.itemsDone = itemsDone;
    }

    public int getNbItems() {
        return nbItems;
    }

    public void setNbItems(int nbItems) {
        this.nbItems = nbItems;
    }
}
