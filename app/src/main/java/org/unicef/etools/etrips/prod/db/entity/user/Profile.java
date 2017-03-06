package org.unicef.etools.etrips.prod.db.entity.user;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.realm.RealmObject;

public class Profile extends RealmObject {

    @SerializedName("office")
    private String office;

    @SerializedName("section")
    private String section;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("partner_staff_member")
    private String partnerStaffMember;

    @SerializedName("job_title")
    private String jobTitle;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("installation_id")
    private String installationId;

    @SerializedName("country")
    private long country;

    @SerializedName("country_override")
    private String countryOverride;

    // FIXME: server returns array of objects instead of string.
    /*@SerializedName("countries_available")
    @Ignore
    private ArrayList<String> availableCountryList;*/

    public Profile() {
    }

    public Profile(String office, String section, String countryName, String partnerStaffMember,
                   String jobTitle, String phoneNumber, String installationId, long country,
                   String countryOverride, ArrayList<String> availableCountryList) {
        this.office = office;
        this.section = section;
        this.countryName = countryName;
        this.partnerStaffMember = partnerStaffMember;
        this.jobTitle = jobTitle;
        this.phoneNumber = phoneNumber;
        this.installationId = installationId;
        this.country = country;
        this.countryOverride = countryOverride;
        //this.availableCountryList = availableCountryList;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPartnerStaffMember() {
        return partnerStaffMember;
    }

    public void setPartnerStaffMember(String partnerStaffMember) {
        this.partnerStaffMember = partnerStaffMember;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public long getCountry() {
        return country;
    }

    public void setCountry(long country) {
        this.country = country;
    }

    public String getCountryOverride() {
        return countryOverride;
    }

    public void setCountryOverride(String countryOverride) {
        this.countryOverride = countryOverride;
    }

    /*public ArrayList<String> getAvailableCountryList() {
        return availableCountryList;
    }

    public void setAvailableCountryList(ArrayList<String> availableCountryList) {
        this.availableCountryList = availableCountryList;
    }*/

}
