package org.unicef.etools.etrips.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class CostSummary extends RealmObject {

    @Expose
    @SerializedName("dsa_total")
    private String dsaTotal;

    @Expose
    @SerializedName("expenses_total")
    private String expensesTotal;

    @Expose
    @SerializedName("deductions_total")
    private String deductionsTotal;

    @Expose
    @SerializedName("preserved_expenses")
    private String preservedExpenses;

    @Expose
    @SerializedName("expenses_delta")
    private String expensesDelta;

    @Expose
    @SerializedName("dsa")
    private RealmList<Dsa> dsa;

    public CostSummary() {
    }

    public CostSummary(String dsa_total, String expensesTotal, String deductionsTotal,
                       String preservedExpenses, String expensesDelta, RealmList<Dsa> dsa) {
        this.dsaTotal = dsa_total;
        this.expensesTotal = expensesTotal;
        this.deductionsTotal = deductionsTotal;
        this.preservedExpenses = preservedExpenses;
        this.expensesDelta = expensesDelta;
        this.dsa = dsa;
    }

    public String getDsaTotal() {
        return dsaTotal;
    }

    public void setDsaTotal(String dsa_total) {
        this.dsaTotal = dsa_total;
    }

    public String getExpensesTotal() {
        return expensesTotal;
    }

    public void setExpensesTotal(String expensesTotal) {
        this.expensesTotal = expensesTotal;
    }

    public String getDeductionsTotal() {
        return deductionsTotal;
    }

    public void setDeductionsTotal(String deductionsTotal) {
        this.deductionsTotal = deductionsTotal;
    }

    public String getPreservedExpenses() {
        return preservedExpenses;
    }

    public void setPreservedExpenses(String preservedExpenses) {
        this.preservedExpenses = preservedExpenses;
    }

    public String getExpensesDelta() {
        return expensesDelta;
    }

    public void setExpensesDelta(String expensesDelta) {
        this.expensesDelta = expensesDelta;
    }

    public RealmList<Dsa> getDsa() {
        return dsa;
    }

    public void setDsa(RealmList<Dsa> dsa) {
        this.dsa = dsa;
    }
}
