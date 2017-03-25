package org.unicef.etools.etrips.prod.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class LocalTrip extends RealmObject {

    @Ignore
    public long pk;

    @PrimaryKey
    @Expose
    @SerializedName("id")
    public long id;

    @Expose
    @SerializedName("reference_number")
    public String referenceNumber;

    // here saved traveler id
    @Expose(deserialize = false)
    public long traveler;

    private boolean isMyTrip;

    public String travelerName;

    @Expose
    @SerializedName("purpose")
    public String purpose;

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("section")
    public int section;

    @Expose
    @SerializedName("office")
    public int office;

    @Expose
    @SerializedName("start_date")
    public String startDate;

    @Expose
    @SerializedName("end_date")
    public String endDate;

    @Expose
    @SerializedName("supervisor")
    public long supervisor;

    @Expose
    @SerializedName("supervisor_name")
    public String supervisorName;

    @Expose
    @SerializedName("international_travel")
    public boolean isInternationalTravel;

    @Expose
    @SerializedName("ta_required")
    public boolean isTaRequired;

    @Expose
    @SerializedName("estimated_travel_cost")
    public double estimatedTravelCost;

    @Expose
    @SerializedName("currency")
    public long currency;

    @Expose
    @SerializedName("completed_at")
    public String completedAt;

    @Expose
    @SerializedName("canceled_at")
    public String canceledAt;

    @Expose
    @SerializedName("rejection_note")
    public String rejectionNote;

    @Expose
    @SerializedName("cancellation_note")
    public String cancellationNote;

    @Expose
    @SerializedName("certification_note")
    public String certificationNote;

    @Expose
    @SerializedName("report")
    public String report;

    @Expose
    @SerializedName("additional_note")
    public String additionalNote;

    @Expose
    @SerializedName("misc_expenses")
    public String miscExpenses;

    @Expose
    @SerializedName("itinerary")
    public RealmList<Itinerary> itineraries;

    @Expose
    @SerializedName("expenses")
    public RealmList<Expense> expenses;

    @Expose
    @SerializedName("deductions")
    public RealmList<Deduction> deductions;

    @Expose
    @SerializedName("cost_assignments")
    public RealmList<CostAssignment> costAssignments;

    @Expose
    @SerializedName("clearances")
    public Clearance clearances;

    @Expose
    @SerializedName("activities")
    public RealmList<Activity> activities;

    @Expose
    @SerializedName("attachments")
    public RealmList<Attachment> attachments;

    @Expose
    @SerializedName("cost_summary")
    public CostSummary costSummary;

    @Expose
    @SerializedName("action_points")
    public RealmList<ActionPoint> actionPoints;

    public LocalTrip() {
    }

    public LocalTrip(long pk, long id, String referenceNumber, long traveler, boolean isMyTrip,
                     String travelerName, String purpose, String status, int section, int office, String startDate,
                     String endDate, long supervisor, String supervisorName, boolean isInternationalTravel,
                     boolean isTaRequired, double estimatedTravelCost, long currency, String completedAt, String canceledAt,
                     String rejectionNote, String cancellationNote, String certificationNote, String report,
                     String additionalNote, String miscExpenses, RealmList<Itinerary> itineraries, RealmList<Expense> expenses,
                     RealmList<Deduction> deductions, RealmList<CostAssignment> costAssignments, Clearance clearances,
                     RealmList<Activity> activities, RealmList<Attachment> attachments, CostSummary costSummary,
                     RealmList<ActionPoint> actionPoints) {
        this.pk = pk;
        this.id = id;
        this.referenceNumber = referenceNumber;
        this.traveler = traveler;
        this.isMyTrip = isMyTrip;
        this.travelerName = travelerName;
        this.purpose = purpose;
        this.status = status;
        this.section = section;
        this.office = office;
        this.startDate = startDate;
        this.endDate = endDate;
        this.supervisor = supervisor;
        this.supervisorName = supervisorName;
        this.isInternationalTravel = isInternationalTravel;
        this.isTaRequired = isTaRequired;
        this.estimatedTravelCost = estimatedTravelCost;
        this.currency = currency;
        this.completedAt = completedAt;
        this.canceledAt = canceledAt;
        this.rejectionNote = rejectionNote;
        this.cancellationNote = cancellationNote;
        this.certificationNote = certificationNote;
        this.report = report;
        this.additionalNote = additionalNote;
        this.miscExpenses = miscExpenses;
        this.itineraries = itineraries;
        this.expenses = expenses;
        this.deductions = deductions;
        this.costAssignments = costAssignments;
        this.clearances = clearances;
        this.activities = activities;
        this.attachments = attachments;
        this.costSummary = costSummary;
        this.actionPoints = actionPoints;
    }

    public boolean isMyTrip() {
        return isMyTrip;
    }

    public void setMyTrip(boolean myTrip) {
        isMyTrip = myTrip;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public long getTraveler() {
        return traveler;
    }

    public void setTraveler(long traveler) {
        this.traveler = traveler;
    }

    public String getTravelerName() {
        return travelerName;
    }

    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getOffice() {
        return office;
    }

    public void setOffice(int office) {
        this.office = office;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(long supervisor) {
        this.supervisor = supervisor;
    }

    public boolean isInternationalTravel() {
        return isInternationalTravel;
    }

    public void setInternationalTravel(boolean internationalTravel) {
        isInternationalTravel = internationalTravel;
    }

    public boolean isTaRequired() {
        return isTaRequired;
    }

    public void setTaRequired(boolean taRequired) {
        isTaRequired = taRequired;
    }

    public double getEstimatedTravelCost() {
        return estimatedTravelCost;
    }

    public void setEstimatedTravelCost(double estimatedTravelCost) {
        this.estimatedTravelCost = estimatedTravelCost;
    }

    public long getCurrency() {
        return currency;
    }

    public void setCurrency(long currency) {
        this.currency = currency;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(String canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }

    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }

    public String getCancellationNote() {
        return cancellationNote;
    }

    public void setCancellationNote(String cancellationNote) {
        this.cancellationNote = cancellationNote;
    }

    public String getCertificationNote() {
        return certificationNote;
    }

    public void setCertificationNote(String certificationNote) {
        this.certificationNote = certificationNote;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getMiscExpenses() {
        return miscExpenses;
    }

    public void setMiscExpenses(String miscExpenses) {
        this.miscExpenses = miscExpenses;
    }

    public RealmList<Itinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(RealmList<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    public RealmList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(RealmList<Expense> expenses) {
        this.expenses = expenses;
    }

    public RealmList<Deduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(RealmList<Deduction> deductions) {
        this.deductions = deductions;
    }

    public RealmList<CostAssignment> getCostAssignments() {
        return costAssignments;
    }

    public void setCostAssignments(RealmList<CostAssignment> costAssignments) {
        this.costAssignments = costAssignments;
    }

    public Clearance getClearances() {
        return clearances;
    }

    public void setClearances(Clearance clearances) {
        this.clearances = clearances;
    }

    public RealmList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(RealmList<Activity> activities) {
        this.activities = activities;
    }

    public RealmList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(RealmList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public CostSummary getCostSummary() {
        return costSummary;
    }

    public void setCostSummary(CostSummary costSummary) {
        this.costSummary = costSummary;
    }

    public RealmList<ActionPoint> getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(RealmList<ActionPoint> actionPoints) {
        this.actionPoints = actionPoints;
    }

    public static LocalTrip buildFromTrip(Trip trip) {
        return trip != null && trip.isValid() ?
                new LocalTrip(trip.getPk(), trip.getId(), trip.getReferenceNumber(), trip.getTraveler(), trip.isMyTrip(), trip.getTravelerName(),
                        trip.getPurpose(), trip.status, trip.getSection(), trip.office, trip.getStartDate(), trip.endDate, trip.getSupervisor(), trip.getSupervisorName(),
                        trip.isInternationalTravel(), trip.isTaRequired(), trip.estimatedTravelCost, trip.getCurrency(), trip.getCompletedAt(), trip.getCanceledAt(),
                        trip.getRejectionNote(), trip.getCancellationNote(), trip.getCertificationNote(), trip.getReport(), trip.getAdditionalNote(), trip.getMiscExpenses(),
                        trip.getItineraries(), trip.getExpenses(), trip.getDeductions(), trip.getCostAssignments(), trip.getClearances(), trip.getActivities(),
                        trip.getAttachments(), trip.getCostSummary(), trip.getActionPoints())
                : null;
    }
}
