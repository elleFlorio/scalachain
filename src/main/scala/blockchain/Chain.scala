package blockchain

import crypto.Crypto
import spray.json._
import utils.BlockchainJsonProtocol._

sealed trait Chain {
  val size: Int

  val blockHash: String

  val block: Block

  val proof: Long

  def ::(block: Block): Chain = ChainLink(this.size + 1, block, blockHash, this)

}

case class ChainLink(index: Int, block: Block, previousHash: String, tail: Chain, timestamp: Long = System.currentTimeMillis()) extends Chain {
  val size = 1 + tail.size

  val blockHash = Crypto.sha256Hash(this.toJson.toString)

  val proof = block.proof
}


case object EmptyChain extends Chain {
  val size = 0

  val blockHash = null

  val block = null

  val proof = -1
}


object Chain {

  def apply(blocks: ChainLink*): Chain = {
    if (blocks.isEmpty) {
      return EmptyChain
    } else {
      val chainLink = blocks.head
      ChainLink(chainLink.index, chainLink.block, chainLink.previousHash, apply(blocks.tail: _*))
    }
  }
}