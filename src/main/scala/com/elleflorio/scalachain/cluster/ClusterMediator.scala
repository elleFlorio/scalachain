package com.elleflorio.scalachain.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import com.elleflorio.scalachain.actor.Broker.AddTransaction
import com.elleflorio.scalachain.blockchain.Transaction
import com.elleflorio.scalachain.cluster.ClusterMediator.TransactionMessage

object ClusterMediator {

  sealed trait ClusterMessage
  case class TransactionMessage(transaction: Transaction) extends ClusterMessage

  def props(broker: ActorRef) = Props(new ClusterMediator(broker))

}

class ClusterMediator(broker: ActorRef) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("transaction", self)

  def receive = {
    case SubscribeAck(Subscribe("transaction", None, `self`)) =>
      log.info("subscribing")
    case TransactionMessage(transaction: Transaction) => {
      log.info(s"Publishing $transaction")
      mediator ! Publish("transaction", transaction)
    }
    case transaction: Transaction => {
      broker ! AddTransaction(transaction)
      log.info(s"Added transaction $transaction")
    }
  }

}
