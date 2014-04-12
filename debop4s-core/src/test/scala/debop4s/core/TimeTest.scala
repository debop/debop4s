package debop4s.core

import debop4s.core.conversions.time._
import debop4s.core.io.Serializers
import debop4s.core.utils.TimeUtil
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

trait TimeLikeTest[T <: TimeLike[T]] extends AbstractCoreTest {
  val ops: TimeLikeOps[T]

  import ops._

  val easyVs = Seq(Zero, Inf, MinusInf, Undefined, fromNanoseconds(1), fromNanoseconds(-1))
  val vs = easyVs ++ Seq(
    fromNanoseconds(Long.MaxValue - 1),
    fromNanoseconds(Long.MinValue + 1)
  )

  test("Inf, MinusInf, Undefined, Nanoseconds(_), Finite(_)") {
    Inf compare Undefined should be < 0
    MinusInf compare Inf should be < 0
    Undefined compare Undefined shouldEqual 0
    Inf compare Inf shouldEqual 0
    MinusInf compare MinusInf shouldEqual 0

    Inf + Duration.Inf shouldEqual Inf
    MinusInf - Duration.MinusInf shouldEqual Undefined
    Inf - Duration.Inf shouldEqual Undefined
    MinusInf + Duration.MinusInf shouldEqual MinusInf
  }

  test("complementary diff") {
    // Note that this doesn't always hold because of two's complement arithmetic.
    for (a <- easyVs; b <- easyVs) {
      a diff b shouldEqual (-(b diff a))
    }
  }

  test("compelementary compare") {
    for (a <- vs; b <- vs) {
      val x = a compare b
      val y = b compare a
      (x == 0 && y == 0) || ((x < 0) != (y < 0)) shouldEqual true
    }
  }

  test("commutative max") {
    for (a <- vs; b <- vs) {
      a max b shouldEqual (b max a)
    }
  }
  test("commutative min") {
    for (a <- vs; b <- vs) {
      a min b shouldEqual (b min a)
    }
  }
  test("handle underflows") {
    fromNanoseconds(Long.MinValue) - 1.nanosecond shouldEqual MinusInf
    fromMicroseconds(Long.MinValue) - 1.nanosecond shouldEqual MinusInf
  }
  test("handle overflows") {
    fromNanoseconds(Long.MaxValue) + 1.nanosecond shouldEqual Inf
    fromMicroseconds(Long.MaxValue) + 1.nanosecond shouldEqual Inf
  }

  test("Nanoseconds extracts only finite values, in nanoseconds") {
    for (t <- Seq(Inf, MinusInf, Undefined)) {
      t match {
        case Finite(_) => fail("finite")
        case _ =>
      }
    }

    for (ns <- Seq[Long](Long.MinValue, -1, 0, 1, Long.MaxValue); t = fromNanoseconds(ns)) {
      t match {
        case Finite(`t`) =>
        case _ => fail("not Finite")
      }
    }
  }

  test("roundtrip through serialization") {
    for (v <- vs) {
      Serializers.copyObject(v) shouldEqual v
    }
  }

  test("Inf - be impermeable to finite arithmetic") {
    Inf - 0L.seconds shouldEqual Inf
    Inf - 100L.seconds shouldEqual Inf
    Inf - Duration.fromNanos(Long.MaxValue) shouldEqual Inf
  }

  test("Inf - become undefined when subtracted from itself, or added to bottom") {
    Inf - Duration.Inf shouldEqual Undefined
    Inf + Duration.MinusInf shouldEqual Undefined
  }

  test("Inf - not be equal to the maximum value") {
    fromNanoseconds(Long.MaxValue) should not be Inf
  }

  test("Inf - always be max") {
    Inf max fromSeconds(1) shouldEqual Inf
    Inf max fromNanoseconds(Long.MaxValue) shouldEqual Inf
    Inf max MinusInf shouldEqual Inf
  }

  test("Inf - greater than everything else") {
    fromSeconds(0) should be < Inf
    fromNanoseconds(Long.MaxValue) should be < Inf
  }

