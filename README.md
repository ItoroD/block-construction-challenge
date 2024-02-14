# Here is a brief explanation of what my block construction algorithm deos to create the block

1. I read the csv **readCSVFile()** file into memeory and store them in txMap using the processTransactions(). This **txMap** will hold the transaction id as key and the transaction object itself <txId, Transaction>
2. Transaction class has been created to conform to the given transactions in the csv.
3. the **processTransactions()** method also creates a map of all parents transactions. (**parentsMap**) I created this parent map to be able to have access to parents really quickly but i realized it was not needed as i built on the solution.
4. since I have all transactions in a map, I iterate through all transactions to calculate the feeRate for each transaction. To calculate the feeRate, we want to take the sum of the all parent transaction fee
   and all parents weight (including ancestors) plus the fee and weight of the transaction we are currently on. Finally we divide the totalfee by totalweight

   _feeRate = totalFees/totalweight_

   This feeRate is stored in a map called **feeRateMap** which holds the transaction id as key and feerate as its value <txId, feeRate>
5. Getting the sum of parent fee and weight is where I realize that we do not need the parent map. The parent map will actually not work because parents can also have parents which could also have parents (ancestors). This can lead to a long chain of ancestors. Hence, we can get parents and the ancestors by recursively checking the same txMap for
   the parents of a particular transaction **getTotalWeight() getTotalFee()**. The key here is, once a parent is found we must add that parent to a visited set which tells us not to process that transaction again.
6. In the end we sort the **feeRateMap** by the highest feerate. Note that only child transactions are in the fee rate map because we have included their parents in the calculation of the fee calculation.
7. To print the block txids, we iterate through the fee rate map. But before we print an id, we check if it has parents, if it does, we print parents txid first then the txid. We also want to ensure that the weight of transactions we are printing does not exceed max block height
