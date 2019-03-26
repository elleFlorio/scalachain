package com.elleflorio.scalachain.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import com.elleflorio.scalachain.blockchain.Transaction

object Broker {
  sealed trait BrokerMessage
  case class TransactionMessage(transaction: Transaction) extends BrokerMessage
  case class AddTransaction(transaction: Transaction) extends BrokerMessage
  case class DiffTransaction(transactions: List[Transaction]) extends BrokerMessage
  case object GetTransactions extends BrokerMessage

  case object Clear extends BrokerMessage

  val props: Props = Props(new Broker)
}

class Broker extends Actor with ActorLogging {
  import Broker._

  var pending: List[Transaction] = List()

  override def receive: Receive = {
    case AddTransaction(transaction) => {
      pending = transaction :: pending
      log.info(s"Added $transaction to pending Transaction")
    }
    case GetTransactions => {
      log.info(s"Getting pending transactions")
      sender() ! pending
    }
    case DiffTransaction(externalTransactions) => {
      pending = pending diff externalTransactions
    }
    case Clear => {
      pending = List()
      log.info("Clear pending transaction List")
    }
    case SubscribeAck(Subscribe("transaction", None, `self`)) =>
      log.info("subscribing")
  }
}
