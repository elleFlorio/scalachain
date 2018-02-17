package utils

import blockchain._
import spray.json._

object BlockchainJsonProtocol extends DefaultJsonProtocol {

  implicit object TransactionJsonFormat extends RootJsonFormat[Transaction] {
    def write(t: Transaction) = JsObject(
      "sender" -> JsString(t.sender),
      "recipient" -> JsString(t.recipient),
      "amount" -> JsNumber(t.amount),
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("sender", "recipient", "amount") match {
        case Seq(JsString(sender), JsString(recipient), JsNumber(amount)) =>
          new Transaction(sender, recipient, amount.toLong)
        case _ => throw new DeserializationException("Transaction expected")
      }
    }
  }

  implicit object BlockJsonFormat extends RootJsonFormat[Block] {
    def write(b: Block) = JsObject(
      "transactions" -> JsArray(b.transactions.map(_.toJson).toVector),
      "proof" -> JsNumber(b.proof)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("transactions", "proof") match {
        case Seq(JsArray(transactions), JsNumber(proof)) =>
          new Block(transactions.map(_.convertTo(TransactionJsonFormat)).toList, proof.toLong)
        case _ => throw new DeserializationException("Transaction expected")
      }
    }
  }

  implicit object ChainLinkJsonFormat extends RootJsonFormat[ChainLink] {
    override def write(chainLink: ChainLink) = JsObject(
    "index" -> JsNumber(chainLink.index),
    "block" -> chainLink.block.toJson,
    "previousHash" -> JsString(chainLink.previousHash),
    "tail" -> JsString("TODO"),
    "timeStamp" -> JsNumber(chainLink.timestamp)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("index", "block", "previousHash", "tail", "timestamp") match {
        case Seq(JsNumber(index), block, JsString(previousHash), tail, JsNumber(timestamp)) =>
          new ChainLink(index.toInt, block.convertTo(BlockJsonFormat), previousHash, tail.convertTo(ChainJsonFormat), timestamp.toLong)
        case _ => throw new DeserializationException("ChainLink expected")
      }
    }
  }

  implicit object ChainJsonFormat extends RootJsonFormat[Chain] {
    override def write(chain: Chain) = chain match {
      case chainLink: ChainLink => chainLink.toJson
      case _ => JsNull
    }

    override def read(value: JsValue) = ChainLinkJsonFormat.read(value)
  }
}
