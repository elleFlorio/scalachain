package com.elleflorio.scalachain.actor

import com.elleflorio.scalachain.actor.Blockchain.{AddBlockCommand, GetChain, GetLastHash, GetLastIndex}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.elleflorio.scalachain.blockchain.{ChainLink, EmptyChain, Transaction}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._

class BlockchainTest(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("blockchain-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Blockchain Actor" should "correctly add a new block" in {
    val blockchain = system.actorOf(Blockchain.props(EmptyChain, "test"))

    blockchain ! GetChain
    expectMsg(1000 millis, EmptyChain)

    blockchain ! GetLastIndex
    expectMsg(1000 millis, 0)

    blockchain ! GetLastHash
    expectMsg(1000 millis, "1")


    val transactions = List(Transaction("a", "b", 1L))
    val proof = 1L
    blockchain ! AddBlockCommand(transactions, proof)
    expectMsg(1000 millis, 1)

    blockchain ! GetLastIndex
    expectMsg(1000 millis, 1)

    blockchain ! GetChain
    expectMsgType[ChainLink]

  }

  "A Blockchain Actor" should "correctly recover from a snapshot" in {
    val blockchain = system.actorOf(Blockchain.props(EmptyChain, "test"))

    blockchain ! GetLastIndex
    expectMsg(1000 millis, 1)

    blockchain ! GetChain
    val ack = expectMsgType[ChainLink]

    ack.values.head.sender shouldBe "a"
  }
}
