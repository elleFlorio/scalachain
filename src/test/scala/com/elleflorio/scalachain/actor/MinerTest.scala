package com.elleflorio.scalachain.actor

import com.elleflorio.scalachain.actor.Miner.{Mine, Ready}
import akka.actor.ActorSystem
import akka.actor.Status.{Failure, Success}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

class MinerTest(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("broker-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Miner com.elleflorio.scalachain.actor" should "be ready when requested" in {
    val miner = system.actorOf(Miner.props)

    miner ! Ready
    miner ! Ready

    expectMsg(500 millis, Success("OK"))
  }

  "A Miner com.elleflorio.scalachain.actor" should "be busy while mining a new block" in {
    val miner = system.actorOf(Miner.props)

    miner ! Ready
    miner ! Mine("1")

    expectMsgClass(500 millis, classOf[Future[Long]])

    miner ! Mine("2")

    expectMsgType[Failure]
  }

}
