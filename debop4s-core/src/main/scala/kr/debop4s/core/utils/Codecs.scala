package kr.debop4s.core.utils

import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory

/**
 * Codec helper object
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:27
 */
object Codecs {

    lazy val log = LoggerFactory.getLogger(getClass)

    def encodeBase64(input: Array[Byte], isChucked: Boolean = false, urlSafe: Boolean = true): Array[Byte] =
        Base64.encodeBase64(input, isChucked, urlSafe)

    def encodeBase64String(str: String, isChunked: Boolean = false, urlSafe: Boolean = true): String = {
        val input = Strings.getBytesUtf8(str)
        val bytes = encodeBase64(input, isChunked, urlSafe)
        Strings.getStringUtf8(bytes)
    }

    def decodeBase64(input: Array[Byte]): Array[Byte] =
        Base64.decodeBase64(input)

    def decodeBase64String(str: String): String = {
        val input = Strings.getBytesUtf8(str)
        val bytes = decodeBase64(input)
        Strings.getStringUtf8(bytes)
    }
}
