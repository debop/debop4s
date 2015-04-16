package debop4s.core.io

import java.io.{File, FileInputStream, InputStream}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.Try

/**
 * Reader
 */
trait Reader {

  /**
   * 비동기 방식으로 읽기를 수행합니다. `n` 개의 바이트를 비동기 방식으로 읽습니다.
   * 끝까지 읽은 경우 [[debop4s.core.io.Buff.Eof]] 를 반환합니다.
   */
  def read(n: Int): Future[Buff]

  /**
   * 이 reader 를 버립니다. 더 이상 읽기 작업을 하지 않도록 합니다.
   */
  def discard()

}

/**
 * 바이트 스트림을 쓰기 위한 Writer 를 표현합니다.
 */
trait Writer {

  /**
   * 비동기 방식으로 [[debop4s.core.io.Buff]] 정보를 읽어 씁니다.
   */
  def write(buf: Buff): Future[Unit]

  /**
   * 바이트 스트림에 예외가 발생했을 때 호출합니다. 이 호출 이후로는 쓰기 작업을 할 수 없습니다.
   */
  def fail(cause: Throwable)
}

object Reader {

  def readAll(r: Reader): Future[Buff] = {
    def loop(buf: Buff): Future[Buff] = {
      r.read(Int.MaxValue) flatMap {
        case Buff.Eof => Future.successful(buf)
        case next => loop(buf concat next)
      }
    }
    loop(Buff.Empty)
  }

  class ReaderDiscarded extends Exception("This writer's reader has been discarded")

  private sealed trait State
  private object Idle extends State
  private case class Reading(n: Int, p: Promise[Buff]) extends State
  private case class Writing(buf: Buff, p: Promise[Unit]) extends State
  private case class Failing(exc: Throwable) extends State

  /**
   * Create a reader which is also a writer
   */
  def writable(): Reader with Writer = new Reader with Writer {
    private[this] var state: State = Idle

    def write(buf: Buff): Future[Unit] = synchronized {
      state match {
        case Failing(exc) => Future.failed(exc)

        case Idle =>
          val p = Promise[Unit]()
          state = Writing(buf, p)
          p.future

        case Reading(n, p) if n < buf.length =>
          val nextp = Promise[Unit]()
          state = Writing(buf.slice(n, buf.length), nextp)
          p.success(buf.slice(0, n))
          nextp.future

        case Reading(n, p) =>
          state = Idle
          p.success(buf)
          Future {}

        case Writing(_, _) =>
          Future.failed(new IllegalStateException("write() while Writing"))

      }
    }

    def read(n: Int): Future[Buff] = synchronized {
      state match {
        case Failing(exc) => Future.failed(exc)

        case Idle =>
          val p = Promise[Buff]()
          state = Reading(n, p)
          p.future

        case Writing(buf, p) if buf.length <= n =>
          state = Idle
          p.complete(Try {})
          Future { buf }

        case Writing(buf, p) =>
          state = Writing(buf.slice(n, buf.length), p)
          Future { buf.slice(0, n) }

        case Reading(_, _) =>
          Future.failed(new IllegalStateException("read() while Reading"))
      }
    }

    def discard() = fail(new ReaderDiscarded)

    def fail(cause: Throwable): Unit = synchronized {
      state match {
        case Idle | Failing(_) =>
        case Reading(_, p) =>
          p.failure(cause)
        case Writing(_, p) =>
          p.failure(cause)
      }
      state = Failing(cause)
    }
  }

  def fromFile(f: File): Reader = fromStream(new FileInputStream(f))

  def fromStream(s: InputStream): Reader = InputStreamReader(s)
}
