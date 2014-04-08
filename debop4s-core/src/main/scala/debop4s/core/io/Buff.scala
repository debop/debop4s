package debop4s.core.io

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util

/**
 * 고정된 크기의 바이트 버퍼를 표현합니다.
 * @author Sunghyouk Bae
 */
trait Buff {outer =>

    /**
     * `Buffer` 정보를 `output` 바이트 배열에 씁니다.
     * @param output 버퍼 정보를 쓸 대상 바이트 배열
     * @param offset 대상 바이트 배열의 오프셋
     */
    def write(output: Array[Byte], offset: Int)

    def length: Int

    /**
     * 현 `Buffer` 의 지정된 범위를 ( start <= x < end ) 잘라 복사합니다.
     * @param start 시작 인덱스
     * @param end 끝 인덱스 (포함되지 않는다)
     * @return
     */
    def slice(start: Int, end: Int): Buff

    /**
     * 현 `Buffer` 와 `right` 버퍼를 합친 버퍼를 빌드합니다.
     * @param right 합칠 버퍼
     * @return
     */
    def concat(right: Buff): Buff = {
        if (right == Buff.Empty) outer
        else new Buff {
            private[this] val left = outer

            def slice(start: Int, end: Int): Buff = {
                left.slice(start min left.length, end min left.length) concat
                right.slice((start - left.length) max 0, (end - left.length) max 0)
            }

            def write(buff: Array[Byte], offset: Int) {
                require(length <= buff.length - offset)
                left.write(buff, offset)
                right.write(buff, offset + left.length)
            }
            def length = left.length + right.length
        }
    }

    def toArray: Array[Byte] = {
        val r = new Array[Byte](this.length)
        write(r, 0)
        r
    }

    override def equals(other: Any): Boolean = other match {
        case other: Buff => Buff.equals(this, other)
        case _ => false
    }
    override def toString: String = {
        toArray.mkString(",")
    }
}


object Buff {

    private class Noop extends Buff {
        def write(output: Array[Byte], offset: Int) = ()
        def length = 0
        def slice(start: Int, end: Int): Buff = {
            require(start >= 0 && end >= 0, "Index out of bounds")
            this
        }
    }

    /**
     * `end of file` 를 표현하는 buffer 입니다.
     */
    val Eof: Buff = new Noop

    /**
     * 빈 버퍼를 나타냅니다.
     */
    val Empty: Buff = new Noop

    class ByteArray(val bytes: Array[Byte], val start: Int, val end: Int) extends Buff {
        override def write(output: Array[Byte], offset: Int): Unit = {
            System.arraycopy(bytes, start, output, offset, length)
        }
        override def length: Int = end - start
        override def slice(start: Int, end: Int): Buff = {
            require(start >= 0 && end >= 0, "Index out of bounds")
            if (end <= start || start > length) Buff.Empty
            else ByteArray(bytes, this.start + start, (this.start + end) min this.end)
        }
        override def toString: String = s"ByteArray(${ super.toString })"
        override def equals(other: Any): Boolean = other match {
            case other: ByteArray
                if other.start == 0 && other.end == other.bytes.length && start == 0 && end == bytes.length =>
                util.Arrays.equals(bytes, other.bytes)
            case x => super.equals(x)
        }
    }

    object ByteArray {

        def apply(bytes: Array[Byte], start: Int, end: Int): Buff =
            if (start >= end) Buff.Empty else new ByteArray(bytes, start, end)

        def apply(bytes: Array[Byte]): Buff = {
            if (bytes == null || bytes.length == 0) Buff.Empty
            else ByteArray(bytes, 0, bytes.length)
        }

        def apply(bytes: Byte*): Buff = apply(Array[Byte](bytes: _*))

        def unapply(buffer: Buff): Option[(Array[Byte], Int, Int)] = buffer match {
            case ba: ByteArray => Some(ba.bytes, ba.start, ba.end)
            case _ => None
        }
    }

    /**
     * `Buffer` 를 java nio `ByteBuffer` 로 변환합니다.
     * @param buffer
     * @return
     */
    def toByteBuffer(buffer: Buff): ByteBuffer = buffer match {
        case ByteArray(bytes, s, e) => ByteBuffer.wrap(bytes, s, e - s)
        case buff =>
            val bytes = new Array[Byte](buff.length)
            buff.write(bytes, 0)
            ByteBuffer.wrap(bytes)
    }

    /**
     * 두 개의 Buffer의 값이 같은지 비교합니다.
     * @param x
     * @param y
     * @return
     */
    def equals(x: Buff, y: Buff): Boolean = {
        if (x.length != y.length)
            return false

        val a, b = new Array[Byte](x.length)
        x.write(a, 0)
        y.write(b, 0)
        util.Arrays.equals(a, b)
    }

    /**
     * `Buffer` 내용을 16진수 문자열로 표현합니다.
     * @param buffer
     * @return
     */
    def toHexString(buffer: Buff): String = {
        val bytes = new Array[Byte](buffer.length)
        buffer.write(bytes, 0)
        val digits = for (b <- bytes) yield "%02x".format(b)
        digits.mkString
    }

    object Utf8 {
        private val utf8 = Charset.forName("UTF-8")

        def apply(s: String): Buff = ByteArray(s.getBytes(utf8))
        def unapply(buff: Buff): Option[String] = buff match {
            case ba: ByteArray =>
                val s = new String(ba.bytes, ba.start, ba.end - ba.start, utf8)
                Some(s)
            case buf =>
                val bytes = new Array[Byte](buff.length)
                buff.write(bytes, 0)
                Some(new String(bytes, utf8))
        }
    }
}
