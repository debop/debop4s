package debop4s.core

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
    def get = t

    def dispose(deadline: Time) = Future(())
  }
}
