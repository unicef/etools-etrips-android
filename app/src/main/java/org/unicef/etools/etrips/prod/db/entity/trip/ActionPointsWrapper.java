package org.unicef.etools.etrips.prod.db.entity.trip;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActionPointsWrapper {

    @SerializedName("action_points")
    private List<ActionPoint> actionPoints;

    public ActionPointsWrapper(List<ActionPoint> actionPointList) {
        this.actionPoints = actionPointList;
    }
}
