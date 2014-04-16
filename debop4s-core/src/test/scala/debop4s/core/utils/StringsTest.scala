package debop4s.core.utils

import debop4s.core.AbstractCoreTest

/**
 * StringsTest
 * Created by debop on 2014. 3. 16.
 */
class StringsTest extends AbstractCoreTest {

    test("Strings.isNull") {
        assert(Strings.isNull(null))
        assert(!Strings.isNull(""))
        assert(!Strings.isNotNull(null))
        assert(Strings.isNotNull(""))
    }

    test("Strings.isEmpty") {
        assert(Strings.isEmpty(null))
        assert(Strings.isEmpty(""))
        assert(Strings.isEmpty("      "))
        assert(!Strings.isEmpty("null"))

        assert(!Strings.isNotEmpty(null))
        assert(!Strings.isNotEmpty(""))
        assert(!Strings.isNotEmpty("      "))
        assert(Strings.isNotEmpty("null"))
    }

    test("Strings isWhitespace printable 문자열이 아니면 빈 문자열로 본다.") {
        assert(Strings.isWhitespace(null))
        assert(Strings.isWhitespace(""))
        assert(Strings.isWhitespace("   \t  "))
        assert(Strings.isWhitespace("   \r  "))
        assert(!Strings.isWhitespace("null"))

        assert(!Strings.isNotWhitespace(null))
        assert(!Strings.isNotWhitespace(""))
        assert(!Strings.isNotWhitespace("  \t    "))
        assert(!Strings.isNotWhitespace("  \r    "))
        assert(Strings.isNotWhitespace("null"))
    }

    test("Strings join") {
        implicit val separator = ","

        val strs = Array("a", "bc", "def")
        Strings.join(strs: _*) shouldEqual "a,bc,def"
        Strings.join(strs: _*)("") shouldEqual "abcdef"
        Strings.join(strs: _*)("|") shouldEqual "a|bc|def"
    }

    test("String split") {

        val str = "동해,물 || 백두,산 a BaB"
        val strArr = Strings.split(str, ",", "||", "A")
        strArr shouldEqual Array("동해", "물", "백두", "산", "B", "B")

        val strArr2 = Strings.split(str, false, true, ",", "||", "A")
        strArr2 shouldEqual Array("동해", "물", "백두", "산 a BaB")
    }

    test("Base64 converting") {
        val text = "동해물과 백두산이 Hello Word! http://github.com/debop"

        val base64String = Strings.encodeBase64String(text)
        val converted = Strings.decodeBase64String(base64String)
        converted shouldEqual text
    }

}
