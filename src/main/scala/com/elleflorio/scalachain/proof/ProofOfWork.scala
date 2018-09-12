package com.elleflorio.scalachain.proof

import spray.json._
import DefaultJsonProtocol._
import com.elleflorio.scalachain.crypto.Crypto
import spray.json.pimpAny

import scala.annotation.tailrec

object ProofOfWork {

  def proofOfWork(lastHash: String): Long = {
    @tailrec
    def powHelper(lastHash: String, proof: Long): Long = {
      if (validProof(lastHash, proof))
        proof
      else
        powHelper(lastHash, proof + 1)
    }

    val proof = 0
    powHelper(lastHash, proof)
  }

  def validProof(lastHash: String, proof: Long): Boolean = {
    val guess = (lastHash ++ proof.toString).toJson.toString()
    val guessHash = Crypto.sha256Hash(guess)
    (guessHash take 4) == "0000"
  }

}
