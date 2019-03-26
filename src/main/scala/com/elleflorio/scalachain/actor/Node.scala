package com.elleflorio.scalachain.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.pattern.ask
import akka.util.Timeout
import com.elleflorio.scalachain.actor.Blockchain.{AddBlockCommand, GetChain, GetLastHash, GetLastIndex}
import com.elleflorio.scalachain.actor.Miner.{Ready, Validate}
import com.elleflorio.scalachain.blockchain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Node {

  sealed trait NodeMessage

  case class AddTransaction(transaction: Transaction) extends NodeMessage

  case class TransactionMessage(transaction: Transaction, nodeId: String) extends NodeMessage

  case class CheckPowSolution(solution: Long) extends NodeMessage

  case class AddBlock(proof: Long) extends NodeMessage

  case class AddBlockMessage(proof: Long, tansactions: List[Transaction], timestamp: Long) extends NodeMessage

  case object GetTransactions extends NodeMessage

  case object Mine extends NodeMessage

  case object StopMining extends NodeMessage

  case object GetStatus extends NodeMessage

  case object GetLastBlockIndex extends NodeMessage

  case object GetLastBlockHash extends NodeMessage

  def props(nodeId: String, mediator: ActorRef): Props = Props(new Node(nodeId, mediator))

  def createCoinbaseTransaction(nodeId: String) = Transaction("coinbase", nodeId, 100)
}

class Node(nodeId: String, mediator: ActorRef) extends Actor with ActorLogging {

  import Node._

  implicit lazy val timeout = Timeout(5.seconds)

  mediator ! Subscribe("newBlock", self)
  mediator ! Subscribe("transaction", self)

  val broker: ActorRef = context.actorOf(Broker.props)
  val miner: ActorRef = context.actorOf(Miner.props)
  val blockchain: ActorRef = context.actorOf(Blockchain.props(EmptyChain, nodeId))

  miner ! Ready

  override def receive: Receive = {
    case TransactionMessage(transaction, messageNodeId) => {
      log.info(s"Received transaction message from $messageNodeId")
      if (messageNodeId != nodeId) {
        broker ! Broker.AddTransaction(transaction)
      }
    }

    case AddTransaction(transaction) => {
      val node = sender()
      (blockchain ? GetLastIndex).mapTo[Int] onComplete {
        case Success(index) =>
          broker ! Broker.AddTransaction(transaction)
          mediator ! Publish("transaction", TransactionMessage(transaction, nodeId))
          node ! (index + 1)
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }
    }

    case CheckPowSolution(solution) =>
      val node = sender()
      (blockchain ? GetLastHash).mapTo[String] onComplete {
        case Success(hash: String) => miner.tell(Validate(hash, solution), node)
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }

    case AddBlockMessage(proof, transactions, timestamp) =>
      val node = sender()
      (self ? CheckPowSolution(proof)) onComplete {
        case Failure(e) => node ! akka.actor.Status.Failure(e)
        case Success(_) =>
          broker ! Broker.DiffTransaction(transactions)
          blockchain.tell(AddBlockCommand(transactions, proof, timestamp), node)
          miner ! Ready
      }

    case Mine =>
      val node = sender()
      (blockchain ? GetLastHash).mapTo[String] onComplete {
        case Success(hash) => (miner ? Miner.Mine(hash)).mapTo[Future[Long]] onComplete {
          case Success(solution) => waitForSolution(solution)
          case Failure(e) => log.error(s"Error finding PoW solution: ${e.getMessage}")
        }
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }

    case GetTransactions => broker forward Broker.GetTransactions
    case GetStatus => blockchain forward GetChain
    case GetLastBlockIndex => blockchain forward GetLastIndex
    case GetLastBlockHash => blockchain forward GetLastHash
  }

  def waitForSolution(solution: Future[Long]) = Future {
    solution onComplete {
      case Success(proof) =>
        val node = sender()
        val ts = System.currentTimeMillis()
        broker ! Broker.AddTransaction(createCoinbaseTransaction(nodeId))
        (broker ? Broker.GetTransactions).mapTo[List[Transaction]] onComplete {
          case Success(transactions) => mediator ! Publish("newBlock", AddBlockMessage(proof, transactions, ts))
          case Failure(e) => node ! akka.actor.Status.Failure(e)
        }
      case Failure(e) => log.error(s"Error finding PoW solution: ${e.getMessage}")
    }
  }

}
