package org.unicef.etools.etrips.prod.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BusinessArea extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("default_currency")
    private long defaultCurrency;

    @SerializedName("region")
    private Region region;

    public BusinessArea() {
    }

    public BusinessArea(long id, String name, String code, long defaultCurrency, Region region) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.defaultCurrency = defaultCurrency;
        this.region = region;
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

    public long getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(long defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
