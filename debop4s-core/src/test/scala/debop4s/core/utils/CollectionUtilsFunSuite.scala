package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

import scala.collection.mutable

/**
 * CollectionsTest
 * @author Sunghyouk Bae
 */
class CollectionUtilsFunSuite extends AbstractCoreFunSuite {

  val COUNT = 1000
  val map = mutable.LinkedHashMap[String, Int]()


  before {
    map.clear()
    var i = 0
    while (i < COUNT) {
      map.put("KEY-" + "00000".format(i), COUNT - i)
      i += 1
    }
  }

  test("값으로 정렬하기") {
    val sortedByValue = mutable.LinkedHashMap(map.toSeq.sortBy(_._2): _*)

    val keys = map.keys.toSeq
    val sortedKeys = sortedByValue.keys.toSeq

    //        println(s"keys=$keys")
    //        println(s"sortedKeys=$sortedKeys")
    sortedKeys shouldEqual keys.reverse
  }

  test("Key 로 정렬하기") {
    val sortedByKey = CollectionUtils.sortAscByKey(map.toMap)
    sortedByKey.keys shouldEqual map.keys

    val reversedByKey = CollectionUtils.sortDescByKey(map.toMap)
    reversedByKey.keys shouldEqual map.keys
  }

  test("Value 로 정렬하기") {
    val sortedByValue = CollectionUtils.sortAscByValue(map.toMap)
    sortedByValue.keys shouldEqual map.keys

    val reversedByValue = CollectionUtils.sortDescByValue(map.toMap)
    reversedByValue.keys shouldEqual map.keys
  }
}
