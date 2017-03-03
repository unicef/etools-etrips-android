package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ActionPoint extends RealmObject {

    public static class Status {
        public static final String OPEN = "open";
        public static final String ONGOING = "ongoing";
        public static final String COMPLETED = "completed";
        public static final String CANCELLED = "cancelled";
    }

    public static final long INVALID_ID = -1;

    public long pk;

    @Expose
    @PrimaryKey
    @SerializedName("id")
    public long id;

    @Expose
    @SerializedName("action_point_number")
    public String actionPointNumber;

    @Expose
    @SerializedName("trip_reference_number")
    public String tripReferenceNumber;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("due_date")
    public String dueDate;

    @Expose
    @SerializedName("person_responsible")
    public long personResponsible;

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("completed_at")
    public String completedAt;

    @Expose
    @SerializedName("actions_taken")
    public String actionsTaken;

    @Expose
    @SerializedName("follow_up")
    public boolean isfollowUp;

    @Expose
    @SerializedName("comments")
    public String comments;

    @Expose
    @SerializedName("created_at")
    public String createdAt;

    @Expose
    @SerializedName("assigned_by")
    public long assignedBy;

    public String assignedByFullName;

    @Expose
    @SerializedName("trip_id")
    public long tripId;

    public ActionPoint() {
    }

    public ActionPoint(long pk, long id, String actionPointNumber, String tripReferenceNumber,
                       String description, String dueDate, long personResponsible, String status,
                       String completedAt, String actionsTaken, boolean isfollowUp, String comments,
                       String createdAt, long assignedBy, long tripId) {
        this.pk = pk;
        this.id = id;
        this.actionPointNumber = actionPointNumber;
        this.tripReferenceNumber = tripReferenceNumber;
        this.description = description;
        this.dueDate = dueDate;
        this.personResponsible = personResponsible;
        this.status = status;
        this.completedAt = completedAt;
        this.actionsTaken = actionsTaken;
        this.isfollowUp = isfollowUp;
        this.comments = comments;
        this.createdAt = createdAt;
        this.assignedBy = assignedBy;
        this.tripId = tripId;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActionPointNumber() {
        return actionPointNumber;
    }

    public void setActionPointNumber(String actionPointNumber) {
        this.actionPointNumber = actionPointNumber;
    }

    public String getTripReferenceNumber() {
        return tripReferenceNumber;
    }

    public void setTripReferenceNumber(String tripReferenceNumber) {
        this.tripReferenceNumber = tripReferenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public long getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(long personResponsible) {
        this.personResponsible = personResponsible;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(String actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public boolean isfollowUp() {
        return isfollowUp;
    }

    public void setIsfollowUp(boolean isfollowUp) {
        this.isfollowUp = isfollowUp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getAssignedByFullName() {
        return assignedByFullName;
    }

    public void setAssignedByFullName(String assignedByFullName) {
        this.assignedByFullName = assignedByFullName;
    }
}