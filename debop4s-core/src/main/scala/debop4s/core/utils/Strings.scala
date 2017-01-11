package debop4s.core.utils

import java.lang.{Iterable => JIterable}
import java.nio.charset.Charset
import java.util
import java.util.{Collection => JCollection, List => JList, Map => JMap}

import debop4s.core.BinaryStringFormat
import org.apache.commons.codec.binary.{Base64, Hex}
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchData

/**
 * debop4s.core.tools.String
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:06
 */
object Strings {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)
  /**
   * 한국어 등 Multi byte 문자열임을 나타내는 접두사입니다.
   */
  private[this] lazy val MULTI_BYTES_PREFIX: Array[Byte] = Array(0xEF.toByte, 0xBB.toByte, 0xBF.toByte)

  lazy val TRIMMING_STR = "..."
  lazy val NULL_STR = "<null>"
  lazy val EMPTY_STR = ""
  lazy val COMMA_STR = ","

  /** 시스템의 Line 구분자 : LF | CRLF | CR */
  private[this] lazy val LINE_SEPARATOR = System.getProperty("line.separator")

  /** 빈 공간을 찾는 RegEx */
  private[this] lazy val WHITESPACE_BLOCK = "\\s+".r

  val UTF8: Charset = Charsets.UTF_8

  def asString(x: Any): String = x match {
    case None => None.toString
    case null => NULL_STR
    case _ => x.toString
  }

  def isNull(cs: CharSequence): Boolean = cs == null

  def isNotNull(cs: CharSequence): Boolean = cs != null

  def isEmpty(cs: String): Boolean = isEmpty(cs, doTrim = true)

  def isEmpty(cs: String, doTrim: Boolean): Boolean = {
    if (cs == null)
      return true

    if (doTrim) cs.trim.length == 0
    else cs.length == 0
  }

  def isNotEmpty(str: String): Boolean = isNotEmpty(str, doTrim = true)

  def isNotEmpty(cs: String, doTrim: Boolean): Boolean = !isEmpty(cs, doTrim)


  def isWhitespace(cs: CharSequence): Boolean = {
    if (cs == null)
      return true

    val sz = cs.length
    var i = 0
    while (i < sz) {
      if (!Character.isWhitespace(cs.charAt(i)))
        return false
      i += 1
    }
    true
  }

  def isNotWhitespace(cs: CharSequence): Boolean = !isWhitespace(cs)

  def isMultiByteString(bytes: Array[Byte]): Boolean = {
    if (bytes == null || bytes.length < MULTI_BYTES_PREFIX.length)
      return false

    MULTI_BYTES_PREFIX.sameElements(bytes.take(MULTI_BYTES_PREFIX.length))
  }

  def isMultiByteString(str: CharSequence): Boolean = {
    if (isWhitespace(str))
      return false

    val bytes = str.subSequence(0, math.min(2, str.length())).toString.getBytes(Charsets.US_ASCII)
    isMultiByteString(bytes)
  }

  def contains(str: String, subStr: String): Boolean =
    isNotEmpty(str) && str.contains(subStr)

  def needEllipsis(str: String, maxLength: Int = 80): Boolean =
    isNotEmpty(str) && str.length() > maxLength


  def ellipsisChar(str: String, maxLength: Int = 80): String = {
    if (isEmpty(str) || !needEllipsis(str, maxLength))
      str
    else
      str.substring(0, maxLength - TRIMMING_STR.length) + TRIMMING_STR
  }


  def ellipsisPath(str: String, maxLength: Int = 80): String = {
    if (isEmpty(str) || !needEllipsis(str, maxLength))
      return str

    val length = maxLength / 2

    val builder = new StringBuilder()
    builder.append(str.substring(0, length)).append(TRIMMING_STR)

    if (maxLength % 2 == 0)
      builder.append(str.substring(str.length - length))
    else
      builder.append(str.substring(str.length - length - 1))

    builder.toString()
  }


  def ellipsisFirst(str: String, maxLength: Int = 80): String = {
    if (isEmpty(str) || !needEllipsis(str, maxLength))
      return str

    TRIMMING_STR + str.substring(str.length - maxLength)
  }


  def intToHex(n: Int): Char = {
    if (n < 10) (n + 48).asInstanceOf[Char]
    else (n - 10 + 97).asInstanceOf[Char]
  }


  def hexToInt(h: Char): Int = {
    if (h > '0' && h <= '9') h - '0'
    else if (h > 'a' && h < 'f') h - 'a' + 10
    else if (h > 'A' && h < 'F') h - 'A' + 10
    else -1
  }

  /** 바이트 배열을 문자열로 표현합니다. */
  def toHexString(bytes: Array[Byte]): String = {
    if (bytes == null || bytes.length == 0) EMPTY_STR
    else toHexString(bytes, 0, bytes.length)
  }

  /** 바이트 배열을 문자열로 표현합니다. */
  def toHexString(bytes: Array[Byte], from: Int, to: Int): String = {
    val sb = new StringBuilder((to - from) * 2)

    var i = from
    while (i < to) {
      val b = bytes(i)
      val s = Integer.toHexString(b.toInt & 0xff)
      if (s.length < 2) sb append '0'
      sb append s
      i += 1
    }
    sb.toString()
  }

  /**
   * 16 진수 형태로 표현된 문자열을 실제 16진수 byte array 로 변환합니다.
   * @see `toHexString`
   */
  def fromHexString(str: String): Array[Byte] = {
    val buffer = new Array[Byte]((str.length + 1) / 2)

    (str.grouped(2).toSeq zipWithIndex) foreach {
      case (substr, i) =>
        buffer(i) = Integer.parseInt(substr, 16).toByte
    }
    buffer
  }

  def getBytesFromHexString(hexString: String): Array[Byte] =
    if (isEmpty(hexString)) Array.emptyByteArray
    else Hex.decodeHex(hexString.toCharArray)

  def getHexString(bytes: Array[Byte]): String =
    Hex.encodeHexString(bytes)

  def encodeBase64(input: Array[Byte]): Array[Byte] =
    Base64.encodeBase64URLSafe(input)

  def encodeBase64String(input: Array[Byte]): String =
    new String(encodeBase64(input), Charsets.UTF_8)

  def encodeBase64String(str: String): String =
    encodeBase64String(getUtf8Bytes(str))

  def decodeBase64(base64Data: Array[Byte]): Array[Byte] =
    Base64.decodeBase64(base64Data)

  def decodeBase64(base64String: String): Array[Byte] =
    Base64.decodeBase64(base64String)

  def decodeBase64String(base64Data: Array[Byte]): String =
    getUtf8String(decodeBase64(base64Data))

  def decodeBase64String(base64String: String): String =
    getUtf8String(decodeBase64(base64String))

  def getUtf8Bytes(str: String): Array[Byte] =
    if (isEmpty(str)) Arrays.EMPTY_BYTE_ARRAY
    else str.getBytes(Charsets.UTF_8)

  def getUtf8String(bytes: Array[Byte]) =
    if (Arrays.isEmpty(bytes)) EMPTY_STR
    else new String(bytes, Charsets.UTF_8)

  def getString(bytes: Array[Byte], charsetName: String = "UTF-8"): String =
    if (Arrays.isEmpty(bytes)) EMPTY_STR
    else new String(bytes, Charset.forName(charsetName))

  def getStringFromBytes(bytes: Array[Byte],
                         format: BinaryStringFormat = BinaryStringFormat.HexDecimal): String =
    format match {
      case BinaryStringFormat.HexDecimal => getHexString(bytes)
      case _ => encodeBase64String(bytes)
    }

  def getBytesFromString(str: String,
                         format: BinaryStringFormat = BinaryStringFormat.HexDecimal): Array[Byte] =
    format match {
      case BinaryStringFormat.HexDecimal => getBytesFromHexString(str)
      case _ => decodeBase64(str)
    }


  def deleteCharAny(cs: CharSequence, chars: Char*): String = {
    if (cs == null || cs.length == 0 || chars == null || chars.length == 0)
      return EMPTY_STR

    val builder = new StringBuilder()
    var i = 0
    while (i < cs.length()) {
      val c = cs.charAt(i)
      if (!chars.contains(c)) {
        builder.append(c)
      }
      i += 1
    }
    builder.toString()
  }

  def deleteChar(cs: CharSequence, chars: Array[Char]): String = {
    if (cs == null || cs.length == 0 || chars == null || chars.length == 0)
      return EMPTY_STR

    val builder = new StringBuilder()
    var i = 0
    while (i < cs.length()) {
      val c = cs.charAt(i)
      if (!chars.contains(c)) {
        builder.append(c)
      }
      i += 1
    }
    builder.toString()
  }

  def deleteChar(cs: CharSequence, dc: Char): String = {
    if (cs == null || cs.length == 0)
      return EMPTY_STR

    val builder = new StringBuilder()

    var i = 0
    while (i < cs.length()) {
      val c = cs.charAt(i)
      if (c != dc) builder.append(c)
      i += 1
    }
    builder.toString()
  }

  @varargs
  def concat(items: Any*): String = {
    val builder = new StringBuilder()
    var i = 0
    while (i < items.length) {
      builder.append(asString(items(i)))
      i += 1
    }
    builder.toString()
  }

  @varargs
  def concat(items: Any*)(implicit separator: String = ""): String = {
    if (items == null || items.isEmpty)
      return ""

    val builder = new StringBuilder()

    var isFirst = true
    var i = 0
    while (i < items.length) {
      if (!isFirst) {
        builder.append(separator)
        isFirst = false
      }
      builder.append(items(i))
      i += 1
    }

    builder.toString()
  }

  def subSequence(cs: CharSequence, fromIndex: Int = 0): CharSequence = {
    if (cs == null) null else cs.subSequence(fromIndex, cs.length())
  }

  def indexOf(cs: CharSequence, searchChar: Int, fromIndex: Int = 0): Int = {
    cs match {
      case s: String => s.indexOf(searchChar, fromIndex)
      case _ =>
        var i = fromIndex
        while (i < cs.length) {
          if (cs.charAt(i) == searchChar)
            return i
          i += 1
        }
        -1
    }
  }

  def lastIndexOf(cs: CharSequence, searchChar: Int, fromIndex: Int = 0): Int = {
    cs match {
      case s: String => s.lastIndexOf(searchChar, fromIndex)
      case _ =>
        var start = fromIndex
        if (start >= cs.length)
          start = cs.length - 1

        var i = start
        while (i >= 0) {
          if (cs.charAt(i) == searchChar)
            return i
          i -= 1
        }
        -1
    }
  }


  def toCharArray(cs: CharSequence): Array[Char] = {
    cs match {
      case s: String => s.toCharArray
      case _ =>
        val sz = cs.length()
        val array = new Array[Char](sz)

        var i = 0
        while (i < sz) {
          array(i) = cs.charAt(i)
          i += 1
        }
        array
    }
  }

  def regionMathches(cs: CharSequence,
                     ignoreCase: Boolean,
                     toffset: Int,
                     other: CharSequence,
                     ooffset: Int,
                     len: Int): Boolean = {
    cs match {
      case s: String if other.isInstanceOf[String] =>
        s.regionMatches(ignoreCase, toffset, other.asInstanceOf[String], ooffset, len)
      case _ =>
        // todo String#regionMatches, String#indexOf 를 참고해서 구현해야 한다.
        cs.toString.regionMatches(ignoreCase, toffset, other.toString, ooffset, len)
    }
  }

  def mkString(items: Iterable[_], separator: String): String = {
    val sb = new StringBuilder()
    mkString(sb, items, separator)
    sb.toString()
  }

  def mkString(sb: StringBuilder, items: Iterable[_], separator: String): Unit = {
    var sp = ""
    var first = true
    items.foreach { item =>
      sb.append(sp).append(item)
      if (first) {
        first = false
        sp = separator
      }
    }
  }

  def mkString(items: Map[_, _], separator: String): String = {
    val sb = new StringBuilder()
    mkString(sb, items, separator)
    sb.toString()
  }

  def mkString(sb: StringBuilder, items: Map[_, _], separator: String): Unit = {
    var sp = ""
    var first = true

    items.foreach { item =>
      sb.append(sp).append(item._1).append("=").append(item._2)
      if (first) {
        first = false
        sp = separator
      }
    }
  }

  def join(items: java.lang.Iterable[_]): String = mkString(items.asScala, COMMA_STR)
  def join(items: java.lang.Iterable[_], separator: String): String = mkString(items.asScala, separator)
  def join(items: Iterable[_]): String = mkString(items, COMMA_STR)
  def join(items: Iterable[_], separator: String): String = mkString(items, separator)
  def join(items: Array[String]): String = mkString(items, COMMA_STR)
  def join(items: Array[String], separator: String): String = mkString(items, separator)

  def quotedStr(str: String): String =
    if (isNull(str)) NULL_STR
    else String.format("\'%s\'", str.replace("\'", "\'\'"))


  def quotedStr(str: String, defaultStr: String): String =
    if (isWhitespace(str)) quotedStr(defaultStr)
    else quotedStr(str)

  def reverse(str: String): String = str.reverse

  def replicate(str: String, n: Int): String = str * n

  @varargs
  def split(str: String, separators: String*): Array[String] =
    split(str, ignoreCase = true, removeEmptyEntries = true, separators: _*)

  @varargs
  def split(str: String, ignoreCase: Boolean, removeEmptyEntries: Boolean, separators: String*): Array[String] = {
    if (isEmpty(str))
      return Array[String]()

    val results = new ArrayBuffer[String]()
    val seps = new ArrayBuffer[Array[Char]]()
    for (sep <- separators) {
      if (ignoreCase) seps += sep.toLowerCase.toCharArray
      else seps += sep.toCharArray
    }
    val strArray = if (ignoreCase) str.toLowerCase.toCharArray else str.toCharArray

    var startIndex = 0
    var prevIndex = 0

    while (startIndex < strArray.length) {
      var continue = true
      for (sep <- seps if continue) {
        if (sep.sameElements(strArray.drop(startIndex).take(sep.length))) {
          val item = str.substring(prevIndex, startIndex)
          if (item != null && item.length > 0) {
            if (removeEmptyEntries) {
              if (isNotWhitespace(item))
                results += item.trim
            } else {
              results += item
            }
          }
          prevIndex = startIndex + sep.length
          startIndex = startIndex + sep.length

          continue = false
        }
      }
      startIndex += 1
    }

    if (prevIndex < strArray.length) {
      if (removeEmptyEntries)
        results += str.substring(prevIndex).trim
      else
        results += str.substring(prevIndex)
    }
    results.toArray

  }

  def wordCount(str: String, word: String, ignoreCase: Boolean = true): Int = {
    if (isEmpty(str) || isEmpty(word))
      return 0

    val targetStr = if (ignoreCase) str.toLowerCase else str
    val searchWord = if (ignoreCase) word.toLowerCase else word

    val wordLength = searchWord.length
    val maxLength = targetStr.length - wordLength

    var count = 0
    var index = 0
    while (index >= 0 && index <= maxLength) {
      index = targetStr.indexOf(searchWord, index)

      if (index >= 0) {
        count += 1
        index += wordLength
      }
    }
    count
  }

  def getFirstLine(str: String): String = {
    getFirstLine(str, LINE_SEPARATOR)
  }

  def getFirstLine(str: String, lineSeparator: String): String = {
    if (isEmpty(str))
      return str

    val index = str.indexOf(lineSeparator)

    if (index > 0) str.substring(0, index)
    else str
  }

  def getBetween(text: String, start: String, end: String): String = {
    if (isEmpty(text))
      return text

    var startIndex = 0
    if (isNotEmpty(start)) {
      val index = text.indexOf(start, startIndex)
      if (index > -1)
        startIndex = index + start.length
    }

    var endIndex = text.length - 1
    if (isNotEmpty(end)) {
      val index = text.lastIndexOf(start)
      if (index > -1)
        endIndex = index
    }

    if (endIndex >= startIndex)
      text.substring(startIndex, endIndex + 1)
    else
      EMPTY_STR
  }


  def objectToString(obj: Any): String =
    asString(obj)

  @varargs
  def listToString(item: Any, items: Any*): String =
    join((Seq(item) ++ items).toIterable)


  def listToString(items: java.lang.Iterable[_]): String =
    if (items == null) NULL_STR
    else join(items)


  def listToString(items: Array[_]): String =
    if (items == null) NULL_STR
    else join(items.toList)


  def mapToString(map: util.Map[_, _], openStr: String = "{", delimeter: String = ",", closeStr: String = "}"): String =
    if (map == null) NULL_STR
    else openStr + join(mapToEntityList(map), delimeter) + closeStr


  def mapToEntityList(map: util.Map[_, _]): util.List[String] = {
    val list = new util.ArrayList[String](map.size())
    map.asScala.foreach { case (k, v) =>
      list.add(k.toString + "=" + v.toString)
    }
    list
  }


  def regexSub(cs: CharSequence, re: Regex)(replace: (Regex.MatchData => String)): String = {
    var offset = 0
    val out = new StringBuilder()

    val matches = re.findAllIn(cs).matchData

    while (matches.hasNext) {
      val m = matches.next()
      if (m.start > offset) {
        out.append(cs.subSequence(offset, m.start))
      }
      out.append(replace(m))
      offset = m.end
    }

    if (offset < cs.length())
      out.append(cs.subSequence(offset, cs.length()))

    out.toString()
  }

  /**
   * quote 를 구분할 RegEx
   */
  private[this] lazy val QUOTE_RE = "[\u0000-\u001f\u007f-\uffff\\\\\"]".r

  /**
   * 프린트 불가한 char 를 c 스타일로 quote 해 준다.
   * 예를 들어 linefeed 를 `"\n"` 으로 표시해 준다.
   */
  def quoteC(cs: CharSequence): String = {
    regexSub(cs, QUOTE_RE) { m: MatchData =>
      m.matched.charAt(0) match {
        case '\r' => "\\r"
        case '\n' => "\\n"
        case '\t' => "\\t"
        case '"' => "\\\""
        case '\\' => "\\\\"
        case c =>
          if (c <= 255) "\\x%02x" format c.asInstanceOf[Int]
          else "\\u%04x" format c.asInstanceOf[Int]
      }
    }
  }

  // we intentionally don't unquote "\$" here, so it can be used to escape interpolation later.
  private[this] lazy val UNQUOTE_RE = """\\(u[\dA-Fa-f]{4}|x[\dA-Fa-f]{2}|[/rnt\"\\])""".r

  /**
   * Unquote an ASCII string that has been quoted in a style like
   * `quoteC` and convert it into a standard unicode string.
   * `"\\uHHHH"` and `"\xHH"` expressions are unpacked
   * into unicode characters, as well as `"\r"`, `"\n"`,
   * `"\t"`, `"\\"`, and `'\"'`.
   *
   * @return an unquoted unicode string
   */
  def unquoteC(cs: CharSequence): String = {
    regexSub(cs, UNQUOTE_RE) {
      m: MatchData =>
        val ch = m.group(1).charAt(0) match {
          case 'u' => Character.valueOf(Integer.valueOf(m.group(1).substring(1), 16).asInstanceOf[Int].toChar)
          case 'x' => Character.valueOf(Integer.valueOf(m.group(1).substring(1), 16).asInstanceOf[Int].toChar)
          case 'r' => '\r'
          case 'n' => '\n'
          case 't' => '\t'
          case x => x
        }
        ch.toString
    }
  }
}
