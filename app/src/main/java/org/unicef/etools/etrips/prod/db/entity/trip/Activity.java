package org.unicef.etools.etrips.prod.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Activity extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("travel_type")
    private String travelType;

    @Expose
    @SerializedName("partner")
    private long partner;

    @Expose
    @SerializedName("partnership")
    private long partnerShip;

    @Expose
    @SerializedName("result")
    private long result;

    @Expose
    @SerializedName("is_primary_traveler")
    private boolean isPrimaryTraveler;

    @Expose
    @SerializedName("primary_traveler")
    private int primaryTraveler;

    @Expose
    @SerializedName("date")
    private String date;

    public Activity() {
    }

    public Activity(long id, String travelType, long partner, long partnerShip, long result, boolean isPrimaryTraveler, String date) {
        this.id = id;
        this.travelType = travelType;
        this.partner = partner;
        this.partnerShip = partnerShip;
        this.result = result;
        this.isPrimaryTraveler = isPrimaryTraveler;
        this.date = date;
    }

    public Activity(long id, String travelType, long partner, long partnerShip, long result, boolean isPrimaryTraveler, int primaryTraveler, String date) {
        this.id = id;
        this.travelType = travelType;
        this.partner = partner;
        this.partnerShip = partnerShip;
        this.result = result;
        this.isPrimaryTraveler = isPrimaryTraveler;
        this.primaryTraveler = primaryTraveler;
        this.date = date;
    }

    public int getPrimaryTraveler() {
        return primaryTraveler;
    }

    public void setPrimaryTraveler(int primaryTraveler) {
        this.primaryTraveler = primaryTraveler;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public long getPartner() {
        return partner;
    }

    public void setPartner(long partner) {
        this.partner = partner;
    }

    public long getPartnerShip() {
        return partnerShip;
    }

    public void setPartnerShip(long partnerShip) {
        this.partnerShip = partnerShip;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    public boolean isPrimaryTraveler() {
        return isPrimaryTraveler;
    }

    public void setPrimaryTraveler(boolean primaryTraveler) {
        this.isPrimaryTraveler = primaryTraveler;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
