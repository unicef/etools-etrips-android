package org.unicef.etools.etrips.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Fund extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("grant")
    private long grant;

    public Fund() {
    }

    public Fund(long id, String name, long grant) {
        this.id = id;
        this.name = name;
        this.grant = grant;
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

    public long getGrant() {
        return grant;
    }

    public void setGrant(long grant) {
        this.grant = grant;
    }
}
