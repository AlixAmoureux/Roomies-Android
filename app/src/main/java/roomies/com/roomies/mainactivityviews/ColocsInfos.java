package roomies.com.roomies.mainactivityviews;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xila on 07/12/2016.
 */

public class ColocsInfos implements Parcelable
{
    private String title;
    private String description;
    private String picturePath;
    private String createdAt;
    private long id;

    public ColocsInfos(){}

    public ColocsInfos(JSONObject json) throws JSONException
    {
        this.title = json.getString("title");
        this.picturePath = json.getString("picturePath");
        this.id = json.getLong("id");
        this.description = json.getString("description");
        // TODO createdAt
    }

    protected ColocsInfos(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.picturePath = in.readString();
        this.description = in.readString();
        this.createdAt = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.id);
        out.writeString(this.title);
        out.writeString(this.picturePath);
        out.writeString(this.description);
        out.writeString(this.createdAt);
    }

    public static final Parcelable.Creator<ColocsInfos> CREATOR = new Parcelable.Creator<ColocsInfos>() {
        public ColocsInfos createFromParcel(Parcel in) {
            return new ColocsInfos(in);
        }

        public ColocsInfos[] newArray(int size) {
            return new ColocsInfos[size];
        }
    };
}
