package org.unicef.etools.etrips.prod.io.rest.entity;

import com.google.gson.annotations.SerializedName;

import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;

import java.util.ArrayList;

public class ActionPointListResponse {

    @SerializedName("page_count")
    public int pageCount;

    @SerializedName("data")
    public ArrayList<ActionPoint> actionPoints;

    @SerializedName("total_count")
    public int totalCount;

    @Override
    public String toString() {
        return "ActionPointListResponse{" +
                "pageCount=" + pageCount +
                ", actionPoints=" + actionPoints +
                ", totalCount=" + totalCount +
                '}';
    }
}
