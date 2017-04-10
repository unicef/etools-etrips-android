package org.unicef.etools.etrips.prod.db.entity.user;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Travel2Field extends RealmObject {

    @PrimaryKey
    private long pk;

    @SerializedName("travel_count")
    private long travelCount;

    @SerializedName("business_area")
    private long businessArea;

    public Travel2Field() {
    }

    public Travel2Field(long pk, long travelCount, long businessArea) {
        this.pk = pk;
        this.travelCount = travelCount;
        this.businessArea = businessArea;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getTravelCount() {
        return travelCount;
    }

    public void setTravelCount(long travelCount) {
        this.travelCount = travelCount;
    }

    public long getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(long businessArea) {
        this.businessArea = businessArea;
    }
}
