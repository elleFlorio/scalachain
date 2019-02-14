package com.elleflorio.scalachain

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.elleflorio.scalachain.actor.Node
import com.elleflorio.scalachain.api.NodeRoutes
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Server extends App with NodeRoutes {

  implicit val system: ActorSystem = ActorSystem("scalachain")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config: Config = ConfigFactory.load()
  val address = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("scalachain.node.id")

  lazy val routes: Route = statusRoutes ~ transactionRoutes ~ mineRoutes

  val node: ActorRef = system.actorOf(Node.props(nodeId))

  Http().bindAndHandle(routes, address, port)
  println(s"Server online at http://$address:$port/")

  Await.result(system.whenTerminated, Duration.Inf)

}
