package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Deduction extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("breakfast")
    private boolean isBreakfast;

    @Expose
    @SerializedName("lunch")
    private boolean isLunch;

    @Expose
    @SerializedName("dinner")
    private boolean isDinner;

    @Expose
    @SerializedName("accomodation")
    private boolean isAccomodation;

    @Expose
    @SerializedName("no_dsa")
    private boolean isNoDsa;

    @Expose
    @SerializedName("day_of_week")
    private String dayOfWeek;

    public Deduction() {
    }

    public Deduction(long id, String date, boolean isBreakfast, boolean isLunch, boolean isDinner,
                     boolean isAccomodation, boolean isNoDsa, String dayOfWeek) {
        this.id = id;
        this.date = date;
        this.isBreakfast = isBreakfast;
        this.isLunch = isLunch;
        this.isDinner = isDinner;
        this.isAccomodation = isAccomodation;
        this.isNoDsa = isNoDsa;
        this.dayOfWeek = dayOfWeek;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isBreakfast() {
        return isBreakfast;
    }

    public void setBreakfast(boolean breakfast) {
        isBreakfast = breakfast;
    }

    public boolean isLunch() {
        return isLunch;
    }

    public void setLunch(boolean lunch) {
        isLunch = lunch;
    }

    public boolean isDinner() {
        return isDinner;
    }

    public void setDinner(boolean dinner) {
        isDinner = dinner;
    }

    public boolean isAccomodation() {
        return isAccomodation;
    }

    public void setAccomodation(boolean accomodation) {
        isAccomodation = accomodation;
    }

    public boolean isNoDsa() {
        return isNoDsa;
    }

    public void setNoDsa(boolean noDsa) {
        isNoDsa = noDsa;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
