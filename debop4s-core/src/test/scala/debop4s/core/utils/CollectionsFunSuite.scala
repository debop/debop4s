package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite
import scala.collection.mutable

/**
 * CollectionsFunSuite
 * @author Sunghyouk Bae
 */
class CollectionsFunSuite extends AbstractCoreFunSuite {

  val COUNT = 1000
  val map = mutable.LinkedHashMap[String, Int]()


  before {
    map.clear()
    for (i <- 0 until COUNT) {
      map.put("KEY-" + "00000".format(i), COUNT - i)
    }
  }

  test("값으로 정렬하기") {
    val sortedByValue = mutable.LinkedHashMap(map.toSeq.sortBy(_._2): _*)

    val keys = map.keys.toSeq
    val sortedKeys = sortedByValue.keys.toSeq

    //        println(s"keys=$keys")
    //        println(s"sortedKeys=$sortedKeys")
    assert(sortedKeys.sameElements(keys.reverse))
  }

  test("Key 로 정렬하기") {
    val sortedByKey = CollectionUtils.sortAscByKey(map.toMap)
    assert(sortedByKey.keys.sameElements(map.keys))

    val reversedByKey = CollectionUtils.sortDescByKey(map.toMap)
    assert(reversedByKey.keys.toSeq.reverse.sameElements(map.keys))
  }

  test("Value 로 정렬하기") {
    val sortedByValue = CollectionUtils.sortAscByValue(map.toMap)
    assert(sortedByValue.keys.toSeq.reverse.sameElements(map.keys))

    val reversedByValue = CollectionUtils.sortDescByValue(map.toMap)
    assert(reversedByValue.keys.sameElements(map.keys))
  }
}
