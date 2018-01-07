package blockchain

import scorex.crypto.hash.Sha256
import spray.json.pimpAny

sealed trait Chain {
  def size: Int

  def blockHash: String

  def block: Block

  def ::(block: Block): Chain = ChainLink(this.size + 1, block, blockHash, this)

}

case class ChainLink(index: Int, block: Block, previousHash: String, tail: Chain, timestamp: Long = System.currentTimeMillis()) extends Chain {

  def size = 1 + tail.size

  def blockHash = hashBlock(this)

  def hashBlock(block: ChainLink): String = Sha256.hash(block.toJson.toString).toString
}


case object Empty extends Chain {
  def size = 0

  def blockHash = null

  def block = null
}


object Chain {

  def apply(blocks: ChainLink*): Chain = {
    if (blocks.isEmpty) {
      return Empty
    } else {
      val chainLink = blocks.head
      ChainLink(chainLink.index, chainLink.block, chainLink.previousHash, apply(blocks.tail: _*))
    }
  }
}