package com.github.debop4s.core.stests.utils

import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.utils.Strings
import com.github.debop4s.core.utils.Strings._

/**
 * StringsTest
 * Created by debop on 2014. 3. 16.
 */
class StringsTest extends AbstractCoreTest {

    test("Strings.isNull") {
        assert(isNull(null))
        assert(!isNull(""))
        assert(!isNotNull(null))
        assert(isNotNull(""))
    }

    test("Strings.isEmpty") {
        assert(isEmpty(null))
        assert(isEmpty(""))
        assert(isEmpty("      "))
        assert(!isEmpty("null"))

        assert(!isNotEmpty(null))
        assert(!isNotEmpty(""))
        assert(!isNotEmpty("      "))
        assert(isNotEmpty("null"))
    }

    test("Strings isWhitespace printable 문자열이 아니면 빈 문자열로 본다.") {
        assert(isWhitespace(null))
        assert(isWhitespace(""))
        assert(isWhitespace("   \t  "))
        assert(isWhitespace("   \r  "))
        assert(!isWhitespace("null"))

        assert(!isNotWhitespace(null))
        assert(!isNotWhitespace(""))
        assert(!isNotWhitespace("  \t    "))
        assert(!isNotWhitespace("  \r    "))
        assert(isNotWhitespace("null"))
    }

    test("Strings join") {
        val strs = Array("a", "bc", "def")
        assert(Strings.join(strs) == "a,bc,def")
        assert(Strings.join(strs)("") == "abcdef")
        assert(Strings.join(strs)("|") == "a|bc|def")
    }

    test("String split") {

        val str = "동해,물 || 백두,산 a BaB"
        val strArr = split(str, ",", "||", "A")
        assert(strArr.sameElements(Array("동해", "물", "백두", "산", "B", "B")))

        val strArr2 = split(str, false, true, ",", "||", "A")
        assert(strArr2.sameElements(Array("동해", "물", "백두", "산 a BaB")))
    }

    test("Base64 converting") {
        val text = "동해물과 백두산이 Hello Word! http://github.com/debop"

        val base64String = encodeBase64String(text)
        val converted = decodeBase64String(base64String)
        assert(converted == text)
    }

}
