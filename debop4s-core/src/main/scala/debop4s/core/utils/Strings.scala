package debop4s.core.utils

import debop4s.core.BinaryStringFormat
import debop4s.core.BinaryStringFormat.BinaryStringFormat
import java.nio.charset.Charset
import java.util
import org.apache.commons.codec.binary.{Base64, Hex}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchData

/**
 * debop4s.core.tools.String
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:06
 */
object Strings {

    private lazy val log = LoggerFactory.getLogger(getClass)

    lazy val MULTI_BYTES_PREFIX = Array(0xEF.toByte, 0xBB.toByte, 0xBF.toByte)
    lazy val TRIMMING_STR = "..."
    lazy val NULL_STR = "<null>"
    lazy val EMPTY_STR = ""
    lazy val COMMA_STR = ","
    lazy val LINE_SEPARATOR = System.getProperty("line.separator")

    val WHITESPACE_BLOCK = "\\s+".r

    // val UTF8: Charset = Charsets.UTF_8

    // join 함수의 암묵적 인자로 사용됩니다.
    implicit val separator: String = COMMA_STR

    @inline
    def asString(x: Any): String = x match {
        case null => NULL_STR
        case None => None.toString
        case _ => x.toString
    }

    @inline
    def isNull(cs: CharSequence): Boolean = cs == null

    @inline
    def isNotNull(cs: CharSequence): Boolean = cs != null

    @inline
    def isEmpty(cs: String): Boolean = isEmpty(cs, doTrim = true)

    @inline
    def isEmpty(cs: String, doTrim: Boolean = true): Boolean = {
        if (cs == null)
            return true

        if (doTrim) cs.trim.length == 0
        else cs.length == 0
    }

    @inline
    def isNotEmpty(str: String): Boolean = isNotEmpty(str, doTrim = true)

    @inline
    def isNotEmpty(cs: String, doTrim: Boolean): Boolean = !isEmpty(cs, doTrim)

    @inline
    def isWhitespace(cs: CharSequence): Boolean = {
        if (cs == null)
            return true

        val sz = cs.length
        for (i <- 0 until sz) {
            if (!Character.isWhitespace(cs.charAt(i)))
                return false
        }
        true
    }

    @inline
    def isNotWhitespace(cs: CharSequence): Boolean = !isWhitespace(cs)

    @inline
    def isMultiByteString(bytes: Array[Byte]): Boolean = {
        if (bytes == null || bytes.length < MULTI_BYTES_PREFIX.length)
            return false

        MULTI_BYTES_PREFIX.sameElements(bytes.take(MULTI_BYTES_PREFIX.length))
    }

    @inline
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

    @inline
    def ellipsisChar(str: String, maxLength: Int = 80): String = {
        if (isEmpty(str) || !needEllipsis(str, maxLength))
            return str

        str.substring(0, maxLength - TRIMMING_STR.length) + TRIMMING_STR
    }

    @inline
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

    @inline
    def ellipsisFirst(str: String, maxLength: Int = 80): String = {
        if (isEmpty(str) || !needEllipsis(str, maxLength))
            return str

        TRIMMING_STR + str.substring(str.length - maxLength)
    }

    @inline
    def intToHex(n: Int): Char = {
        if (n < 10) (n + 48).asInstanceOf[Char]
        else (n - 10 + 97).asInstanceOf[Char]
    }

    @inline
    def hexToInt(h: Char): Int = {
        if (h > '0' && h <= '9') h - '0'
        else if (h > 'a' && h < 'f') h - 'a' + 10
        else if (h > 'A' && h < 'F') h - 'A' + 10
        else -1
    }

    /**
     * 바이트 배열을 문자열로 표현합니다.
     */
    def toHexString(bytes: Array[Byte]): String = {
        if (bytes == null || bytes.length == 0) EMPTY_STR
        else toHexString(bytes, 0, bytes.length)
    }

    /**
     * 바이트 배열을 문자열로 표현합니다.
     */
    def toHexString(bytes: Array[Byte], from: Int, to: Int): String = {
        val out = new StringBuilder

        for (i <- from until to) {
            val b = bytes(i)
            val s = (b.toInt & 0xff).toHexString
            if (s.length < 2) {
                out append '0'
            }
            out append s
        }
        out.toString()
    }

