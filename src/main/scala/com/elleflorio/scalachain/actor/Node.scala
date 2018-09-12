package com.elleflorio.scalachain.actor

import com.elleflorio.scalachain.actor.Blockchain.{AddBlockCommand, GetChain, GetLastHash, GetLastIndex}
import com.elleflorio.scalachain.actor.Broker.Clear
import com.elleflorio.scalachain.actor.Miner.{Ready, Validate}
import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.elleflorio.scalachain.blockchain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Node {

  sealed trait NodeMessage

  case class AddTransaction(transaction: Transaction) extends NodeMessage

  case class CheckPowSolution(solution: Long) extends NodeMessage

  case class AddBlock(proof: Long) extends NodeMessage

  case object GetTransactions extends NodeMessage

  case object Mine extends NodeMessage

  case object StopMining extends NodeMessage

  case object GetStatus extends NodeMessage

  case object GetLastBlockIndex extends NodeMessage

  case object GetLastBlockHash extends NodeMessage

  def props(nodeId: String): Props = Props(new Node(nodeId))

  def createCoinbaseTransaction(nodeId: String) = Transaction("coinbase", nodeId, 100)
}

class Node(nodeId: String) extends Actor with ActorLogging {

  import Node._

  implicit lazy val timeout = Timeout(5.seconds)

  val broker = context.actorOf(Broker.props)
  val miner = context.actorOf(Miner.props)
  val blockchain = context.actorOf(Blockchain.props(EmptyChain, nodeId))

  miner ! Ready

  override def receive: Receive = {
    case AddTransaction(transaction) => {
      val node = sender()
      broker ! Broker.AddTransaction(transaction)
      (blockchain ? GetLastIndex).mapTo[Int] onComplete {
        case Success(index) => node ! (index + 1)
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }
    }
    case CheckPowSolution(solution) => {
      val node = sender()
      (blockchain ? GetLastHash).mapTo[String] onComplete {
        case Success(hash: String) => miner.tell(Validate(hash, solution), node)
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }
    }
    case AddBlock(proof) => {
      val node = sender()
      (self ? CheckPowSolution(proof)) onComplete {
        case Success(_) => {
          (broker ? Broker.GetTransactions).mapTo[List[Transaction]] onComplete {
            case Success(transactions) => blockchain.tell(AddBlockCommand(transactions, proof), node)
            case Failure(e) => node ! akka.actor.Status.Failure(e)
          }
          broker ! Clear
        }
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }
    }
    case Mine => {
      val node = sender()
      (blockchain ? GetLastHash).mapTo[String] onComplete {
        case Success(hash) => (miner ? Miner.Mine(hash)).mapTo[Future[Long]] onComplete {
          case Success(solution) => waitForSolution(solution)
          case Failure(e) => log.error(s"Error finding PoW solution: ${e.getMessage}")
        }
        case Failure(e) => node ! akka.actor.Status.Failure(e)
      }
    }
    case GetTransactions => broker forward Broker.GetTransactions
    case GetStatus => blockchain forward GetChain
    case GetLastBlockIndex => blockchain forward GetLastIndex
    case GetLastBlockHash => blockchain forward GetLastHash
  }

  def waitForSolution(solution: Future[Long]) = Future {
    solution onComplete {
      case Success(proof) => {
        broker ! Broker.AddTransaction(createCoinbaseTransaction(nodeId))
        self ! AddBlock(proof)
        miner ! Ready
      }
      case Failure(e) => log.error(s"Error finding PoW solution: ${e.getMessage}")
    }
  }

}
