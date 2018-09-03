package com.elleflorio.scalachain.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.elleflorio.scalachain.blockchain.Transaction

object Broker {
  sealed trait BrokerMessage
  case class AddTransaction(transaction: Transaction) extends BrokerMessage
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
    case Clear => {
      pending = List()
      log.info("Clear pending transaction List")
    }
  }
}