    /**
     * 16 진수 형태로 표현된 문자열을 실제 16진수 byte array 로 변환합니다.
     * @see `toHexString`
     */
    def fromHexString(str: String): Array[Byte] = {
        val buffer = new Array[Byte]((str.length + 1) / 2)
        (str.grouped(2).toSeq zipWithIndex) foreach {
            case (substr, i) =>
                buffer(i) = java.lang.Integer.parseInt(substr, 16).toByte
        }
        buffer
    }

    def getBytesFromHexString(hexString: String): Array[Byte] = {
        if (isEmpty(hexString))
            return Array.emptyByteArray

        Hex.decodeHex(hexString.toCharArray)
    }

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

    def getUtf8Bytes(str: String): Array[Byte] = {
        if (isEmpty(str)) Arrays.EMPTY_BYTE_ARRAY
        else str.getBytes(Charsets.UTF_8)
    }

    def getUtf8String(bytes: Array[Byte]) = {
        if (Arrays.isEmpty(bytes)) EMPTY_STR
        else new String(bytes, Charsets.UTF_8)
    }

    def getString(bytes: Array[Byte], charsetName: String = "UTF-8"): String = {
        if (Arrays.isEmpty(bytes)) EMPTY_STR
        else new String(bytes, Charset.forName(charsetName))
    }

    def getStringFromBytes(bytes: Array[Byte],
                           format: BinaryStringFormat = BinaryStringFormat.HexDecimal): String = {
        if (format == BinaryStringFormat.HexDecimal)
            getHexString(bytes)
        else
            encodeBase64String(bytes)
    }

    def getBytesFromString(str: String,
                           format: BinaryStringFormat = BinaryStringFormat.HexDecimal): Array[Byte] = {
        if (format == BinaryStringFormat.HexDecimal)
            getBytesFromHexString(str)
        else
            decodeBase64(str)
    }

    @inline
    def deleteCharAny(cs: CharSequence, chars: Char*): CharSequence = {
        if (cs == null || cs.length == 0 || chars == null || chars.length == 0)
            return cs

        val builder = new StringBuilder()

        for (i <- 0 until cs.length()) {
            val c = cs.charAt(i)
            if (!chars.contains(c))
                builder.append(c)
        }
        builder.toString()
    }

    @inline
    def deleteChar(cs: CharSequence, chars: Array[Char]): CharSequence = {
        if (cs == null || cs.length == 0 || chars == null || chars.length == 0)
            return cs

        val builder = new StringBuilder()

        for (i <- 0 until cs.length()) {
            val c = cs.charAt(i)
            if (!chars.contains(c))
                builder.append(c)
        }
        builder.toString()
    }

    @inline
    def deleteChar(cs: CharSequence, dc: Char): CharSequence = {
        if (cs == null || cs.length == 0)
            return cs

        val builder = new StringBuilder()

        for (i <- 0 until cs.length()) {
            val c = cs.charAt(i)
            if (c != dc)
                builder.append(c)
        }
        builder.toString()
    }

    @varargs
    @inline
    def concat(items: Any*): String = {
        val builder = new StringBuilder()
        items.foreach(x => builder.append(asString(x)))
        builder.toString()
    }

    @varargs
    @inline
    def concat(items: Any*)(implicit separator: String = ""): String = {
        if (items == null || items.isEmpty)
            return ""

        val builder = new StringBuilder

        builder.append(asString(items.headOption.getOrElse("")))
        items
        .tail
        .foreach(x => builder.append(separator).append(asString(x)))

        builder.toString()
    }

    @inline
    def subSequence(cs: CharSequence, fromIndex: Int = 0): CharSequence = {
        if (cs == null) null else cs.subSequence(fromIndex, cs.length())
    }

    @inline
    def indexOf(cs: CharSequence, searchChar: Int, fromIndex: Int = 0): Int = {
        cs match {
            case s: String => s.indexOf(searchChar, fromIndex)
            case _ =>
                val sz = cs.length()
                for (i <- fromIndex until sz) {
                    if (cs.charAt(i) == searchChar)
                        return i
                }
                return -1
        }
    }

    @inline
    def lastIndexOf(cs: CharSequence, searchChar: Int, fromIndex: Int = 0): Int = {
        cs match {
            case s: String => s.lastIndexOf(searchChar, fromIndex)
            case _ =>
                var start = fromIndex
                val sz = cs.length()
                if (start >= sz)
                    start = sz - 1
                for (i <- start to 0 by -1) {
                    if (cs.charAt(i) == searchChar)
                        return i
                }
                return -1
        }
    }

