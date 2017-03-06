package org.unicef.etools.etrips.prod.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Currency extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("iso_4217")
    private String iso4217;

    @SerializedName("exchange_to_dollar")
    private double exchangeToDollar;

    public Currency() {
    }

    public Currency(long id, String name, String code, String iso4217, double exchangeToDollar) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.iso4217 = iso4217;
        this.exchangeToDollar = exchangeToDollar;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIso4217() {
        return iso4217;
    }

    public void setIso4217(String iso4217) {
        this.iso4217 = iso4217;
    }

    public double getExchangeToDollar() {
        return exchangeToDollar;
    }

    public void setExchangeToDollar(double exchangeToDollar) {
        this.exchangeToDollar = exchangeToDollar;
    }
}
