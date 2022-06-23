package com.codelabs.selfit_admin.models;

public class PaymentHistory {
    String adminsID, trainersID, transAmount, transDate, transRef;

    public PaymentHistory(String adminsID, String trainersID, String transAmount, String transDate, String transRef) {
        this.adminsID = adminsID;
        this.trainersID = trainersID;
        this.transAmount = transAmount;
        this.transDate = transDate;
        this.transRef = transRef;
    }

    public String getAdminsID() {
        return adminsID;
    }

    public void setAdminsID(String adminsID) {
        this.adminsID = adminsID;
    }

    public String getTrainersID() {
        return trainersID;
    }

    public void setTrainersID(String trainersID) {
        this.trainersID = trainersID;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(String transAmount) {
        this.transAmount = transAmount;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransRef() {
        return transRef;
    }

    public void setTransRef(String transRef) {
        this.transRef = transRef;
    }
}
