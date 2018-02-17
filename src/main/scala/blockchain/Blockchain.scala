package blockchain

import proof.ProofOfWork

import scala.collection.mutable.ArrayBuffer

class Blockchain(chain: Chain = EmptyChain) {

  val transactions : ArrayBuffer[Transaction] = ArrayBuffer.empty

  def addTransaction(sender: String, recipient: String, amount: Long) : Int = {
    transactions += new Transaction(sender, recipient, amount)
    chain.size + 1
  }

  def checkPoWSolution(lastHash: String, proof: Long) : Boolean = {
    ProofOfWork.validProof(lastHash, proof)
  }

  def addBlock(proof: Long) : Blockchain = {
    val transactions = this.transactions.toList
    val block = new Block(transactions, proof)
    this.transactions.clear()

    new Blockchain(block :: chain)
  }

  def findProof() : Long = {
    ProofOfWork.proofOfWork(this.getLastHash())
  }

  def getLastBlock(): Block = chain.block

  def getLastHash(): String = chain.blockHash

  def getLastIndex(): Int = this.chain.size

  def getChain(): Chain = this.chain

}