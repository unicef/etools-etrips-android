package org.unicef.etools.etrips.db.entity.trip;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ReportPhoto implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("file")
    private String file;

    @SerializedName("type")
    private int type;

    private Uri uri;

    @SerializedName("type_name")
    private String typeName;

    @SerializedName("caption")
    private String caption;

    @SerializedName("trip")
    private long trip;

    public ReportPhoto() {
    }

    public ReportPhoto(Uri uri, String caption) {
        this.uri = uri;
        this.caption = caption;
    }

    public ReportPhoto(long id, String file, int type, Uri uri, String typeName, String caption, long trip) {
        this.id = id;
        this.file = file;
        this.type = type;
        this.uri = uri;
        this.typeName = typeName;
        this.caption = caption;
        this.trip = trip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getTrip() {
        return trip;
    }

    public void setTrip(long trip) {
        this.trip = trip;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.file);
        dest.writeInt(this.type);
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.typeName);
        dest.writeString(this.caption);
        dest.writeLong(this.trip);
    }

    protected ReportPhoto(Parcel in) {
        this.id = in.readLong();
        this.file = in.readString();
        this.type = in.readInt();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.typeName = in.readString();
        this.caption = in.readString();
        this.trip = in.readLong();
    }

    public static final Creator<ReportPhoto> CREATOR = new Creator<ReportPhoto>() {
        @Override
        public ReportPhoto createFromParcel(Parcel source) {
            return new ReportPhoto(source);
        }

        @Override
        public ReportPhoto[] newArray(int size) {
            return new ReportPhoto[size];
        }
    };
}
