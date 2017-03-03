package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Itinerary extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("origin")
    private String origin;

    @Expose
    @SerializedName("destination")
    private String destination;

    @Expose
    @SerializedName("departure_date")
    private String departureDate;

    @Expose
    @SerializedName("arrival_date")
    private String arrivalDate;

    @Expose
    @SerializedName("dsa_region")
    private String dsaRegion;

    @Expose
    @SerializedName("overnight_travel")
    private boolean isOvernightTravel;

    @Expose
    @SerializedName("mode_of_travel")
    private String modeOfTravel;

    public Itinerary() {
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDsaRegion() {
        return dsaRegion;
    }

    public void setDsaRegion(String dsaRegion) {
        this.dsaRegion = dsaRegion;
    }

    public boolean isOvernightTravel() {
        return isOvernightTravel;
    }

    public void setOvernightTravel(boolean overnightTravel) {
        this.isOvernightTravel = overnightTravel;
    }

    public String getModeOfTravel() {
        return modeOfTravel;
    }

    public void setModeOfTravel(String modeOfTravel) {
        this.modeOfTravel = modeOfTravel;
    }
}
