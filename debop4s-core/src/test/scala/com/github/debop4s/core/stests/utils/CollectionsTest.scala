package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.stests.AbstractCoreTest
import scala.collection.mutable

/**
 * CollectionsTest
 * @author Sunghyouk Bae
 */
class CollectionsTest extends AbstractCoreTest {

    val COUNT = 100
    val map = mutable.LinkedHashMap[String, Int]()


    before {
        for (i <- 0 until COUNT) {
            map.put("KEY-" + i, COUNT - i)
        }
    }

    test("값으로 정렬하기") {
        val sortedByValue = map.toList.sortBy(_._2)

        val keys = map.keys
        val sortedKeys = sortedByValue.map(_._1)

        assert(sortedKeys.sameElements(keys.toSeq.reverse))
    }
}
