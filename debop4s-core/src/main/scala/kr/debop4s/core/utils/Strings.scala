package kr.debop4s.core.utils

import java.nio.charset.Charset
import java.util
import kr.debop4s.core.BinaryStringFormat
import kr.debop4s.core.BinaryStringFormat.BinaryStringFormat
import org.apache.commons.codec.binary.{Base64, Hex, StringUtils}
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.core.tools.String
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:06
 */
object Strings {

    lazy val log = LoggerFactory.getLogger(getClass)

    val MULTI_BYTES_PREFIX = Array(0xEF.toByte, 0xBB.toByte, 0xBF.toByte)
    val TRIMMING_STR = "..."
    val NULL_STR = "<null>"
    val EMPTY_STR = ""
    val COMMA_STR = ","
    val UTF8: Charset = Charsets.UTF_8

    // join 함수의 암묵적 인자로 사용됩니다.
    implicit val separator: String = COMMA_STR

    @inline
    def asString(x: Any): String = if (x == null) NULL_STR else x.toString

    @inline
    def isNull(str: String): Boolean = str == null

    @inline
    def isNotNull(str: String): Boolean = str != null

    @inline
    def isEmpty(str: String): Boolean = isEmpty(str, doTrim = false)

    @inline
    def isEmpty(str: String, doTrim: Boolean = false): Boolean =
        isNull(str) || (if (doTrim) str.trim() else str).length == 0

    @inline
    def isNotEmpty(str: String): Boolean = isNotEmpty(str, doTrim = false)

    @inline
    def isNotEmpty(str: String, doTrim: Boolean): Boolean = !isEmpty(str, doTrim)

    @inline
    def isWhitespace(str: String): Boolean = isEmpty(str, doTrim = true)

    @inline
    def isNotWhitespace(str: String): Boolean = isNotEmpty(str, doTrim = true)

    def isMultiByteString(bytes: Array[Byte]): Boolean = {
        if (bytes == null || bytes.length < MULTI_BYTES_PREFIX.length)
            false
        else
            util.Arrays.equals(MULTI_BYTES_PREFIX,
                util.Arrays.copyOf(bytes, MULTI_BYTES_PREFIX.length)) //.asInstanceOf[Array[Byte]])
    }

    def isMultiByteString(str: String): Boolean = {
        if (isWhitespace(str))
            return false

        val bytes = StringUtils.getBytesUsAscii(str.substring(0, Math.min(2, str.length)))
        isMultiByteString(bytes)
    }

    def contains(str: String, subStr: String): Boolean = isNotEmpty(str) && str.contains(subStr)

