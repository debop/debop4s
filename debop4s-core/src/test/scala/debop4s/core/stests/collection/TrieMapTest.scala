package debop4s.core.stests.collection

import debop4s.core.stests.AbstractCoreTest
import scala.collection.parallel.mutable.ParTrieMap

/**
 * TreiMapTest
 * Created by debop on 2014. 3. 15.
 */
class TrieMapTest extends AbstractCoreTest {

  case class Entry(num: Double) {
    var sqrt = num
  }

  val count = 50

  test("ParTrieMap 테스트") {
    val entries = 1 to count map {
      num => Entry(num.toDouble)
    }
    val results = ParTrieMap[Double, Entry]()
    for (e <- entries) {
      results += ((e.num, e))
    }

    // compre squre root
    while (results.nonEmpty) {
      results.foreach { case (num, e) =>
        val nsqrt = 0.5 * (e.sqrt + e.num / e.sqrt)
        if (math.abs(nsqrt - e.sqrt) < 0.01) {
          results.remove(num)
        } else {
          e.sqrt = nsqrt
        }
      }
    }
    println(entries)
  }

}
