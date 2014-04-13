package debop4s.core.jvm

/**
 * An estimator for values of type T.
 * Created by debop on 2014. 4. 14.
 */
trait Estimator[T] {
  /** A scalar measurement `m` was taken */
  def measure(m: T)

  /** Estimate the current value */
  def estimate: T
}
