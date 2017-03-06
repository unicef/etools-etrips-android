package org.unicef.etools.etrips.prod.db.entity.user;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserStatic extends RealmObject {

    @PrimaryKey
    @SerializedName("user_id")
    private long id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    public UserStatic() {
    }

    public UserStatic(long id, String fullName, String username, String email) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