    @inline
    def toCharArray(cs: CharSequence): Array[Char] = {
        cs match {
            case s: String => s.toCharArray
            case _ =>
                val sz = cs.length()
                val array = new Array[Char](sz)
                for (i <- 0 until sz) {
                    array(i) = cs.charAt(i)
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


    @inline
    def join(items: java.lang.Iterable[_])(implicit separator: String): String =
        items.mkString(separator)

    @inline
    @varargs
    def join(items: Any*)(implicit separator: String): String = items.mkString(separator)

    @inline
    def quotedStr(str: String): String =
        if (isNull(str)) NULL_STR
        else String.format("\'%s\'", str.replace("\'", "\'\'"))

    @inline
    def quotedStr(str: String, defaultStr: String): String =
        if (isWhitespace(str)) quotedStr(defaultStr)
        else quotedStr(str)

    def reverse(str: String): String = str.reverse

    def replicate(str: String, n: Int): String = str * n

    @varargs
    def split(str: String, separators: String*): Array[String] = {
        split(str, ignoreCase = true, removeEmptyEntries = true, separators: _*)
    }

    @varargs
    def split(str: String, removeEmptyEntries: Boolean, separators: String*): Array[String] = {
        split(str, ignoreCase = true, removeEmptyEntries = removeEmptyEntries, separators: _*)
    }

    @inline
    @varargs
    def split(str: String, ignoreCase: Boolean, removeEmptyEntries: Boolean, separators: String*): Array[String] = {
        if (isEmpty(str))
            return Array[String]()

        val results = ArrayBuffer[String]()
        val seps = ArrayBuffer[Array[Char]]()
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
                    if (removeEmptyEntries) {
                        results += item.trim
                    } else {
                        results += item
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

            if (index > 0) {
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

        if (index > 0) str.substring(0, index - 1)
        else str
    }

    def getBetween(text: String, start: String, end: String): String = {
        if (isEmpty(text))
            return text

        var startIndex = 0
        if (isNotEmpty(start)) {
            val index = text.indexOf(start)
            if (index > -1)
                startIndex = index + start.length
        }

        var endIndex = text.length - 1
        if (isNotEmpty(end)) {
            val index = text.lastIndexOf(end)
            if (index > -1)
                endIndex = index - 1
        }

        if (endIndex > startIndex)
            text.substring(startIndex, endIndex)
        else
            EMPTY_STR
    }

    @inline
    def objectToString(obj: Any): String = {
        asString(obj)
    }

    @varargs
    def listToString(items: Any*): String = {
        if (items == null) NULL_STR
        else join(items.toArray)
    }

    def listToString(items: java.lang.Iterable[_]): String = {
        if (items == null) NULL_STR
        else join(items)
    }

    def listToString(items: Array[_]): String = {
        if (items == null) NULL_STR
        else join(items)
    }

    def mapToString(map: util.Map[_, _], openStr: String = "{", delimeter: String = ",", closeStr: String = "}"): String = {
        if (map == null) NULL_STR
        else openStr + join(mapToEntityList(map))(delimeter) + closeStr
    }

    def mapToEntityList(map: util.Map[_, _]): util.List[String] = {
        val list = new ArrayBuffer[String]()
        map.foreach(entry => list += (entry._1.toString + "=" + entry._2.toString))
        list
    }

    def regexSub(cs: CharSequence, re: Regex)(replace: (Regex.MatchData => String)): String = {
        var offset = 0
        val out = new StringBuilder()

        for (m <- re.findAllIn(cs).matchData) {
            if (m.start > offset) {
                out.append(cs.subSequence(offset, m.start))
            }
            out.append(replace(m))
            offset = m.end
        }

        if (offset < cs.length())
            out.append(cs.subSequence(offset, cs.length() - 1))

        out.toString()
    }

    private val QUOTE_RE = "[\u0000-\u001f\u007f-\uffff\\\\\"]".r

    /**
     * 프린트 불가한 char 를 c 스타일로 quote 해 준다.
     * 예를 들어 linefeed 를 `"\n"` 으로 표시해 준다.
     */
    def quoteC(cs: CharSequence): String = {
        regexSub(cs, QUOTE_RE) {
            m: MatchData =>
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
    private val UNQUOTE_RE = """\\(u[\dA-Fa-f]{4}|x[\dA-Fa-f]{2}|[/rnt\"\\])""".r

    /**
     * Unquote an ASCII string that has been quoted in a style like
     * {@link #quoteC} and convert it into a standard unicode string.
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
