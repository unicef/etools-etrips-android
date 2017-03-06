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

    public Data2(RealmList<Currency> currencies, RealmList<Airline> airlines, RealmList<DsaRegion> dsaRegions,
                 RealmList<Country> countries, RealmList<BusinessArea> businessAreas, RealmList<Wbs> wbs,
                 RealmList<Grant> grants, RealmList<Fund> funds, RealmList<ExpenseType> expenseTypes) {
        this.currencies = currencies;
        this.airlines = airlines;
        this.dsaRegions = dsaRegions;
        this.countries = countries;
        this.businessAreas = businessAreas;
        this.wbs = wbs;
        this.grants = grants;
        this.funds = funds;
        this.expenseTypes = expenseTypes;
    }

    public RealmList<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(RealmList<Currency> currencies) {
        this.currencies = currencies;
    }

    public RealmList<Airline> getAirlines() {
        return airlines;
    }

    public void setAirlines(RealmList<Airline> airlines) {
        this.airlines = airlines;
    }

    public RealmList<DsaRegion> getDsaRegions() {
        return dsaRegions;
    }

    public void setDsaRegions(RealmList<DsaRegion> dsaRegions) {
        this.dsaRegions = dsaRegions;
    }

    public RealmList<Country> getCountries() {
        return countries;
    }

    public void setCountries(RealmList<Country> countries) {
        this.countries = countries;
    }

    public RealmList<BusinessArea> getBusinessAreas() {
        return businessAreas;
    }

    public void setBusinessAreas(RealmList<BusinessArea> businessAreas) {
        this.businessAreas = businessAreas;
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

    public RealmList<ExpenseType> getExpenseTypes() {
        return expenseTypes;
    }

    public void setExpenseTypes(RealmList<ExpenseType> expenseTypes) {
        this.expenseTypes = expenseTypes;
    }
}
