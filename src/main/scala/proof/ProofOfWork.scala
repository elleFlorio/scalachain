package proof

import spray.json._
import DefaultJsonProtocol._
import scorex.crypto.hash.Sha256
import spray.json.pimpAny

import scala.annotation.tailrec

object ProofOfWork {

  def proofOfWork(lastProof: Long): Long = {
    @tailrec
    def powHelper(lastProof: Long, proof: Long): Long = {
      if (validProof(lastProof, proof))
        proof
      else
        powHelper(lastProof, proof + 1)
    }

    val proof = 0
    powHelper(lastProof, proof)
  }

  def validProof(lastProof: Long, proof: Long): Boolean = {
    val guess = (lastProof.toString ++ proof.toString).toJson.toString
    val guessHash = Sha256.hash(guess).toString
    (guessHash takeRight 4) == "0000"
  }

}
