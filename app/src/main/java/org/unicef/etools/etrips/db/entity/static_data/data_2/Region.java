package org.unicef.etools.etrips.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Region extends RealmObject {

    @PrimaryKey
    @SerializedName("code")
    private long code;

    @SerializedName("name")
    private String name;

    public Region() {
    }

    public Region(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
