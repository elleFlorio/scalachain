package utils

import blockchain.ChainLink
import spray.json.DefaultJsonProtocol

object JsonUtils {

  object ChainLinkJsonProtocol extends DefaultJsonProtocol {
    implicit val chainLinkFormat = jsonFormat5(ChainLink)
  }

}
