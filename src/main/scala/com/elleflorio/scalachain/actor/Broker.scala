package com.elleflorio.scalachain.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import com.elleflorio.scalachain.blockchain.Transaction

object Broker {
  sealed trait BrokerMessage
  case class TransactionMessage(transaction: Transaction) extends BrokerMessage
  case class AddTransaction(transaction: Transaction) extends BrokerMessage
  case object GetTransactions extends BrokerMessage

  case object Clear extends BrokerMessage

  def props(mediator: ActorRef): Props = Props(new Broker(mediator))
}

class Broker(mediator: ActorRef) extends Actor with ActorLogging {
  import Broker._

  mediator ! Subscribe("transaction", self)

  var pending: List[Transaction] = List()

  override def receive: Receive = {
    case AddTransaction(transaction) => {
      mediator ! Publish("transaction", TransactionMessage(transaction))
      log.info(s"Published $transaction to transaction topic")
    }
    case TransactionMessage(transaction) => {
      pending = transaction :: pending
      log.info(s"Added $transaction to pending Transaction")
    }
    case GetTransactions => {
      log.info(s"Getting pending transactions")
      sender() ! pending
    }
    case Clear => {
      pending = List()
      log.info("Clear pending transaction List")
    }
    case SubscribeAck(Subscribe("transaction", None, `self`)) =>
      log.info("subscribing")
  }
}
