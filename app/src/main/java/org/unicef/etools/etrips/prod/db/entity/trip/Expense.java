package org.unicef.etools.etrips.prod.db.entity.trip;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Expense extends RealmObject {

    @Expose
    @PrimaryKey
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("type")
    private long type;

    @Expose
    @SerializedName("document_currency")
    private long document_currency;

    @Expose
    @SerializedName("account_currency")
    private long accountCurrency;

    @Expose
    @SerializedName("amount")
    private double amount;

    public Expense() {
    }

    public Expense(long id, long type, long document_currency, long accountCurrency, double amount) {
        this.id = id;
        this.type = type;
        this.document_currency = document_currency;
        this.accountCurrency = accountCurrency;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getDocument_currency() {
        return document_currency;
    }

    public void setDocument_currency(long document_currency) {
        this.document_currency = document_currency;
    }

    public long getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(long accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
