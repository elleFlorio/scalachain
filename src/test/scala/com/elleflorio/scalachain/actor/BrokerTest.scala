package com.elleflorio.scalachain.actor

import com.elleflorio.scalachain.actor.Broker.{AddTransaction, Clear, GetTransactions}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.elleflorio.scalachain.blockchain.Transaction
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._

class BrokerTest(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("broker-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Broker com.elleflorio.scalachain.actor" should "start with an empty list of transactions" in {
    val broker = system.actorOf(Broker.props)

    broker ! GetTransactions
    expectMsg(500 millis, List())
  }

  "A Broker com.elleflorio.scalachain.actor" should "return the correct list of added transactions" in {
    val broker = system.actorOf(Broker.props)
    val transaction1 = Transaction("A", "B", 100)
    val transaction2 = Transaction("C", "D", 1000)

    broker ! AddTransaction(transaction1)
    broker ! AddTransaction(transaction2)

    broker ! GetTransactions
    expectMsg(500 millis, List(transaction2, transaction1))
  }

  "A Broker com.elleflorio.scalachain.actor" should "clear the transaction lists when requested" in {
    val broker = system.actorOf(Broker.props)
    val transaction1 = Transaction("A", "B", 100)
    val transaction2 = Transaction("C", "D", 1000)

    broker ! AddTransaction(transaction1)
    broker ! AddTransaction(transaction2)

    broker ! Clear

    broker ! GetTransactions
    expectMsg(500 millis, List())
  }

}
