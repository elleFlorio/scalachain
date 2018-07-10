import blockchain._
import org.scalatest.FunSuite

class ChainTest extends FunSuite{

  test("Chain.apply") {
    val empty = Chain()
    assert(empty.index == 0)

    val transaction = new Transaction("me", "you", 1)
    val link = new ChainLink(1, 1,  List(transaction), "abc", empty)
    val justOne = Chain(link)
    assert(justOne.index == 1)

    val transaction2 = new Transaction("me", "you", 1)
    val link2 = new ChainLink(2, 1,  List(transaction), "abc", empty)
    val justTwo = link2 :: link
    assert(justTwo.index == 2)
  }

}
