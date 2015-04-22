package debop4s.core.io

import java.io.InputStream

/**
 * 버퍼링이 되는 `InputStream` 입니다.
 */
class BuffInputStream(val buff: Buff) extends InputStream {

  // `buff` 에서 사용되지 않은 부분을 표현
  private[this] var rest: Buff = buff

  // `rest` 의 저장 버전
  private[this] var mrk: Buff = buff

  override def available(): Int = synchronized { rest.length }

  override def close(): Unit = {}

  override def mark(readlimit: Int): Unit = synchronized { mrk = rest }

  override def markSupported(): Boolean = true

  def read(): Int = synchronized {
    if (rest.length <= 0)
      return -1

    val b = new Array[Byte](1)
    rest.slice(0, 1).write(b, 0)
    rest = rest.slice(1, rest.length)
    b(0) & 0xFF
  }

  override def read(b: Array[Byte], off: Int, len: Int): Int = synchronized {
    if (rest.length <= 0)
      return -1

    if (len == 0)
      return 0

    val n = len min rest.length
    rest.slice(0, n).write(b, off)
    rest = rest.slice(n, rest.length)
    n
  }

  override def reset(): Unit = synchronized { rest = mrk }

  override def skip(n: Long): Long = synchronized {
    if (n <= 0) return 0

    val skipped: Long = n min rest.length
    rest = rest.slice(skipped.toInt, rest.length)
    skipped
  }
}


