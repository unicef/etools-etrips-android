package org.unicef.etools.etrips.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Country extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("long_name")
    private String longName;

    @SerializedName("business_area")
    private long businessArea;

    @SerializedName("vision_code")
    private String visionCode;

    @SerializedName("iso_2")
    private String iso2;

    @SerializedName("iso_3")
    private String iso3;

    @SerializedName("currency")
    private double currency;

    @SerializedName("valid_from")
    private String validFrom;

    @SerializedName("valid_to")
    private String validTo;

    public Country() {
    }

    public Country(long id, String name, String longName, long businessArea, String visionCode,
                   String iso2, String iso3, double currency, String validFrom, String validTo) {
        this.id = id;
        this.name = name;
        this.longName = longName;
        this.businessArea = businessArea;
        this.visionCode = visionCode;
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.currency = currency;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public long getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(long businessArea) {
        this.businessArea = businessArea;
    }

    public String getVisionCode() {
        return visionCode;
    }

    public void setVisionCode(String visionCode) {
        this.visionCode = visionCode;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public double getCurrency() {
        return currency;
    }

    public void setCurrency(double currency) {
        this.currency = currency;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
}
