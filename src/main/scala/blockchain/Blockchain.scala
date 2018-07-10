package blockchain

import proof.ProofOfWork

import scala.collection.mutable.ArrayBuffer

class Blockchain(chain: Chain = EmptyChain) {

  val transactions : ArrayBuffer[Transaction] = ArrayBuffer.empty

  def addValue(content: Transaction) : Int = {
    transactions += content
    chain.index + 1
  }

  def checkPoWSolution(lastHash: String, proof: Long) : Boolean = {
    ProofOfWork.validProof(lastHash, proof)
  }

  def findProof() : Long = {
    ProofOfWork.proofOfWork(this.getLastHash())
  }

  def addBlock(proof: Long) : Blockchain = {
    val transactions = this.transactions.toList
    this.transactions.clear()

    new Blockchain(ChainLink(chain.index + 1, proof, transactions) :: chain)
  }

  def getChain(): Chain = chain

  def getLastHash(): String = chain.hash

  def getLastIndex(): Int = chain.index

}
