package com.elleflorio.scalachain.actor

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.pubsub.DistributedPubSub
import akka.testkit.{ImplicitSender, TestKit}
import com.elleflorio.scalachain.actor.Broker.{AddTransaction, Clear, GetTransactions, TransactionMessage}
import com.elleflorio.scalachain.blockchain.Transaction
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._

class BrokerTest(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("broker-test"))
  val mediator: ActorRef = DistributedPubSub(this.system).mediator

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Broker Actor" should "start with an empty list of transactions" in {
    val broker = system.actorOf(Broker.props(mediator))

    broker ! GetTransactions
    expectMsg(500 millis, List())
  }

  "A Broker Actor" should "return the correct list of added transactions" in {
    val broker = system.actorOf(Broker.props(mediator))
    val transaction1 = Transaction("A", "B", 100)
    val transaction2 = Transaction("C", "D", 1000)

    broker ! TransactionMessage(transaction1)
    broker ! TransactionMessage(transaction2)

    broker ! GetTransactions
    expectMsg(500 millis, List(transaction2, transaction1))
  }

  "A Broker Actor" should "clear the transaction lists when requested" in {
    val broker = system.actorOf(Broker.props(mediator))
    val transaction1 = Transaction("A", "B", 100)
    val transaction2 = Transaction("C", "D", 1000)

    broker ! TransactionMessage(transaction1)
    broker ! TransactionMessage(transaction2)

    broker ! Clear

    broker ! GetTransactions
    expectMsg(500 millis, List())
  }

}
