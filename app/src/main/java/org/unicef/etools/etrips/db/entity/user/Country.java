package org.unicef.etools.etrips.db.entity.user;


import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("name")
    private String name;

    @SerializedName("business_area_code")
    private String business_area_code;

    @SerializedName("id")
    private long id;

    public Country() {
    }

    public Country(String name, String business_area_code, long id) {
        this.name = name;
        this.business_area_code = business_area_code;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness_area_code() {
        return business_area_code;
    }

    public void setBusiness_area_code(String business_area_code) {
        this.business_area_code = business_area_code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
