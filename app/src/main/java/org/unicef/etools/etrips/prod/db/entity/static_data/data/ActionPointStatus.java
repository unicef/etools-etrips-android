package org.unicef.etools.etrips.prod.db.entity.static_data.data;


import android.support.annotation.NonNull;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ActionPointStatus extends RealmObject {

    @PrimaryKey
    private String status;

    public ActionPointStatus() {
    }

    public ActionPointStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    public static CharSequence[] obtainNames(List<ActionPointStatus> statuses) {
        CharSequence[] names = new CharSequence[statuses.size()];
        for (int i = 0; i < statuses.size(); ++i) {
            names[i] = statuses.get(i).getStatus();
        }
        return names;
    }
}
