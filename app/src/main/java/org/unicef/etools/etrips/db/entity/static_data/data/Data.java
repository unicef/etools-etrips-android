package org.unicef.etools.etrips.db.entity.static_data.data;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;

public class Data {

    @SerializedName("partners")
    private RealmList<Partner> partners;

    @SerializedName("partnerships")
    private RealmList<PartnerShip> partnerships;

    @SerializedName("results")
    private RealmList<Result> results;

    @SerializedName("locations")
    private RealmList<Location> locations;

    private RealmList<ActionPointStatus> actionPointStatuses;

    @SerializedName("action_point_statuses")
    private List<String> actionPointStatusesArray;

    public Data() {
    }

    public Data(RealmList<Partner> partners, RealmList<PartnerShip> partnerships, RealmList<Result> results,
                RealmList<Location> locations, RealmList<ActionPointStatus> actionPointStatuses,
                List<String> actionPointStatusesArray) {
        this.partners = partners;
        this.partnerships = partnerships;
        this.results = results;
        this.locations = locations;
        this.actionPointStatuses = actionPointStatuses;
        this.actionPointStatusesArray = actionPointStatusesArray;
    }

    public RealmList<Partner> getPartners() {
        return partners;
    }

    public void setPartners(RealmList<Partner> partners) {
        this.partners = partners;
    }

    public RealmList<PartnerShip> getPartnerships() {
        return partnerships;
    }

    public void setPartnerships(RealmList<PartnerShip> partnerships) {
        this.partnerships = partnerships;
    }

    public RealmList<Result> getResults() {
        return results;
    }

    public void setResults(RealmList<Result> results) {
        this.results = results;
    }

    public RealmList<Location> getLocations() {
        return locations;
    }

    public void setLocations(RealmList<Location> locations) {
        this.locations = locations;
    }

    public RealmList<ActionPointStatus> getActionPointStatuses() {
        return actionPointStatuses;
    }

    public void setActionPointStatuses(RealmList<ActionPointStatus> actionPointStatuses) {
        this.actionPointStatuses = actionPointStatuses;
    }

    public List<String> getActionPointStatusesArray() {
        return actionPointStatusesArray;
    }

    public void setActionPointStatusesArray(List<String> actionPointStatusesArray) {
        this.actionPointStatusesArray = actionPointStatusesArray;
    }
}
