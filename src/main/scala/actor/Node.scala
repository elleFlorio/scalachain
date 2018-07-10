package actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import blockchain._

object Node {
  def props: Props = Props(new Node(UUID.randomUUID()))
  final case class GetTransactions()
  final case class NewTransaction(sender: String, receiver: String, transaction: Long)
  final case class NewBlock(proof: Long)
  final case class UpdatedChain(blockchain: Chain)
  final case class MineBlock()
  final case class GetStatus()
}

class Node(uuid: UUID) extends Actor with ActorLogging {
  import Node._

  var blockchain = new Blockchain()

  override def receive: Receive = {
    case GetTransactions => {
      log.info(s"Retrieving transactions of next block with id: ${blockchain.getLastIndex() + 1}")
      sender() ! blockchain.transactions
    }
    case NewTransaction(transactionSender, transactionReceiver, transaction) => {
      val index = blockchain.addValue(Transaction(transactionSender, transactionReceiver, transaction))
      log.info(s"Added transaction: sender: $transactionSender; receiver: $transactionReceiver; transaction:$transaction")
      sender() ! index
    }
    case NewBlock(proof) => {
      log.info(s"Received proof: $proof - checking solution...")
      if (blockchain.checkPoWSolution(blockchain.getLastHash(), proof)) {
        blockchain = blockchain.addBlock(proof)
        log.info("proof ok, added new block")
      } else {
        log.warning(s"proof $proof not valid!")
      }
      sender() ! blockchain.getLastHash()
    }
    case UpdatedChain(chain) => {
      blockchain = new Blockchain(chain)
      log.info(s"chain updated - last block hash: ${blockchain.getLastHash()}")
      sender() ! blockchain.getLastHash()
    }
    case MineBlock => {
      log.info("Mining new block...")
      val proof = blockchain.findProof()
      log.info(s"Found proof: $proof")
      blockchain.addValue(Transaction("0", this.uuid.toString, 1))
      log.info(s"Added reward to node $uuid")
      blockchain = blockchain.addBlock(proof)
      log.info(s"new block mined - id: ${blockchain.getLastIndex()}")
      sender() ! blockchain.getLastIndex()
    }
    case GetStatus => {
      log.info(s"Blockchain status:\n ${blockchain.getChain().toString}")
      sender() ! blockchain.getChain()
    }
  }
}
