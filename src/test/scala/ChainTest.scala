import blockchain._
import org.scalatest.FunSuite

class ChainTest extends FunSuite{

  test("Chain.apply") {
    val empty = Chain()
    assert(empty.size == 0)

    val transaction = new Transaction("me", "you", 1)
    val block = new Block(List(transaction), 1)
    val link = new ChainLink(0, block, "abc", empty)
    val justOne = Chain(link)
    assert(justOne.size == 1)
  }

}
