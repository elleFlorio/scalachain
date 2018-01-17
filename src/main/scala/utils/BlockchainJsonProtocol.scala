package utils

import blockchain._
import spray.json._

object BlockchainJsonProtocol extends DefaultJsonProtocol {

  //FIXME Cannot automatically determine case class field names and order for 'blockchain.ChainLink'
  // please use the 'jsonFormat' overload with explicit field name specification
  implicit val chainLinkJsonFormat = jsonFormat5(ChainLink)

  implicit object ChainJsonFormat extends RootJsonFormat[Chain] {

    override def write(chain: Chain) = chain match {
      case _: ChainLink => chain.toJson
      case _ => serializationError("Cannot serialize empty chain")
    }

    override def read(value: JsValue) = chainLinkJsonFormat.read(value)
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

}
