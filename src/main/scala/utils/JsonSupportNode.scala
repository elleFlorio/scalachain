package utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport


trait JsonSupportNode extends SprayJsonSupport{

  implicit val chainJsonFormat = JsonSupport.ChainJsonFormat
  implicit val chainLinkJsonFormat = JsonSupport.ChainLinkJsonFormat
  implicit val transactionJsonFormat = JsonSupport.TransactionJsonFormat
}
