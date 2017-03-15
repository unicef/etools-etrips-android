package org.unicef.etools.etrips.prod.io.rest.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.unicef.etools.etrips.prod.db.entity.trip.Trip;

import java.util.ArrayList;

public class TripListResponse {

    @Expose
    @SerializedName("page_count")
    public int pageCount;

    @Expose
    @SerializedName("data")
    public ArrayList<Trip> trips;

    @Expose
    @SerializedName("total_count")
    public int totalCount;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "TripListResponse{" +
                "pageCount=" + pageCount +
                ", actionPoints=" + trips +
                ", totalCount=" + totalCount +
                '}';
    }
}
