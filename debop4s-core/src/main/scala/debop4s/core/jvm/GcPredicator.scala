package debop4s.core.jvm

import debop4s.core.Time
import debop4s.core.conversions.time._
import debop4s.core.utils.Timer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
 * A Gc predictor. This predicts a time based on measuring rates,
 * which are reported to the given estimator. Rates are measured
 * every `period`. The estimated GC time is based on interpolating
 * the rate estimated by `estimator`.
 *
 * from Twitter/util
 */
class GcPredicator(pool: Pool, period: Duration, timer: Timer, estimator: Estimator[Double]) {

  private[this] def loop() {
    for (bps <- pool.estimateAllocRate(period, timer)) {
      synchronized { estimator.measure(bps.toDouble) }
      loop()
    }
  }

  loop()

  def nextGcEstimate(): Time = {
    val e = synchronized(estimator.estimate).toLong

    if (e == 0) Time.Inf
    else {
      val PoolState(_, capacity, used) = pool.state()
      val r = ( capacity - used ).inBytes
      Time.now + ( ( 1000 * r ) / e ).milliseconds
    }
  }
}

// Note: it may be preoductive to make use of several inputs
// (average, short-term average, instantaneous rate) and average them
// using something like ARIMA, or using a Kalman filter for the
// output of *that*. This can be done by composing Estimators.
