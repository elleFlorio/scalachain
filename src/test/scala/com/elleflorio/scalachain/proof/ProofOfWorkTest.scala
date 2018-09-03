package com.elleflorio.scalachain.proof

import org.scalatest.{FlatSpecLike, Matchers}

class ProofOfWorkTest extends Matchers
  with FlatSpecLike {

  "Validation of com.elleflorio.scalachain.proof" should "correctly validate proofs" in {
    val lastHash = "1"
    val correctProof = 7178
    val wrongProof = 0

    ProofOfWork.validProof(lastHash, correctProof) should be (true)

    ProofOfWork.validProof(lastHash, wrongProof) should be (false)
  }
}
