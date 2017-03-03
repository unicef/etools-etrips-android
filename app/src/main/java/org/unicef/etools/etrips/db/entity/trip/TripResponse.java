package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TripResponse {

    @SerializedName("page_count")
    public int pageCount;

    @SerializedName("total_count")
    public int total_count;

    @SerializedName("data")
    public ArrayList<Trip> trips;

    public TripResponse() {
    }

    public TripResponse(int pageCount, int total_count, ArrayList<Trip> trips) {
        this.pageCount = pageCount;
        this.total_count = total_count;
        this.trips = trips;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }
}
