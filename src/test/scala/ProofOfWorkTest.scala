import org.scalatest.FunSuite
import proof.ProofOfWork

class ProofOfWorkTest extends FunSuite {

  test("ProofOfWork.validProof") {
    val lastHash = "abcdefg"

    assert(ProofOfWork.validProof(lastHash, 123456789434523452L))
  }
}
