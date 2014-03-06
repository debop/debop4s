package com.github.debop4s.core.utils

import java.nio.charset.Charset
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory

/**
 * Codec helper object
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:27
 */
object Codecs {

    private lazy val log = LoggerFactory.getLogger(getClass)

    /**
    * 입력 데이터를 base64 방식으로 인코딩합니다.
    */
    def encodeBase64(input: Array[Byte],
                     isChucked: Boolean = false,
                     urlSafe: Boolean = true): Array[Byte] =
        Base64.encodeBase64(input, isChucked, urlSafe)

    /**
    * 입력 데이터를 base64 방식으로 인코딩합니다.
    */
    def encodeBase64String(str: String,
                           cs: Charset = Charsets.UTF_8,
                           isChunked: Boolean = false,
                           urlSafe: Boolean = true): String = {
        val input = Strings.getUtf8Bytes(str)
        val bytes = encodeBase64(input, isChunked, urlSafe)
        Strings.getUtf8String(bytes)
    }

    /**
    * base64 방식으로 인코딩된 데이터를 디코딩합니다.
    */
    def decodeBase64(input: Array[Byte]): Array[Byte] =
        Base64.decodeBase64(input)

    /**
    * base64 방식으로 인코딩된 문자열을 디코딩하여 plain text로 반환합니다.
    */
    def decodeBase64String(str: String, cs: Charset = Charsets.UTF_8): String = {
        val input = Strings.getUtf8Bytes(str)
        val bytes = decodeBase64(input)
        new String(bytes, cs)
    }
}
