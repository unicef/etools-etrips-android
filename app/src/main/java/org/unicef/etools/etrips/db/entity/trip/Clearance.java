package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Clearance extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("medical_clearance")
    private String medicalClearance;

    @Expose
    @SerializedName("security_clearance")
    private String securityClearance;

    @Expose
    @SerializedName("security_course")
    private String securityCourse;

    public Clearance() {
    }

    public Clearance(long id, String medicalClearance, String securityClearance, String securityCourse) {
        this.id = id;
        this.medicalClearance = medicalClearance;
        this.securityClearance = securityClearance;
        this.securityCourse = securityCourse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedicalClearance() {
        return medicalClearance;
    }

    public void setMedicalClearance(String medicalClearance) {
        this.medicalClearance = medicalClearance;
    }

    public String getSecurityClearance() {
        return securityClearance;
    }

    public void setSecurityClearance(String securityClearance) {
        this.securityClearance = securityClearance;
    }

    public String getSecurityCourse() {
        return securityCourse;
    }

    public void setSecurityCourse(String securityCourse) {
        this.securityCourse = securityCourse;
    }
}