    def needEllipsis(str: String, maxLength: Int = 80): Boolean = isNotEmpty(str) && str.length() > maxLength

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
            str
        else
            TRIMMING_STR + str.substring(str.length - maxLength)
    }

    def intToHex(n: Int): Char = {
        if (n < 10)
            (n + 48).asInstanceOf[Char]
        else
            (n - 10 + 97).asInstanceOf[Char]
    }

    def hexToInt(h: Char): Int = {
        if (h > '0' && h <= '9')
            h - '0'
        else if (h > 'a' && h < 'f')
            h - 'a' + 10
        else if (h > 'A' && h < 'F')
            h - 'A' + 10
        else
            -1
    }

    def getBytesFromHexString(hexString: String): Array[Byte] = {
        if (isEmpty(hexString))
            Array()
        else
            Hex.decodeHex(hexString.toCharArray)
    }

    def getHexString(bytes: Array[Byte]): String = Hex.encodeHexString(bytes)

    def encodeBase64(input: Array[Byte]): Array[Byte] = Base64.encodeBase64URLSafe(input)

    def encodeBase64String(input: Array[Byte]): String = StringUtils.newStringUtf8(encodeBase64(input))

    def decodeBase64(base64Data: Array[Byte]): Array[Byte] = Base64.decodeBase64(base64Data)

    def decodeBase64(base64String: String): Array[Byte] = Base64.decodeBase64(base64String)

    def decodeBase64String(base64Data: Array[Byte]): String = StringUtils.newStringUtf8(decodeBase64(base64Data))

    def getBytesUtf8(str: String): Array[Byte] = {
        if (isEmpty(str)) Arrays.EMPTY_BYTE_ARRAY
        else str.getBytes(UTF8)
    }

    def getStringUtf8(bytes: Array[Byte]) = {
        if (Arrays.isEmpty(bytes)) EMPTY_STR
        else new String(bytes, UTF8)
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

    def deleteCharAny(str: String, chars: Char*): String = {
        if (isEmpty(str) || chars == null || chars.length == 0)
            return str

        val builder = new StringBuilder()

        val strArray = str.toCharArray
        for (c <- strArray) {
            if (!chars.contains(c))
                builder.append(c)
        }
        builder.toString()
    }

    def deleteChar(str: String, chars: Array[Char]): String = {
        if (isEmpty(str) || chars == null || chars.length == 0)
            return str

        val builder = new StringBuilder()

        val strArray = str.toCharArray
        for (c <- strArray) {
            if (!chars.contains(c))
                builder.append(c)
        }
        builder.toString()
    }

    def deleteChar(str: String, dc: Char): String = {
        if (isEmpty(str))
            return str

        val builder = new StringBuilder()

        val strArray = str.toCharArray
        for (c <- strArray) {
            if (c != dc)
                builder.append(c)
        }
        builder.toString()
    }


    @varargs
    def concat(items: Any*): String = {
        val builder = new StringBuilder()
        items.foreach(x => builder.append(asString(x)))
        builder.toString()
    }

    @varargs
    def concat(items: Any*)(sep: String): String = {
        val builder = new StringBuilder

        if (items != null && items.size > 0) {
            builder.append(asString(items.head))

            items
                .takeRight(items.size - 1)
                .foreach(x => builder.append(sep).append(asString(x)))
        }
        builder.toString()
    }

    @inline
    def join(items: java.lang.Iterable[_])(implicit separator: String = COMMA_STR): String = items.mkString(separator)

    @inline
    def join(items: Array[_])(implicit separator: String): String = items.mkString(separator)

    def quotedStr(str: String): String =
        if (isNull(str)) NULL_STR else String.format("\'%s\'", str.replace("\'", "\'\'"))

    def quotedStr(str: String, defaultStr: String): String =
        if (isWhitespace(str)) quotedStr(defaultStr) else quotedStr(str)

    def reverse(str: String): String = str.reverse

    def replicate(str: String, n: Int): String = str * n

    @varargs
    def split(str: String, separators: String*): Array[String] = {
        split(str, ignoreCase = false, removeEmptyEntries = false, separators)
    }

    @varargs
    def split(str: String, removeEmptyEntries: Boolean, separators: String*): Array[String] = {
        split(str, ignoreCase = false, removeEmptyEntries = removeEmptyEntries, separators)
    }

    def split(str: String, ignoreCase: Boolean, removeEmptyEntries: Boolean, separators: Seq[String]): Array[String] = {
        if (isEmpty(str))
            return new Array[String](0)

        val result = new ArrayBuffer[String]
        val seps = new ArrayBuffer[Array[Char]]
        for (sep <- separators) {
            if (ignoreCase) seps += sep.toLowerCase.toCharArray
            else seps += sep.toCharArray
        }

        val strArray = str.toCharArray
        val strArray2 = if (ignoreCase) str.toLowerCase.toCharArray else str.toCharArray

        var startIndex = 0
        var prevIndex = 0
        while (startIndex < strArray.length) {
            for (sep <- seps) {
                if (util.Arrays.equals(sep, util.Arrays.copyOfRange(strArray2, startIndex, startIndex + sep.length))) {
                    val item = new String(util.Arrays.copyOfRange(strArray, prevIndex, startIndex))
                    if (!(removeEmptyEntries && isWhitespace(item)))
                        result += item
                    prevIndex = startIndex + sep.length
                    startIndex = startIndex + sep.length
                }
            }
            startIndex += 1
        }
        if (prevIndex < strArray.length - 1)
            result += new String(util.Arrays.copyOfRange(strArray, prevIndex, strArray.length))

        result.toArray
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
        if (isEmpty(str))
            return str

        val index = str.indexOf('\n')
        if (index > 0)
            str.substring(0, index - 1)
        else
            str
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

    def objectToString(obj: Any): String = {
        if (obj == null)
            return NULL_STR

        val helper = ToStringHelper(obj)

        for (field <- obj.getClass.getFields)
            helper.add(field.getName, field.get(obj))

        helper.toString
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
}
