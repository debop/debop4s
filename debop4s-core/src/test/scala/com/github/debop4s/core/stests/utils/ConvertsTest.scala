package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.utils.Converts

/**
 * ConvertsTest
 * @author Sunghyouk Bae
 */
class ConvertsTest extends AbstractCoreTest {

    test("toInt 변환") {
        assert(Converts.toInt(1) == 1)
        assert(Converts.toInt("1") == 1)
        assert(Converts.toInt("12") == 12)
        assert(Converts.toInt("") == 0)
        assert(Converts.toInt("abc") == 0)
        assert(Converts.toInt(null) == 0)
    }

    test("toLong 변환") {
        assert(Converts.toLong(1) == 1)
        assert(Converts.toLong("1") == 1)
        assert(Converts.toLong("12") == 12)
        assert(Converts.toLong("") == 0)
        assert(Converts.toLong("abc") == 0)
        assert(Converts.toLong(null) == 0)
    }

    test("toFloat 변환") {
        assert(Converts.toFloat(1) == 1)
        assert(Converts.toFloat("1") == 1)
        assert(Converts.toFloat("12") == 12)
        assert(Converts.toFloat("") == 0)
        assert(Converts.toFloat("abc") == 0)
        assert(Converts.toFloat(null) == 0)
    }

    test("toDouble 변환") {
        assert(Converts.toDouble(1) == 1)
        assert(Converts.toDouble("1") == 1)
        assert(Converts.toDouble("12") == 12)
        assert(Converts.toDouble("") == 0)
        assert(Converts.toDouble("abc") == 0)
        assert(Converts.toDouble(null) == 0)
    }
}
