package org.example;

import java.util.List;

public class Transaction {
    String txId;
    double fee;
    double weight;
    List<String> parentsTxIdList;

    public Transaction(String txId, double fee, double weight, List<String> parentsTxIdList) {
        this.txId = txId;
        this.fee = fee;
        this.weight = weight;
        this.parentsTxIdList = parentsTxIdList;
    }

    public Transaction() {

    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getParentsTxIdList() {
        return parentsTxIdList;
    }

    public void setParentsTxIdList(List<String> parentsTxIdList) {
        this.parentsTxIdList = parentsTxIdList;
    }
}
