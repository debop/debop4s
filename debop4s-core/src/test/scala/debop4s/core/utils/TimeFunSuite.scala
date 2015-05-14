package debop4s.core.utils

import java.util.concurrent.TimeUnit

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.conversions.time._
import debop4s.core.io.Serializers

import scala.concurrent.duration.Duration

trait TimeLikeFunSuite[T <: TimeLike[T]] extends AbstractCoreFunSuite {
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
    Undefined compare Undefined shouldBe 0
    Inf compare Inf shouldBe 0
    MinusInf compare MinusInf shouldBe 0

    Inf + Duration.Inf shouldBe Inf
    MinusInf - Duration.MinusInf shouldBe Undefined
    Inf - Duration.Inf shouldBe Undefined
    MinusInf + Duration.MinusInf shouldBe MinusInf
  }

  test("complementary diff") {
    // Note that this doesn't always stay because of two's complement arithmetic.
    for (a <- easyVs; b <- easyVs) {
      a diff b shouldBe (-(b diff a))
    }
  }

  test("compelementary compare") {
    for (a <- vs; b <- vs) {
      val x = a compare b
      val y = b compare a
      (x == 0 && y == 0) || (x * y < 0) shouldBe true
    }
  }

  test("commutative max") {
    for (a <- vs; b <- vs) {
      a max b shouldBe (b max a)
    }
  }
  test("commutative min") {
    for (a <- vs; b <- vs) {
      a min b shouldBe (b min a)
    }
  }
  test("handle underflows") {
    fromNanoseconds(Long.MinValue) - 1.nanoseconds shouldBe MinusInf
    fromMicroseconds(Long.MinValue) - 1.nanoseconds shouldBe MinusInf
  }
  test("handle overflows") {
    fromNanoseconds(Long.MaxValue) + 1.nanoseconds shouldBe Inf
    fromMicroseconds(Long.MaxValue) + 1.nanoseconds shouldBe Inf
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
      Serializers.copyObject(v) shouldBe v
    }
  }

  test("Inf - be impermeable to finite arithmetic") {
    Inf - 0L.seconds shouldBe Inf
    Inf - 100L.seconds shouldBe Inf
    Inf - Duration.fromNanos(Long.MaxValue) shouldBe Inf
  }

  test("Inf - become undefined when subtracted from itself, or added to bottom") {
    Inf - Duration.Inf shouldBe Undefined
    Inf + Duration.MinusInf shouldBe Undefined
  }

  test("Inf - not be equal to the maximum value") {
    fromNanoseconds(Long.MaxValue) should not be Inf
  }

  test("Inf - always be max") {
    Inf max fromSeconds(1) shouldBe Inf
    Inf max fromNanoseconds(Long.MaxValue) shouldBe Inf
    Inf max MinusInf shouldBe Inf
  }

  test("Inf - greater than everything else") {
    fromSeconds(0) should be < Inf
    fromNanoseconds(Long.MaxValue) should be < Inf
  }

  test("Inf - equal to itself") {
    Inf shouldBe Inf
  }

  test("Inf - more or less equals only to itself") {
    Inf.moreOrLessEquals(Inf, Duration.Inf) shouldBe true
    Inf.moreOrLessEquals(Inf, Duration.Zero) shouldBe true

    Inf.moreOrLessEquals(MinusInf, Duration.Inf) shouldBe true
    Inf.moreOrLessEquals(MinusInf, Duration.Zero) shouldBe false

    Inf.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldBe true
    Inf.moreOrLessEquals(fromSeconds(0), Duration.MinusInf) shouldBe false
  }

  test("Inf - Undefined diff to Top") {
    Inf diff Inf shouldBe Duration.Undefined
  }

  test("MinusInf - be impermeable to finite arithmetic") {
    MinusInf - 0L.seconds shouldBe MinusInf
    MinusInf - 100L.seconds shouldBe MinusInf
    MinusInf - Duration.fromNanos(Long.MaxValue) shouldBe MinusInf
  }

  test("MinusInf - become undefined when added with Top or subtracted by bottom") {
    MinusInf + Duration.Inf shouldBe Undefined
    MinusInf - Duration.MinusInf shouldBe Undefined
  }

  test("MinusInf - always be min") {
    MinusInf min Inf shouldBe MinusInf
    MinusInf min fromNanoseconds(0) shouldBe MinusInf
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
    MinusInf shouldEqual MinusInf
  }

  test("MinusInf - more or less equals only to itself") {
    MinusInf.moreOrLessEquals(MinusInf, Duration.Inf) shouldBe true
    MinusInf.moreOrLessEquals(MinusInf, Duration.Zero) shouldBe true

    MinusInf.moreOrLessEquals(Inf, Duration.MinusInf) shouldBe false
    MinusInf.moreOrLessEquals(Inf, Duration.Zero) shouldBe false

    MinusInf.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldBe true
    MinusInf.moreOrLessEquals(fromSeconds(0), Duration.MinusInf) shouldBe false
  }

  test("MinusInf - Undefined diff to Bottom") {
    MinusInf diff MinusInf shouldBe Duration.Undefined
  }

  test("Undefined - be impermeable to any arithmetic") {
    Undefined + 0L.seconds shouldBe Undefined
    Undefined + 100L.seconds shouldBe Undefined
    Undefined + Duration.fromNanos(Long.MaxValue) shouldBe Undefined
  }

  test("Undefined - become undefined when added with Top or subtracted by bottom") {
    Undefined + Duration.Inf shouldBe Undefined
    Undefined + Duration.MinusInf shouldBe Undefined
    Undefined - Duration.Undefined shouldBe Undefined
  }

  test("Undefined - always be max") {
    Undefined max Inf shouldBe Undefined
    Undefined max MinusInf shouldBe Undefined
    Undefined max fromNanoseconds(0L) shouldBe Undefined
  }

  test("Undefined - greater than everything else") {
    fromSeconds(0) should be < Undefined
    Inf should be < Undefined
    fromNanoseconds(Long.MaxValue) should be < Undefined
  }

  test("Undefined - equal to itself") {
    Undefined shouldBe Undefined
  }

  test("Undefined - not more or less equal to anything") {
    Undefined.moreOrLessEquals(Undefined, Duration.Inf) shouldBe false
    Undefined.moreOrLessEquals(Undefined, Duration.Zero) shouldBe false

    Undefined.moreOrLessEquals(Inf, Duration.Undefined) shouldBe true
    Undefined.moreOrLessEquals(Inf, Duration.Zero) shouldBe false

    Undefined.moreOrLessEquals(fromSeconds(0), Duration.Inf) shouldBe false
    Undefined.moreOrLessEquals(fromSeconds(0), Duration.Undefined) shouldBe true
  }

  test("Undefined - on diff") {
    Undefined diff Inf shouldBe Duration.Undefined
    Undefined diff MinusInf shouldBe Duration.Undefined
    Undefined diff fromNanoseconds(123) shouldBe Duration.Undefined
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
      t.inNanoseconds shouldBe ns
      t.inMicroseconds shouldBe (ns / 1000L)
      t.inMilliseconds shouldBe (ns / 1000L / 1000L)
      t.inLongSeconds shouldBe (ns / 1000L / 1000L / 1000L)
      t.inMinutes shouldBe (ns / 1000L / 1000L / 1000L / 60L)
      t.inHours shouldBe (ns / 1000L / 1000L / 1000L / 60L / 60L)
      t.inDays shouldBe (ns / 1000L / 1000L / 1000L / 60L / 60L / 24L)
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
      t.inLongSeconds shouldBe t.inSeconds
    }
  }
  test("inSeconds clamp value to Int.MinValue or MaxValue when out of range") {
    val longNs = 2160000000000000000L // 25000.days
    fromNanoseconds(longNs).inSeconds shouldBe Int.MaxValue
    fromNanoseconds(-longNs).inSeconds shouldBe Int.MinValue
  }

  test("floor round down") {
    fromSeconds(60).floor(1.minutes) shouldBe fromSeconds(60)
    fromSeconds(100).floor(1.minutes) shouldBe fromSeconds(60)
    fromSeconds(119).floor(1.minutes) shouldBe fromSeconds(60)
    fromSeconds(120).floor(1.minutes) shouldBe fromSeconds(120)
  }

  test("floor maintain Inf and MinusInf") {
    Inf.floor(1.hours) shouldBe Inf
    MinusInf.floor(1.hours) shouldBe MinusInf
  }

  test("floor divide by zero") {
    Zero.floor(Duration.Zero) shouldBe Undefined
    fromSeconds(1).floor(Duration.Zero) shouldBe Inf
    fromSeconds(-1).floor(Duration.Zero) shouldBe MinusInf
  }

  test("floor deal with undefineds") {
    MinusInf floor 1L.seconds shouldBe MinusInf
    Undefined floor 0L.seconds shouldBe Undefined
    Undefined floor Duration.Inf shouldBe Undefined
    Undefined floor Duration.MinusInf shouldBe Undefined
    Undefined floor Duration.Undefined shouldBe Undefined
  }

  test("floor itself") {
    for (s <- Seq[Long](Long.MinValue, -1, 1, Long.MaxValue)) {
      val t = fromNanoseconds(s)
      t floor TimeUtil.toDuration(t.inNanoseconds) shouldBe t
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
    fromMilliseconds(millis + 1) shouldBe Inf
  }

  test("from - underflow millis") {
    val millis = TimeUnit.NANOSECONDS.toMillis(Long.MinValue)
    fromMilliseconds(millis) match {
      case Nanoseconds(ns) => assert(ns == millis * 1e6)
    }
    fromMilliseconds(millis - 1) shouldBe MinusInf
  }
}

