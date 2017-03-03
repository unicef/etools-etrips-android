package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CostAssignment extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("share")
    private long share;

    @Expose
    @SerializedName("grant")
    private long grant;

    @Expose
    @SerializedName("grant_name")
    private String grantName;

    @Expose
    @SerializedName("wbs")
    private long wbs;

    @Expose
    @SerializedName("wbs_name")
    private String wbsName;

    @Expose
    @SerializedName("fund")
    private long fund;

    @Expose
    @SerializedName("business_area")
    private long businessArea;

    @Expose
    @SerializedName("delegate")
    private boolean isDelegate;

    public CostAssignment() {
    }

    public CostAssignment(long id, long share, long grant, String grantName, long wbs, String wbsName,
                          long fund, long businessArea, boolean isDelegate) {
        this.id = id;
        this.share = share;
        this.grant = grant;
        this.grantName = grantName;
        this.wbs = wbs;
        this.wbsName = wbsName;
        this.fund = fund;
        this.businessArea = businessArea;
        this.isDelegate = isDelegate;
    }

    public double getShare() {
        return share;
    }

    public void setShare(long share) {
        this.share = share;
    }

    public long getGrant() {
        return grant;
    }

    public void setGrant(long grant) {
        this.grant = grant;
    }

    public String getGrantName() {
        return grantName;
    }

    public void setGrantName(String grantName) {
        this.grantName = grantName;
    }

    public long getWbs() {
        return wbs;
    }

    public void setWbs(long wbs) {
        this.wbs = wbs;
    }

    public String getWbsName() {
        return wbsName;
    }

    public void setWbsName(String wbsName) {
        this.wbsName = wbsName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFund() {
        return fund;
    }

    public void setFund(long fund) {
        this.fund = fund;
    }

    public long getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(long businessArea) {
        this.businessArea = businessArea;
    }

    public boolean isDelegate() {
        return isDelegate;
    }

    public void setDelegate(boolean delegate) {
        isDelegate = delegate;
    }
}
