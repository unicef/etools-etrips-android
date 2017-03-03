package org.unicef.etools.etrips.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DsaRegion extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("country")
    private long country;

    @SerializedName("area_name")
    private String areaName;

    @SerializedName("area_code")
    private String areaCode;

    @SerializedName("dsa_amount_usd")
    private double dsaAmountUsd;

    @SerializedName("dsa_amount_60plus_usd")
    private double dsaAmount60PlusUsd;

    @SerializedName("dsa_amount_local")
    private double dsaAmountLocal;

    @SerializedName("dsa_amount_60plus_local")
    private double dsaAmount60PlusLocal;

    @SerializedName("room_rate")
    private double roomRate;

    @SerializedName("finalization_date")
    private String finalizationDate;

    @SerializedName("eff_date")
    private String effDate;

    @SerializedName("unique_id")
    private String uniqueId;

    @SerializedName("unique_name")
    private String uniqueName;

    @SerializedName("label")
    private String label;

    @SerializedName("long_name")
    private String longName;

    public DsaRegion() {
    }

    public DsaRegion(long id, long country, String areaName, String areaCode, double dsaAmountUsd,
                     double dsaAmount60PlusUsd, double dsaAmountLocal, double dsaAmount60PlusLocal,
                     double roomRate, String finalizationDate, String effDate, String uniqueId,
                     String uniqueName, String label, String longName) {
        this.id = id;
        this.country = country;
        this.areaName = areaName;
        this.areaCode = areaCode;
        this.dsaAmountUsd = dsaAmountUsd;
        this.dsaAmount60PlusUsd = dsaAmount60PlusUsd;
        this.dsaAmountLocal = dsaAmountLocal;
        this.dsaAmount60PlusLocal = dsaAmount60PlusLocal;
        this.roomRate = roomRate;
        this.finalizationDate = finalizationDate;
        this.effDate = effDate;
        this.uniqueId = uniqueId;
        this.uniqueName = uniqueName;
        this.label = label;
        this.longName = longName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCountry() {
        return country;
    }

    public void setCountry(long country) {
        this.country = country;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public double getDsaAmountUsd() {
        return dsaAmountUsd;
    }

    public void setDsaAmountUsd(double dsaAmountUsd) {
        this.dsaAmountUsd = dsaAmountUsd;
    }

    public double getDsaAmount60PlusUsd() {
        return dsaAmount60PlusUsd;
    }

    public void setDsaAmount60PlusUsd(double dsaAmount60PlusUsd) {
        this.dsaAmount60PlusUsd = dsaAmount60PlusUsd;
    }

    public double getDsaAmountLocal() {
        return dsaAmountLocal;
    }

    public void setDsaAmountLocal(double dsaAmountLocal) {
        this.dsaAmountLocal = dsaAmountLocal;
    }

    public double getDsaAmount60PlusLocal() {
        return dsaAmount60PlusLocal;
    }

    public void setDsaAmount60PlusLocal(double dsaAmount60PlusLocal) {
        this.dsaAmount60PlusLocal = dsaAmount60PlusLocal;
    }

    public double getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(double roomRate) {
        this.roomRate = roomRate;
    }

    public String getFinalizationDate() {
        return finalizationDate;
    }

    public void setFinalizationDate(String finalizationDate) {
        this.finalizationDate = finalizationDate;
    }

    public String getEffDate() {
        return effDate;
    }

    public void setEffDate(String effDate) {
        this.effDate = effDate;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
