package org.unicef.etools.etrips.db.entity.static_data.data;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PartnerShip extends RealmObject{

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("partner")
    private long partner;

    public PartnerShip() {
    }

    public PartnerShip(long id, String name, long partner) {
        this.id = id;
        this.name = name;
        this.partner = partner;
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

    public long getPartner() {
        return partner;
    }

    public void setPartner(long partner) {
        this.partner = partner;
    }
}
