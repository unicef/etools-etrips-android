package org.unicef.etools.etrips.prod.db.entity.trip;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TripReport implements Parcelable {

    private String description;
    private ArrayList<Attachment> reportPhotos;

    public TripReport() {
    }

    public TripReport(String description, ArrayList<Attachment> reportPhotos) {
        this.description = description;
        this.reportPhotos = reportPhotos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Attachment> getReportPhotos() {
        return reportPhotos;
    }

    public void setReportPhotos(ArrayList<Attachment> reportPhotos) {
        this.reportPhotos = reportPhotos;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeList(this.reportPhotos);
    }

    protected TripReport(Parcel in) {
        this.description = in.readString();
        this.reportPhotos = new ArrayList<Attachment>();
        in.readList(this.reportPhotos, Attachment.class.getClassLoader());
    }

    public static final Creator<TripReport> CREATOR = new Creator<TripReport>() {
        @Override
        public TripReport createFromParcel(Parcel source) {
            return new TripReport(source);
        }

        @Override
        public TripReport[] newArray(int size) {
            return new TripReport[size];
        }
    };
}
