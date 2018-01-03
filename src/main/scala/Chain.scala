import scala.annotation.tailrec

sealed trait Chain {
  def size: Int

  def hash: String

  def ::(block: Block): Chain = ChainLink(this.size + 1, block, hash, this)
}

case class ChainLink(index: Int, block: Block, previousHash: String, tail: Chain) extends Chain {
  val timestamp = System.currentTimeMillis()

  def size = 1 + tail.size

  def hash = hashBlock(this)

  def hashBlock(block: ChainLink): String = ???
}

case object Empty extends Chain {
  def size = 0

  def hash = null
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

