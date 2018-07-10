package api

import actor.Node._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import blockchain.{Chain, Transaction}
import utils.JsonSupportNode
import spray.json.DefaultJsonProtocol._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.duration._

trait NodeRoutes extends JsonSupportNode {

  implicit def system: ActorSystem

  def node: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  lazy val statusRoutes: Route = pathPrefix("status") {
    concat(
      pathEnd {
        concat(
          get {
            val statusFuture: Future[Chain] = (node ? GetStatus).mapTo[Chain]
            onSuccess(statusFuture){ status =>
              complete(StatusCodes.OK, status)
            }
          }
        )
      }
    )
  }

  lazy val transactionRoutes: Route = pathPrefix("transactions") {
    concat(
      pathEnd {
        concat(
          get {
            val transactionsRetrieved: Future[ArrayBuffer[Transaction]] =
              (node ? GetTransactions).mapTo[ArrayBuffer[Transaction]]
            onSuccess(transactionsRetrieved) { transactions =>
              complete(transactions.toList)
            }
          },
          post {
            entity(as[Transaction]) { transaction =>
              val transactionCreated: Future[Int] =
                (node ? NewTransaction(transaction.sender, transaction.recipient, transaction.value)).mapTo[Int]
              onSuccess(transactionCreated) { done =>
                complete((StatusCodes.Created, done.toString))
              }
            }
          }
        )
      }
    )
  }

  lazy val blockRoutes: Route = pathPrefix("blocks") {
    concat(
      path(Segment) { proof =>
        concat(
          get {
            val blockCreated: Future[String] =
              (node ? NewBlock(proof.toLong)).mapTo[String]
            onSuccess(blockCreated) { hash =>
              complete((StatusCodes.Created, hash))
            }
          }
        )
      }
    )
  }

  lazy val chainRoutes: Route = pathPrefix("chain") {
    concat(
      pathEnd {
        concat(
          post {
            entity(as[Chain]) { chain =>
              val chainUpdated: Future[String] =
                (node ? UpdatedChain(chain)).mapTo[String]
              onSuccess(chainUpdated) { hash =>
                complete(StatusCodes.Created, hash)
              }
            }
          }
        )
      }
    )
  }

  lazy val mineRoutes: Route = pathPrefix("mine") {
    concat(
      pathEnd {
        concat(
          get {
            node ? MineBlock
            complete(StatusCodes.OK)
          }
        )
      }
    )
  }

}