class TimeFunSuite extends {val ops = Time } with TimeLikeFunSuite[Time] {

  test("work in collections") {
    val t0 = Time.fromSeconds(100)
    val t1 = Time.fromSeconds(100)

    t0 shouldBe t1
    t0.hashCode shouldBe t1.hashCode
    val pairs = List((t0, "foo"), (t1, "bar"))
    pairs.groupBy {
      case (time: Time, value: String) => time
    } shouldBe Map(t0 -> pairs)
  }

  test("now should be now") {
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("withTimeAt") {
    val t0 = new Time(123456789L)
    Time.withTimeAt(t0) { _ =>
      Time.now shouldBe t0
      Thread.sleep(50)
      Time.now shouldBe t0
    }
    println(s"Time.now.inMillis=${ Time.now }, currentTimeMillis=${ System.currentTimeMillis() }")
    (Time.now.inMillis - System.currentTimeMillis).abs should be < 20L
  }

  test("withTimeAt nested") {
    val t0 = new Time(123456789L)
    val t1 = t0 + 10L.minutes
    Time.withTimeAt(t0) { _ =>
      Time.now shouldBe t0
      Time.withTimeAt(t1) { _ =>
        Time.now shouldBe t1
      }
      Time.now shouldBe t0
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("withTimeAt threaded") {
    val t0 = new Time(314159L)
    val t1 = new Time(314160L)
    Time.withTimeAt(t0) { tc =>
      Time.now shouldBe t0
      Thread.sleep(50)
      Time.now shouldBe t0
      tc.advance(Duration.fromNanos(1))
      Time.now shouldBe t1
      tc.set(t0)
      Time.now shouldBe t0

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
      Time.now shouldBe t0
      Thread.sleep(50)
      Time.now shouldBe t0
      val delta = 100.milliseconds
      t += delta
      Time.now shouldBe (t0 + delta)
    }
  }

  test("withCurrentTimeFrozen") {
    val t0 = new Time(123456789L)
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now
      Thread.sleep(50)
      Time.now shouldBe t0
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("advance") {
    val t0 = new Time(123456789L)
    val delta = 5L.seconds

    Time.withTimeAt(t0) { tc =>
      Time.now shouldBe t0
      tc.advance(delta)
      Time.now shouldBe (t0 + delta)
    }
    (Time.now.inMillis - System.currentTimeMillis()).abs should be < 20L
  }

  test("compare") {
    10L.seconds.afterEpoch should be < 11L.seconds.afterEpoch
    10L.seconds.afterEpoch shouldBe 10L.seconds.afterEpoch
    11L.seconds.afterEpoch should be > 10L.seconds.afterEpoch
    Time.fromMilliseconds(Long.MaxValue) should be > Time.now
  }

  test("+ delta") {
    10L.seconds.afterEpoch + 5L.seconds shouldBe 15L.seconds.afterEpoch
  }

  test("- delta") {
    10L.seconds.afterEpoch - 5L.seconds shouldBe 5L.seconds.afterEpoch
  }

  test("- time") {
    10L.seconds.afterEpoch - 5L.seconds.afterEpoch shouldBe 5L.seconds
  }

  test("max") {
    10L.seconds.afterEpoch max 5L.seconds.afterEpoch shouldBe 10L.seconds.afterEpoch
    5L.seconds.afterEpoch max 10L.seconds.afterEpoch shouldBe 10L.seconds.afterEpoch
  }

  test("min") {
    10L.seconds.afterEpoch min 5L.seconds.afterEpoch shouldBe 5L.seconds.afterEpoch
    5L.seconds.afterEpoch min 10L.seconds.afterEpoch shouldBe 5L.seconds.afterEpoch
  }

  test("moreOrLessEquals") {
    val now = Time.now
    now.moreOrLessEquals(now + 1.seconds, 1.seconds) shouldBe true
    now.moreOrLessEquals(now - 1.seconds, 1.seconds) shouldBe true
    now.moreOrLessEquals(now + 2.seconds, 1.seconds) shouldBe false
    now.moreOrLessEquals(now - 2.seconds, 1.seconds) shouldBe false
  }

  test("floor - like trim") {
    val format = new TimeFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val t0 = format.parse("2010-12-24 11:04:07.567")

    t0.floor(1.milliseconds) shouldBe t0
    t0.floor(10.milliseconds) shouldBe format.parse("2010-12-24 11:04:07.560")
    t0.floor(1.seconds) shouldBe format.parse("2010-12-24 11:04:07.000")
    t0.floor(5.seconds) shouldBe format.parse("2010-12-24 11:04:05.000")
    t0.floor(1.minutes) shouldBe format.parse("2010-12-24 11:04:00.000")
    t0.floor(1.hours) shouldBe format.parse("2010-12-24 11:00:00.000")
  }

  test("since") {
    val t0 = Time.now
    val t1 = t0 + 10L.seconds
    t1.since(t0) shouldBe 10L.seconds
    t0.since(t1) shouldBe (-10L).seconds
  }

  test("sinceEpoch") {
    val t0 = Time.epoch + 100L.hours
    t0.sinceEpoch shouldBe 100L.hours
  }

  test("sinceNow") {
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now + 100L.hours
      t0.sinceNow shouldBe 100L.hours
    }
  }

  test("fromMicroseconds") {
    Time.fromMicroseconds(0).inNanoseconds shouldBe 0L
    Time.fromMicroseconds(-1).inNanoseconds shouldBe (-1L * 1000L)

    Time.fromMicroseconds(Long.MaxValue).inNanoseconds shouldBe Long.MaxValue
    Time.fromMicroseconds(Long.MaxValue - 1) shouldBe Time.Inf

    Time.fromMicroseconds(Long.MinValue) shouldBe Time.MinusInf
    Time.fromMicroseconds(Long.MinValue + 1) shouldBe Time.MinusInf

    val currentTimeMicro = System.currentTimeMillis() * 1000
    Time.fromMicroseconds(currentTimeMicro).inNanoseconds shouldBe currentTimeMicro.microseconds.toNanos
  }

  test("fromMillis") {
    Time.fromMilliseconds(0).inNanoseconds shouldBe 0L
    Time.fromMilliseconds(-1).inNanoseconds shouldBe (-1L * 1000000L)

    Time.fromMilliseconds(Long.MaxValue).inNanoseconds shouldBe Long.MaxValue
    Time.fromMilliseconds(Long.MaxValue - 1) shouldBe Time.Inf

    Time.fromMilliseconds(Long.MinValue) shouldBe Time.MinusInf
    Time.fromMilliseconds(Long.MinValue + 1) shouldBe Time.MinusInf

    val currentTimeMs = System.currentTimeMillis()
    Time.fromMilliseconds(currentTimeMs).inNanoseconds shouldBe currentTimeMs * 1000000L
  }

  test("util") {
    val t0 = Time.now
    val t1 = t0 + 10L.seconds
    t0.until(t1) shouldBe 10L.seconds
    t1.until(t0) shouldBe (-10L).seconds
  }

  test("untilEpoch") {
    val t0 = Time.epoch - 100L.hours
    t0.untilEpoch shouldBe 100L.hours
  }

  test("untilNow") {
    Time.withCurrentTimeFrozen { _ =>
      val t0 = Time.now - 100L.hours
      t0.untilNow shouldBe 100L.hours
    }
  }
}
