package org.unicef.etools.etrips.db.entity.trip;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripLocation implements Parcelable {

    @Expose
    @SerializedName("governorate")
    private String governorate;

    @Expose
    @SerializedName("region")
    private String region;

    @Expose
    @SerializedName("locality")
    private long locality;

    @Expose
    @SerializedName("location")
    private long location;

    public TripLocation() {
    }

    public TripLocation(String governorate, String region, long locality, long location) {
        this.governorate = governorate;
        this.region = region;
        this.locality = locality;
        this.location = location;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getLocality() {
        return locality;
    }

    public void setLocality(long locality) {
        this.locality = locality;
    }

    public long getLocation() {
        return location;
    }

    public void setLocation(long location) {
        this.location = location;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.governorate);
        dest.writeString(this.region);
        dest.writeLong(this.locality);
        dest.writeLong(this.location);
    }

    protected TripLocation(Parcel in) {
        this.governorate = in.readString();
        this.region = in.readString();
        this.locality = in.readLong();
        this.location = in.readLong();
    }

    public static final Parcelable.Creator<TripLocation> CREATOR = new Parcelable.Creator<TripLocation>() {
        @Override
        public TripLocation createFromParcel(Parcel source) {
            return new TripLocation(source);
        }

        @Override
        public TripLocation[] newArray(int size) {
            return new TripLocation[size];
        }
    };
}
