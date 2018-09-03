package com.elleflorio.scalachain.blockchain

import org.scalatest.{FlatSpecLike, Matchers}

class ChainTest extends Matchers
  with FlatSpecLike {

  "A Chain" should "be correctly built" in {
    val transaction = new Transaction("me", "you", 1)

    val empty = Chain()
    empty.index should be (0)

    val link = new ChainLink(1, 1, List(transaction), "abc", empty)
    val justOne = Chain(link)
    justOne.index should be (1)

    val link2 = new ChainLink(2, 1, List(transaction), "abc", empty)
    val justTwo = link2 :: link

    justTwo.index should be (2)
  }

}
