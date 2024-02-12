package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    Map<String, Double[]> parentsMap = new HashMap<>();
    Map<String, Transaction> txMap = new HashMap<>();

    Map<String, Double> feeRateMap = new HashMap<>();

    private void readCSVFile(String csvFile){

        String line = "";
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] values = line.split(csvSplitBy);
                String txId = values[0]; double fee = Double.parseDouble(values[1]); double weight = Double.parseDouble(values[2]);

                List<String> parentsTxId = values.length > 3 && values[3] != "" ? Arrays.asList(values[3].split(";")) : null;
                Transaction tx = new Transaction(txId,fee,weight,parentsTxId);
                processTransactions(tx);
                //System.out.println("Column 1: " + values[0] + ", Column 2: " + values[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTransactions(Transaction tx){
//        long fee = tx.getFee();
//        long weight = tx.getWeight();
        List<String> parentsTxIds = tx.getParentsTxIdList();
        String txId = tx.txId;
        txMap.put(txId, tx);

        if(parentsTxIds != null){
            //System.out.println("in here");
            for(String parentId: parentsTxIds){
                parentsMap.put(parentId, null);
            }
        }
        //System.out.println(parentsMap);

    }

    private void processParentTransactions(){
        //System.out.println(parentsMap);
        for (Map.Entry<String, Transaction> entry : txMap.entrySet()) {
            //System.out.println(entry.getKey());
            if(parentsMap.containsKey(entry.getKey())){ //the transaction we are looking at is a parent tx
                //System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                Transaction tx = entry.getValue();
                double fee = tx.getFee();
                double weight = tx.getWeight();
                Double[] feeAndWeight = {fee,weight};
                parentsMap.put(entry.getKey(), feeAndWeight);
            }
        }

        //now remove parent transactions from the txMap
        for (Map.Entry<String, Double[]> entry : parentsMap.entrySet()) {
            //System.out.println(entry.getKey());
            if(txMap.containsKey(entry.getKey())){ //the transaction we are looking at is a parent tx
                txMap.remove(entry.getKey());
            }
        }

        //now if we see a transaction that is a parent who has a parent, lets update the parent map with its parent and also add the cumulative fee and weight

    }

    private Map<String, Double> calculateFeeRate(){
        double feeRate;
        for (Map.Entry<String, Transaction> entry : txMap.entrySet()) {
            Transaction tx = entry.getValue();
            double fee = entry.getValue().getFee();
            double weight = entry.getValue().getWeight();
            String txId = entry.getValue().getTxId();

            if(entry.getValue().getParentsTxIdList() != null){
                for(String parent : entry.getValue().getParentsTxIdList()){
                    Double[] feeAndWeight = parentsMap.get(parent);
                    fee += feeAndWeight[0];
                    weight += feeAndWeight[1];
                }
            }
            feeRate = fee/weight;
            feeRateMap.put(txId, feeRate);
            tx.setFee(fee);
            tx.setWeight(weight);
            txMap.put(txId, tx); //update with new weights and fee sum from all parents
        }
        return feeRateMap;
    }

    public static void main(String[] args) {
        Main tx = new Main();
        String filePath = "C:\\Users\\itoro\\IdeaProjects\\block-construction\\src\\main\\resources\\test.csv";
        tx.readCSVFile(filePath);
        tx.processParentTransactions();
        Map<String, Double> feeRateMap = tx.calculateFeeRate();

        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<>(feeRateMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            List<String> parentsTxIds = tx.txMap.get(entry.getKey()).getParentsTxIdList();
            if(parentsTxIds != null){
                for(String parent : parentsTxIds){
                    System.out.println(parent);
                }
            }
            System.out.println(entry.getKey());
            //sortedMap.put(entry.getKey(), entry.getValue());
        }
    }
}

//create transaction class with txid, fee, weight, listofparaents

//read tx from csv, see if they have parents, if yes put parent in a map <txid,parenttxidList> or <parenttxid, fee, weight>

//add the transaction to the <txid , Transaction> (we need to check in future when traversing this list if tx we are looking at is a parent. if it is we will want to skipp it and not calculate fee rate on it has it has already been calculated from child)

//create a feeRate class that has <txid, feerate, parentlist