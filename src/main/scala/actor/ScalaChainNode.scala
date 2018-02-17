package actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import blockchain.Blockchain

object ScalaChainNode {
  def props: Props = Props(new ScalaChainNode(UUID.randomUUID()))
  final case class NewTransaction(sender: String, receiver: String, transaction: Long)
  final case class NewBlock(proof: Long)
  final case class UpdatedChain(blockchain: Blockchain)
  final case class MineBlock()
  final case class GetStatus()
}

class ScalaChainNode(uuid: UUID) extends Actor with ActorLogging {
  import ScalaChainNode._

  var blockchain = new Blockchain()

  override def receive: Receive = {
    case NewTransaction(sender, receiver, transaction) => {
      blockchain.addTransaction(sender, receiver, transaction)
      log.info(s"Added transaction: sender: $sender; receiver: $receiver; transaction:$transaction")
    }
    case NewBlock(proof) => {
      log.info(s"Received proof: $proof - checking solution...")
      if (blockchain.checkPoWSolution(blockchain.getLastHash(), proof)) {
        blockchain = blockchain.addBlock(proof)
        log.info("proof ok, added new block")
      } else {
        log.warning(s"proof $proof not valid!")
      }

    }
    case UpdatedChain(chain) => {
      blockchain = new Blockchain(chain.getChain())
      log.info(s"chain updated - last block hash: ${blockchain.getLastHash()}")
    }
    case MineBlock => {
      log.info("Mining new block...")
      val proof = blockchain.findProof()
      log.info(s"Found proof: $proof")
      blockchain.addTransaction("0", this.uuid.toString, 1)
      log.info(s"Added reward to node $uuid")
      blockchain = blockchain.addBlock(proof)
      log.info(s"new block mined - id: ${blockchain.getLastIndex()}")
    }
    case GetStatus => {
      log.info(s"Blockchain status:\n ${blockchain.getChain().toString}")
    }
  }
}