  test("Inf - equal to itself") {
    Inf shouldEqual Inf
  }

  test("Inf - more or less equals only to itself") {
    Inf.moreOrLessEquals(Inf, Duration.Inf) shouldEqual true
    Inf.moreOrLessEquals(Inf, Duration.Zero) shouldEqual true

    Inf.moreOrLessEquals(MinusInf, Duration.Inf) shouldEqual true
    Inf.moreOrLessEquals(MinusInf, Duration.Zero) shouldEqual false

    Inf.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldEqual true
    Inf.moreOrLessEquals(fromSeconds(0), Duration.MinusInf) shouldEqual false
  }

  test("Inf - Undefined diff to Top") {
    Inf diff Inf shouldEqual Duration.Undefined
  }

  test("MinusInf - be impermeable to finite arithmetic") {
    MinusInf - 0L.seconds shouldEqual MinusInf
    MinusInf - 100L.seconds shouldEqual MinusInf
    MinusInf - Duration.fromNanos(Long.MaxValue) shouldEqual MinusInf
  }

  test("MinusInf - become undefined when added with Top or subtracted by bottom") {
    MinusInf + Duration.Inf shouldEqual Undefined
    MinusInf - Duration.MinusInf shouldEqual Undefined
  }

  test("MinusInf - always be min") {
    MinusInf min Inf shouldEqual MinusInf
    MinusInf min fromNanoseconds(0) shouldEqual MinusInf
  }

  test("MinusInf - less than everything else") {
    MinusInf should be < fromSeconds(0)
    MinusInf should be < fromNanoseconds(Long.MaxValue)
    MinusInf should be < fromNanoseconds(Long.MinValue)
  }

  test("MinusInf - less than Inf") {
    MinusInf should be < Inf
  }
  test("MinusInf - equal to itself") {
    MinusInf should be eq MinusInf
  }

  test("MinusInf - more or less equals only to itself") {
    MinusInf.moreOrLessEquals(MinusInf, Duration.Inf) shouldEqual true
    MinusInf.moreOrLessEquals(MinusInf, Duration.Zero) shouldEqual true

    MinusInf.moreOrLessEquals(Inf, Duration.MinusInf) shouldEqual false
    MinusInf.moreOrLessEquals(Inf, Duration.Zero) shouldEqual false

    MinusInf.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldEqual true
    MinusInf.moreOrLessEquals(fromSeconds(0), Duration.MinusInf) shouldEqual false
  }

  test("MinusInf - Undefined diff to Bottom") {
    MinusInf diff MinusInf shouldEqual Duration.Undefined
  }

  test("Undefined - be impermeable to any arithmetic") {
    Undefined + 0L.seconds shouldEqual Undefined
    Undefined + 100L.seconds shouldEqual Undefined
    Undefined + Duration.fromNanos(Long.MaxValue) shouldEqual Undefined
  }

  test("Undefined - become undefined when added with Top or subtracted by bottom") {
    Undefined + Duration.Inf shouldEqual Undefined
    Undefined + Duration.MinusInf shouldEqual Undefined
    Undefined - Duration.Undefined shouldEqual Undefined
  }

  test("Undefined - always be max") {
    Undefined max Inf shouldEqual Undefined
    Undefined max MinusInf shouldEqual Undefined
    Undefined max fromNanoseconds(0L) shouldEqual Undefined
  }

  test("Undefined - greater than everything else") {
    fromSeconds(0) should be < Undefined
    Inf should be < Undefined
    fromNanoseconds(Long.MaxValue) should be < Undefined
  }

  test("Undefined - equal to itself") {
    Undefined shouldEqual Undefined
  }

  test("Undefined - not more or less equal to anything") {
    Undefined.moreOrLessEquals(Undefined, Duration.Inf) shouldEqual false
    Undefined.moreOrLessEquals(Undefined, Duration.Zero) shouldEqual false

    Undefined.moreOrLessEquals(Inf, Duration.Undefined) shouldEqual true
    Undefined.moreOrLessEquals(Inf, Duration.Zero) shouldEqual false

    Undefined.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldEqual false
    Undefined.moreOrLessEquals(fromSeconds(0), Duration.Undefined) shouldEqual true
  }

