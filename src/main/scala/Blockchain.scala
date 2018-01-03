import scala.collection.mutable.ArrayBuffer

class Blockchain(chain: Chain = Empty) {

  val transactions : ArrayBuffer[Transaction] = ArrayBuffer.empty

  def addTransaction(sender: String, recipient: String, amount: Long) : Int = {
    transactions += new Transaction(sender, recipient, amount)
    chain.size + 1
  }

  def addBlock(proof: Long) : Blockchain = {
    val transactions = this.transactions.toList
    val block = new Block(transactions, proof)

    new Blockchain(block :: chain)
  }

  def getLastBlock(): ChainLink = ???

}