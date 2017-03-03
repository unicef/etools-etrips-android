package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Dsa extends RealmObject {

    @Expose
    @SerializedName("start_date")
    private String startDate;

    @Expose
    @SerializedName("end_date")
    private String endDate;

    @Expose
    @SerializedName("daily_rate_usd")
    private String dailyRateUsd;

    @Expose
    @SerializedName("night_count")
    private long nightCount;

    @Expose
    @SerializedName("amount_usd")
    private String amountUsd;

    @Expose
    @SerializedName("dsa_region")
    private long dsaRegion;

    @Expose
    @SerializedName("dsa_region_name")
    private String dsaRegionName;

    public Dsa() {
    }

    public Dsa(String startDate, String endDate, String dailyRateUsd, long nightCount, String amountUsd,
               long dsaRegion, String dsaRegionName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRateUsd = dailyRateUsd;
        this.nightCount = nightCount;
        this.amountUsd = amountUsd;
        this.dsaRegion = dsaRegion;
        this.dsaRegionName = dsaRegionName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDailyRateUsd() {
        return dailyRateUsd;
    }

    public void setDailyRateUsd(String dailyRateUsd) {
        this.dailyRateUsd = dailyRateUsd;
    }

    public long getNightCount() {
        return nightCount;
    }

    public void setNightCount(long nightCount) {
        this.nightCount = nightCount;
    }

    public String getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(String amountUsd) {
        this.amountUsd = amountUsd;
    }

    public long getDsaRegion() {
        return dsaRegion;
    }

    public void setDsaRegion(long dsaRegion) {
        this.dsaRegion = dsaRegion;
    }

    public String getDsaRegionName() {
        return dsaRegionName;
    }

    public void setDsaRegionName(String dsaRegionName) {
        this.dsaRegionName = dsaRegionName;
    }
}
