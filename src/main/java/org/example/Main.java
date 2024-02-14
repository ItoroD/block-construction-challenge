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

    Set<String> visited = new HashSet<>();

    private void readCSVFile(String csvFile){ //this reads in the csv and calls the processtransaction method

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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTransactions(Transaction tx){ // here we put all the transactions from csv into Hashmap, we also create a parent Map
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
    }



    private void processParentTransactions(){ // here we set the weigh and fee for each parent in the parentmap. We remove all transactions that are parents from the transaction map so that we dont double count
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
    }

    private double getTotalFee(String txId){
        Transaction tx = txMap.get(txId);
        List<String> nextParentList = tx.parentsTxIdList;
        double totalFee = tx.getFee();

        if(nextParentList != null){
            for(String parentTxId: nextParentList) {
                totalFee += getTotalFee(parentTxId);
            }
        }

        return totalFee;
    }

    public double getTotalWeight(String txId) {
        Transaction tx = txMap.get(txId);
        List<String> nextParentList = tx.parentsTxIdList;
        visited.add(txId);
        double totalWeight = tx.getWeight();

        if(nextParentList != null){
            for(String parentTxId: nextParentList) {
                totalWeight += getTotalWeight(parentTxId);
            }
        }


        return totalWeight;
    }

    private Map<String, Double> calculateFeeRate(){ //here we pick a tx, get its fee and weight by summing up that of its parent
        double feeRate;
        for (Map.Entry<String, Transaction> entry : txMap.entrySet()) {
            Transaction tx = entry.getValue();
            String txId = tx.getTxId();
            if(visited.contains(txId)){
                continue;
            }

            double fee = getTotalFee(txId);
            double weight = getTotalWeight( txId);
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
        String filePath = "C:\\Users\\itoro\\IdeaProjects\\block-construction\\src\\main\\resources\\mempool.csv";
        tx.readCSVFile(filePath);
        //tx.processParentTransactions();
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

        Set<String> alreadyPrinted = new HashSet();

        int totalWeight = 4_000_000;
        int weightSoFar = 0;
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            weightSoFar += tx.txMap.get(entry.getKey()).getWeight();
            if(weightSoFar > totalWeight) //check that out block has not exceeded maximum.
                break;
            List<String> parentsTxIds = tx.txMap.get(entry.getKey()).getParentsTxIdList();
            if(parentsTxIds != null){
                for(String parent : parentsTxIds){
                    if(alreadyPrinted.contains(parent)) //if parent already printed continue
                        continue;
                    System.out.println(parent);
                    alreadyPrinted.add(parent);
                }
            }
            if(!alreadyPrinted.contains(entry.getKey())){//if tx is not printed, print it and add it to already printed
                System.out.println(entry.getKey());
                alreadyPrinted.add(entry.getKey());
            }

        }
    }
}

//create transaction class with txid, fee, weight, listofparaents

//read tx from csv, see if they have parents, if yes put parent in a map <txid,parenttxidList> or <parenttxid, fee, weight>

//add the transaction to the <txid , Transaction> (we need to check in future when traversing this list if tx we are looking at is a parent. if it is we will want to skipp it and not calculate fee rate on it has it has already been calculated from child)

//create a feeRate class that has <txid, feerate, parentlist