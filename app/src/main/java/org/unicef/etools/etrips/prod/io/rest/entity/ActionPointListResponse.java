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
        return "ActionPointListResponse{" +
                "pageCount=" + pageCount +
                ", actionPoints=" + actionPoints +
                ", totalCount=" + totalCount +
                '}';
    }
}
