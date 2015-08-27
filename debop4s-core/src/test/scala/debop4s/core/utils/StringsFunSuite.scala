package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

class StringsFunSuite extends AbstractCoreFunSuite {

  test("Strings.isNull") {
    Strings.isNull(null) shouldBe true
    Strings.isNull("") shouldBe false
    Strings.isNotNull(null) shouldBe false
    Strings.isNotNull("") shouldBe true
  }

  test("Strings.isEmpty") {
    Strings.isEmpty(null) shouldBe true
    Strings.isEmpty("") shouldBe true
    Strings.isEmpty("      ") shouldBe true
    !Strings.isEmpty("null") shouldBe true

    !Strings.isNotEmpty(null) shouldBe true
    !Strings.isNotEmpty("") shouldBe true
    !Strings.isNotEmpty("      ") shouldBe true
    Strings.isNotEmpty("null") shouldBe true
  }

  test("Strings isWhitespace printable 문자열이 아니면 빈 문자열로 본다.") {
    Strings.isWhitespace(null) shouldBe true
    Strings.isWhitespace("") shouldBe true
    Strings.isWhitespace("   \t  ") shouldBe true
    Strings.isWhitespace("   \r  ") shouldBe true
    !Strings.isWhitespace("null") shouldBe true

    !Strings.isNotWhitespace(null) shouldBe true
    !Strings.isNotWhitespace("") shouldBe true
    !Strings.isNotWhitespace("  \t    ") shouldBe true
    !Strings.isNotWhitespace("  \r    ") shouldBe true
    Strings.isNotWhitespace("null") shouldBe true
  }

  test("Strings join") {
    implicit val separator = ","

    val strs = Array("a", "bc", "def")
    Strings.join(strs) shouldBe "a,bc,def"
    Strings.join(strs, "") shouldBe "abcdef"
    Strings.join(strs, "|") shouldBe "a|bc|def"
  }

  test("String split") {

    val str = "동해,물 || 백두,산 a BaB"
    val strArr = Strings.split(str, ",", "||", "A")
    strArr shouldBe Array("동해", "물", "백두", "산", "B", "B")

    val strArr2 = Strings.split(str, false, true, ",", "||", "A")
    strArr2 shouldBe Array("동해", "물", "백두", "산 a BaB")
  }

  test("String split with ignoreCase") {
    val text: String = "Hello World! Hello java^^"

    var result: Array[String] = Strings.split(text, true, true, "!")
    log.debug("Result=[{}]", Strings.listToString(result))
    result.length shouldBe 2

    result = Strings.split(text, false, true, "hello")
    log.debug("Result=[{}]", Strings.listToString(result))
    result.length shouldBe 1

    result = Strings.split(text, true, true, "hello")
    log.debug("Result=[{}]", Strings.listToString(result))
    result.length shouldBe 2

    result = Strings.split(text, true, true, "hello", "JAVA")
    log.debug("Result=[{}]", Strings.listToString(result))
    result.length shouldBe 2

    result = Strings.split(text, true, true, "||")
    log.debug("Result=[{}]", result.mkString(","))
    result.length shouldBe 1
  }

  test("Base64 converting") {
    val text = "동해물과 백두산이 Hello Word! http://github.com/debop"

    val base64String = Strings.encodeBase64String(text)
    val converted = Strings.decodeBase64String(base64String)
    converted shouldBe text
  }

  test("UTF8 String/Array[Byte]") {
    val text = "동해물과 백두산이 Hello Word! http://github.com/debop"

    val bytes = Strings.getUtf8Bytes(text)
    val converted = Strings.getUtf8String(bytes)

    converted shouldBe text
  }

}
