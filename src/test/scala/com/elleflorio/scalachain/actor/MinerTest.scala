package com.elleflorio.scalachain.actor

import com.elleflorio.scalachain.actor.Miner.{Mine, Ready}
import akka.actor.ActorSystem
import akka.actor.Status.{Failure, Success}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

class MinerTest(sys: ActorSystem) extends TestKit(sys)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("miner-test"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Miner Actor" should "be ready when requested" in {
    val miner = system.actorOf(Miner.props)

    miner ! Ready
    miner ! Ready

    expectMsg(500 millis, Success("OK"))
  }

  "A Miner Actor" should "be busy while mining a new block" in {
    val miner = system.actorOf(Miner.props)

    miner ! Ready
    miner ! Mine("1")

    expectMsgClass(500 millis, classOf[Future[Long]])

    miner ! Mine("2")

    expectMsgType[Failure]
  }

}
