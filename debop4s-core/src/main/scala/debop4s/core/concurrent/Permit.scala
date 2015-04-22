package debop4s.core.concurrent

/**
 * Permit
 * Created by debop on 2014. 4. 6.
 */
trait Permit {
  def release(): Unit
}
