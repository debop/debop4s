package kr.debop4s.core.utils

import java.nio.charset.Charset

/**
 * kr.debop4s.core.tools.Charsets
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오전 9:50
 */
object Charsets {
    /**
     * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the
     * Unicode character set
     */
    final val US_ASCII: Charset = Charset.forName("US-ASCII")
    /**
     * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
     */
    final val ISO_8859_1: Charset = Charset.forName("ISO-8859-1")
    /**
     * Eight-bit UCS Transformation Format
     */
    final val UTF_8: Charset = Charset.forName("UTF-8")
    /**
     * Sixteen-bit UCS Transformation Format, big-endian byte order
     */
    final val UTF_16BE: Charset = Charset.forName("UTF-16BE")
    /**
     * Sixteen-bit UCS Transformation Format, little-endian byte order
     */
    final val UTF_16LE: Charset = Charset.forName("UTF-16LE")
    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark
     */
    final val UTF_16: Charset = Charset.forName("UTF-16")

}