  test("Undefined - on diff") {
    Undefined diff Inf shouldEqual Duration.Undefined
    Undefined diff MinusInf shouldEqual Duration.Undefined
    Undefined diff fromNanoseconds(123) shouldEqual Duration.Undefined
  }

  test("reflect their underlying value") {
    val nss = Seq(
      2592000000000000000L, // 30000.days
      1040403005001003L, // 12.days+1.hour+3.seconds+5.milliseconds+1.microsecond+3.nanoseconds
      123000000000L, // 123.seconds
      1L
    )
    for (ns <- nss) {
      val t = fromNanoseconds(ns)
      t.inNanoseconds shouldEqual ns
      t.inMicroseconds shouldEqual (ns / 1000L)
      t.inMilliseconds shouldEqual (ns / 1000L / 1000L)
      t.inLongSeconds shouldEqual (ns / 1000L / 1000L / 1000L)
      t.inMinutes shouldEqual (ns / 1000L / 1000L / 1000L / 60L)
      t.inHours shouldEqual (ns / 1000L / 1000L / 1000L / 60L / 60L)
      t.inDays shouldEqual (ns / 1000L / 1000L / 1000L / 60L / 60L / 24L)
    }
  }

  test("inSeconds equal inLongSeconds") {
    val nss = Seq(
      315370851000000000L, // 3650.days+3.hours+51.seconds
      1040403005001003L, // 12.days+1.hour+3.seconds+5.milliseconds+1.microsecond+3.nanoseconds
      1L
    )
    for (ns <- nss) {
      val t = fromNanoseconds(ns)
      t.inLongSeconds shouldEqual t.inSeconds
    }
  }
  test("inSeconds clamp value to Int.MinValue or MaxValue when out of range") {
    val longNs = 2160000000000000000L // 25000.days
    fromNanoseconds(longNs).inSeconds shouldEqual Int.MaxValue
    fromNanoseconds(-longNs).inSeconds shouldEqual Int.MinValue
  }

  test("floor round down") {
    fromSeconds(60).floor(1L.minute) shouldEqual fromSeconds(60)
    fromSeconds(100).floor(1L.minute) shouldEqual fromSeconds(60)
    fromSeconds(119).floor(1L.minute) shouldEqual fromSeconds(60)
    fromSeconds(120).floor(1L.minute) shouldEqual fromSeconds(120)
  }

  test("floor maintain Inf and MinusInf") {
    Inf.floor(1L.hour) shouldEqual Inf
    MinusInf.floor(1L.hour) shouldEqual MinusInf
  }

  test("floor divide by zero") {
    Zero.floor(Duration.Zero) shouldEqual Undefined
    fromSeconds(1).floor(Duration.Zero) shouldEqual Inf
    fromSeconds(-1).floor(Duration.Zero) shouldEqual MinusInf
  }

  test("floor deal with undefineds") {
    MinusInf floor 1L.seconds shouldEqual MinusInf
    Undefined floor 0L.seconds shouldEqual Undefined
    Undefined floor Duration.Inf shouldEqual Undefined
    Undefined floor Duration.MinusInf shouldEqual Undefined
    Undefined floor Duration.Undefined shouldEqual Undefined
  }

  test("floor itself") {
    for (s <- Seq[Long](Long.MinValue, -1, 1, Long.MaxValue)) {
      val t = fromNanoseconds(s)
      t floor TimeUtil.toDuration(t.inNanoseconds) shouldEqual t
    }
  }

  test("from never over/under flow nanos") {
    for (v <- Seq(Long.MinValue, Long.MaxValue)) {
      fromNanoseconds(v) match {
        case Nanoseconds(ns) => assert(ns == v)
      }
    }
  }

  test("from overflow millis") {
    val millis = TimeUnit.NANOSECONDS.toMillis(Long.MaxValue)
    fromMilliseconds(millis) match {
      case Nanoseconds(ns) => assert(ns == millis * 1e6)
    }
    fromMilliseconds(millis + 1) shouldEqual Inf
  }

