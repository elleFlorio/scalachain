package utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait NodeJsonProtocol extends SprayJsonSupport{
  import BlockchainJsonProtocol._

  implicit val transactionJsonFormat = TransactionJsonFormat
  implicit val blockJsonFormat = BlockJsonFormat
  implicit val chainLinkJsonFormat = ChainLinkJsonFormat
  implicit val chainJsonFormat = ChainJsonFormat

}
