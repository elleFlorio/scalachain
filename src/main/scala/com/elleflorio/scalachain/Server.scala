package com.elleflorio.scalachain

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.elleflorio.scalachain.actor.Node
import com.elleflorio.scalachain.api.NodeRoutes

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Server extends App with NodeRoutes {

  val address = if (args.length > 0) args(0) else "localhost"
  val port = if (args.length > 1) args(1).toInt else 8080

  implicit val system: ActorSystem = ActorSystem("scalachain")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val node: ActorRef = system.actorOf(Node.props("scalaChainNode0"))

  lazy val routes: Route = statusRoutes ~ transactionRoutes ~ mineRoutes

  Http().bindAndHandle(routes, address, port)

  println(s"Server online at http://$address:$port/")

  Await.result(system.whenTerminated, Duration.Inf)

}
