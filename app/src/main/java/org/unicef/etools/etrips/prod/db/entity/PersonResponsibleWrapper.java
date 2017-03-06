package org.unicef.etools.etrips.prod.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonResponsibleWrapper implements Parcelable {

    private final long id;
    private final String name;

    public PersonResponsibleWrapper(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
    }

    protected PersonResponsibleWrapper(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    public static final Creator<PersonResponsibleWrapper> CREATOR = new Creator<PersonResponsibleWrapper>() {
        @Override
        public PersonResponsibleWrapper createFromParcel(Parcel source) {
            return new PersonResponsibleWrapper(source);
        }

        @Override
        public PersonResponsibleWrapper[] newArray(int size) {
            return new PersonResponsibleWrapper[size];
        }
    };
}
