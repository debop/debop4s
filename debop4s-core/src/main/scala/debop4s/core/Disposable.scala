package debop4s.core

import debop4s.core.utils.Time

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Failure, Success}

/**
 * 리소스 관리를 위한 Trait
 * Created by debop on 2014. 4. 5.
 */
trait Disposable[+T] {

  def get: T

  def dispose(deadline: Time): Future[Unit]

  final def dispose(): Future[Unit] = dispose(Time.Inf)
}

object Disposable {
  def const[T](t: T) = new Disposable[T] {
    override def get = t
    override def dispose(deadline: Time) = Future(())
  }
}

trait Managed[+T] {selfT =>

  def foreach(action: T => Unit): Unit = {
    val r = this.make()
    try action(r.get) finally r.dispose()
  }

  def flatMap[U](f: T => Managed[U]): Managed[U] = new Managed[U] {
    def make() = new Disposable[U] {
      val t = selfT.make()
      val u = try {
        f(t.get).make()
      } catch {
        case e: Exception =>
          t.dispose()
          throw e
      }

      def get = u.get

      def dispose(deadline: Time) = Future {
        u.dispose(deadline) onComplete {
          case Success(_) => t.dispose(deadline)
          case Failure(outer) => t.dispose onComplete {
            case Failure(inner) => Future.failed(new DoubleTrouble(outer, inner))
            case Success(_) => Future.failed(outer)
          }
        }
      }
    }

  }

  def map[U](f: T => U): Managed[U] = flatMap { t => Managed.const(f(t)) }
  def make(): Disposable[T]
}

object Managed {
  def singleton[T](t: Disposable[T]) = new Managed[T] {def make() = t }
  def const[T](t: T) = singleton(Disposable.const(t))
}

class DoubleTrouble(cause1: Throwable, cause2: Throwable) extends Exception {
  override def getStackTrace = cause1.getStackTrace
  override def getMessage =
    s"Double failure while disposing composite resource: ${ cause1.getMessage } \n${ cause2.getMessage }"
}
