package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.stests.AbstractCoreTest
import scala.collection.mutable
import com.github.debop4s.core.utils.Collections

/**
 * CollectionsTest
 * @author Sunghyouk Bae
 */
class CollectionsTest extends AbstractCoreTest {

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
        val sortedByKey = Collections.sortAscByKey(map.toMap)
        assert(sortedByKey.keys.sameElements(map.keys))

        val reversedByKey = Collections.sortDescByKey(map.toMap)
        assert(reversedByKey.keys.toSeq.reverse.sameElements(map.keys))
    }

    test("Value 로 정렬하기") {
        val sortedByValue = Collections.sortAscByValue(map.toMap)
        assert(sortedByValue.keys.toSeq.reverse.sameElements(map.keys))

        val reversedByValue = Collections.sortDescByValue(map.toMap)
        assert(reversedByValue.keys.sameElements(map.keys))
    }
}