  test("from - underflow millis") {
    val millis = TimeUnit.NANOSECONDS.toMillis(Long.MinValue)
    fromMilliseconds(millis) match {
      case Nanoseconds(ns) => assert(ns == millis * 1e6)
    }
    fromMilliseconds(millis - 1) shouldEqual MinusInf
  }
}

class TimeTest extends {val ops = Time } with TimeLikeTest[Time] {

  test("work in collections") {
    val t0 = Time.fromSeconds(100)
    val t1 = Time.fromSeconds(100)

    assert(t0 === t1)
    assert(t0.hashCode == t1.hashCode)
    val pairs = List((t0, "foo"), (t1, "bar"))
    pairs.groupBy {
      case (time: Time, value: String) => time
    } shouldEqual Map(t0 -> pairs)
  }

  test("now should be now") {
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("withTimeAt") {
    val t0 = new Time(123456789L)
    Time.withTimeAt(t0) { _ =>
      Time.now shouldEqual t0
      Thread.sleep(50)
      Time.now shouldEqual t0
    }
    println(s"Time.now.inMillis=${ Time.now }, currentTimeMillis=${ System.currentTimeMillis() }")
    (Time.now.inMillis - System.currentTimeMillis).abs should be < 20L
  }

  test("withTimeAt nested") {
    val t0 = new Time(123456789L)
    val t1 = t0 + 10L.minutes
    Time.withTimeAt(t0) { _ =>
      Time.now shouldEqual t0
      Time.withTimeAt(t1) { _ =>
        Time.now shouldEqual t1
      }
      Time.now shouldEqual t0
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("withTimeAt threaded") {
    val t0 = new Time(314159L)
    val t1 = new Time(314160L)
    Time.withTimeAt(t0) { tc =>
      Time.now shouldEqual t0
      Thread.sleep(50)
      Time.now shouldEqual t0
      tc.advance(Duration.fromNanos(1))
      Time.now shouldEqual t1
      tc.set(t0)
      Time.now shouldEqual t0

      @volatile var threadTime: Option[Time] = None
      val thread = new Thread {
        override def run() {
          threadTime = Some(Time.now)
        }
      }
      thread.start()
      thread.join()
      threadTime.get shouldNot be(t0)
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("withTimeFunction") {
    val t0 = Time.now
    var t = t0
    Time.withTimeFunction(t) { _ =>
      Time.now shouldEqual t0
      Thread.sleep(50)
      Time.now shouldEqual t0
      val delta = 100.milliseconds
      t += delta
      Time.now shouldEqual (t0 + delta)
    }
  }

  test("withCurrentTimeFrozen") {
    val t0 = new Time(123456789L)
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now
      Thread.sleep(50)
      Time.now shouldEqual t0
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("advance") {
    val t0 = new Time(123456789L)
    val delta = 5L.seconds

    Time.withTimeAt(t0) { tc =>
      Time.now shouldEqual t0
      tc.advance(delta)
      Time.now shouldEqual (t0 + delta)
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("compare") {
    10L.seconds.afterEpoch should be < 11L.seconds.afterEpoch
    10L.seconds.afterEpoch shouldEqual 10L.seconds.afterEpoch
    11L.seconds.afterEpoch should be > 10L.seconds.afterEpoch
    Time.fromMilliseconds(Long.MaxValue) should be > Time.now
  }

  test("+ delta") {
    10L.seconds.afterEpoch + 5L.seconds shouldEqual 15L.seconds.afterEpoch
  }

  test("- delta") {
    10L.seconds.afterEpoch - 5L.seconds shouldEqual 5L.seconds.afterEpoch
  }

  test("- time") {
    10L.seconds.afterEpoch - 5L.seconds.afterEpoch shouldEqual 5L.seconds
  }

  test("max") {
    10L.seconds.afterEpoch max 5L.seconds.afterEpoch shouldEqual 10L.seconds.afterEpoch
    5L.seconds.afterEpoch max 10L.seconds.afterEpoch shouldEqual 10L.seconds.afterEpoch
  }

  test("min") {
    10L.seconds.afterEpoch min 5L.seconds.afterEpoch shouldEqual 5L.seconds.afterEpoch
    5L.seconds.afterEpoch min 10L.seconds.afterEpoch shouldEqual 5L.seconds.afterEpoch
  }

  test("moreOrLessEquals") {
    val now = Time.now
    now.moreOrLessEquals(now + 1L.second, 1L.second) shouldEqual true
    now.moreOrLessEquals(now - 1L.second, 1L.second) shouldEqual true
    now.moreOrLessEquals(now + 2L.second, 1L.second) shouldEqual false
    now.moreOrLessEquals(now - 2L.second, 1L.second) shouldEqual false
  }

  test("floor - like trim") {
    val format = new TimeFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val t0 = format.parse("2010-12-24 11:04:07.567")

    t0.floor(1L.millisecond) shouldEqual t0
    t0.floor(10L.milliseconds) shouldEqual format.parse("2010-12-24 11:04:07.560")
    t0.floor(1L.seconds) shouldEqual format.parse("2010-12-24 11:04:07.000")
    t0.floor(5L.seconds) shouldEqual format.parse("2010-12-24 11:04:05.000")
    t0.floor(1L.minutes) shouldEqual format.parse("2010-12-24 11:04:00.000")
    t0.floor(1L.hour) shouldEqual format.parse("2010-12-24 11:00:00.000")
  }

  test("since") {
    val t0 = Time.now
    val t1 = t0 + 10L.seconds
    t1.since(t0) shouldEqual 10L.seconds
    t0.since(t1) shouldEqual (-10L).seconds
  }

  test("sinceEpoch") {
    val t0 = Time.epoch + 100L.hours
    t0.sinceEpoch shouldEqual 100L.hours
  }

  test("sinceNow") {
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now + 100L.hours
      t0.sinceNow shouldEqual 100L.hours
    }
  }

  test("fromMicroseconds") {
    Time.fromMicroseconds(0).inNanoseconds shouldEqual 0L
    Time.fromMicroseconds(-1).inNanoseconds shouldEqual (-1L * 1000L)

    Time.fromMicroseconds(Long.MaxValue).inNanoseconds shouldEqual Long.MaxValue
    Time.fromMicroseconds(Long.MaxValue - 1) shouldEqual Time.Inf

    Time.fromMicroseconds(Long.MinValue) shouldEqual Time.MinusInf
    Time.fromMicroseconds(Long.MinValue + 1) shouldEqual Time.MinusInf

    val currentTimeMicro = System.currentTimeMillis() * 1000
    Time.fromMicroseconds(currentTimeMicro).inNanoseconds shouldEqual currentTimeMicro.microseconds.toNanos
  }

  test("fromMillis") {
    Time.fromMilliseconds(0).inNanoseconds shouldEqual 0L
    Time.fromMilliseconds(-1).inNanoseconds shouldEqual (-1L * 1000000L)

    Time.fromMilliseconds(Long.MaxValue).inNanoseconds shouldEqual Long.MaxValue
    Time.fromMilliseconds(Long.MaxValue - 1) shouldEqual Time.Inf

    Time.fromMilliseconds(Long.MinValue) shouldEqual Time.MinusInf
    Time.fromMilliseconds(Long.MinValue + 1) shouldEqual Time.MinusInf

    val currentTimeMs = System.currentTimeMillis()
    Time.fromMilliseconds(currentTimeMs).inNanoseconds shouldEqual currentTimeMs * 1000000L
  }

  test("util") {
    val t0 = Time.now
    val t1 = t0 + 10L.seconds
    t0.until(t1) shouldEqual 10L.seconds
    t1.until(t0) shouldEqual (-10L).seconds
  }

  test("untilEpoch") {
    val t0 = Time.epoch - 100L.hours
    t0.untilEpoch shouldEqual 100L.hours
  }

  test("untilNow") {
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now - 100L.hours
      t0.untilNow shouldEqual 100L.hours
    }
  }
}
