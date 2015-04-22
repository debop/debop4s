package debop4s.core.io

import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock

import debop4s.core._
import debop4s.core.utils.Time

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


object InputStreamReader {
  val DefaultMaxBufferSize: Int = 4096

  def apply(inputStream: InputStream, maxBufferSize: Int = DefaultMaxBufferSize): InputStreamReader =
    new InputStreamReader(inputStream, maxBufferSize)
}

/**
 * InputStreamReader
 * @author Sunghyouk Bae
 */
class InputStreamReader(inputStream: InputStream,
                        maxBufferSize: Int)
  extends Reader with Closable with CloseAwaitably {

  @volatile private[this] var discarded = false
  private[this] val lock: ReentrantReadWriteLock = new ReentrantReadWriteLock()

  def read(n: Int): Future[Buff] = {
    if (discarded)
      return Future.failed(new Reader.ReaderDiscarded())

    if (n == 0)
      return Future.successful(Buff.Empty)

    Future {
      lock.readLock().lock()
      try {
        if (discarded)
          throw new Reader.ReaderDiscarded()

        val size = n min maxBufferSize
        val buffer = new Array[Byte](size)
        val c = inputStream.read(buffer, 0, size)

        if (c == -1) Buff.Eof
        else if (c == 0) Buff.Empty
        else Buff.ByteArray(buffer, 0, c)

      } catch {
        case exc: InterruptedException =>
          discarded = true
          throw exc
      } finally {
        lock.readLock().unlock()
      }
    }
  }

  /**
   * Discard this reader
   */
  def discard(): Unit = {
    discarded = true
  }

  def close(deadline: Time): Future[Unit] = closeAwaitably {
    discard()
    Future { inputStream.close() }
  }
}
