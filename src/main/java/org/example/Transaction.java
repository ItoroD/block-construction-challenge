package org.example;

import java.util.List;

public class Transaction {
    String txId;
    long fee;
    long weight;
    List<String> parentsTxIdList;

    public Transaction(String txId, long fee, long weight, List<String> parentsTxIdList) {
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

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public List<String> getParentsTxIdList() {
        return parentsTxIdList;
    }

    public void setParentsTxIdList(List<String> parentsTxIdList) {
        this.parentsTxIdList = parentsTxIdList;
    }
}
