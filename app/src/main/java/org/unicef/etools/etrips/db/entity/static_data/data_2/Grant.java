package org.unicef.etools.etrips.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Grant extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("wbs")
    private long wbs;

    public Grant() {
    }

    public Grant(long id, String name, long wbs) {
        this.id = id;
        this.name = name;
        this.wbs = wbs;
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

    public long getWbs() {
        return wbs;
    }

    public void setWbs(long wbs) {
        this.wbs = wbs;
    }
}
