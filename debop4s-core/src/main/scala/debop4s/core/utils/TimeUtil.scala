package debop4s.core.utils

import scala.concurrent.duration.Duration

/**
 * TimeUtil
 * @author Sunghyouk Bae
 */
object TimeUtil {

  def toDuration(ns: Long) = ns match {
    case Long.MaxValue => Duration.Inf
    case Long.MinValue => Duration.MinusInf
    case _ =>
      try {
        Duration.fromNanos(ns)
      } catch {
        case _: Throwable =>
          Duration.Undefined
      }
  }

  def abs(d: Duration) = d match {
    case Duration.Inf => Duration.Inf
    case Duration.MinusInf => Duration.Inf
    case Duration.Undefined => Duration.Undefined
    case _ => if (d < Duration.fromNanos(0)) -d else d
  }

  def inNanos(d: Duration): Option[Long] = {
    if (d == null) None
    else if (d.isFinite()) Some(d.toNanos)
    else None
  }
}
