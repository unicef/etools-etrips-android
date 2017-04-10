package org.unicef.etools.etrips.prod.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;

public class Data2 {

    @SerializedName("currencies")
    private RealmList<Currency> currencies;

    @SerializedName("airlines")
    private RealmList<Airline> airlines;

    @SerializedName("dsa_regions")
    private RealmList<DsaRegion> dsaRegions;

    @SerializedName("countries")
    private RealmList<Country> countries;

    @SerializedName("business_areas")
    private RealmList<BusinessArea> businessAreas;

    @SerializedName("wbs")
    private RealmList<Wbs> wbs;

    @SerializedName("grants")
    private RealmList<Grant> grants;

    @SerializedName("funds")
    private RealmList<Fund> funds;

    @SerializedName("expense_types")
    private RealmList<ExpenseType> expenseTypes;

    public Data2() {
    }

    public Data2(RealmList<Wbs> wbs, RealmList<Grant> grants, RealmList<Fund> funds) {
        this.wbs = wbs;
        this.grants = grants;
        this.funds = funds;
    }

    public RealmList<Wbs> getWbs() {
        return wbs;
    }

    public void setWbs(RealmList<Wbs> wbs) {
        this.wbs = wbs;
    }

    public RealmList<Grant> getGrants() {
        return grants;
    }

    public void setGrants(RealmList<Grant> grants) {
        this.grants = grants;
    }

    public RealmList<Fund> getFunds() {
        return funds;
    }

    public void setFunds(RealmList<Fund> funds) {
        this.funds = funds;
    }

}
