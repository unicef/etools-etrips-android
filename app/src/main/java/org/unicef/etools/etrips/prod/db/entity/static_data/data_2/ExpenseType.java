package org.unicef.etools.etrips.prod.db.entity.static_data.data_2;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ExpenseType extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("vendor_number")
    private String vendorNumber;

    @SerializedName("unique")
    private boolean isUnique;

    public ExpenseType() {
    }

    public ExpenseType(long id, String name, String vendorNumber, boolean isUnique) {
        this.id = id;
        this.name = name;
        this.vendorNumber = vendorNumber;
        this.isUnique = isUnique;
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

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }
}
