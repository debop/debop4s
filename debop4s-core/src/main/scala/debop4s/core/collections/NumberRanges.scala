package debop4s.core.collections

import scala.collection.immutable.{Seq, NumericRange}
import scala.collection.immutable.NumericRange.Exclusive

/**
 * NumberRanges
 * Created by debop on 2014. 4. 21.
 */
object NumberRanges {

  object Int {
    def apply(count: Int): Exclusive[Int] = Range.Int(0, count, 1)

    def apply(start: Int, end: Int, step: Int = 1): Exclusive[Int] =
      Range.Int(start, end, step)

    def group(start: Int, end: Int, step: Int, groupSize: Int): Iterator[Seq[Int]] =
      Range.Int(start, end, step).toSeq.grouped(groupSize)
  }

  object Long {
    def apply(count: Long): Exclusive[Long] = Range.Long(0, count, 1)

    def apply(start: Long, end: Long, step: Long = 1L): Exclusive[Long] =
      Range.Long(start, end, step)

    def group(start: Long, end: Long, step: Long, groupSize: Int): Iterator[Seq[Long]] =
      Range.Long(start, end, step).toSeq.grouped(groupSize)
  }

  object BigInt {
    def apply(count: BigInt): Exclusive[BigInt] = Range.BigInt(0, count, 1)

    def apply(start: BigInt, end: BigInt, step: BigInt = 1L): Exclusive[BigInt] =
      Range.BigInt(start, end, step)

    def group(start: BigInt, end: BigInt, step: BigInt, groupSize: Int): Iterator[Seq[BigInt]] =
      Range.BigInt(start, end, step).toSeq.grouped(groupSize)
  }

  object BigDecimal {
    def apply(count: BigDecimal) = Range.BigDecimal(0, count, 1)

    def apply(start: BigDecimal, end: BigDecimal, step: BigDecimal = 1L): Exclusive[BigDecimal] =
      Range.BigDecimal(start, end, step)

    def group(start: BigDecimal, end: BigDecimal, step: BigDecimal, groupSize: Int): Iterator[Seq[BigDecimal]] =
      Range.BigDecimal(start, end, step).toSeq.grouped(groupSize)
  }

  object Double {
    def apply(count: Double): NumericRange[Double] = Range.Double(0, count, 1)

    def apply(start: Double, end: Double, step: Double = 1D): NumericRange[Double] =
      Range.Double(start, end, step)

    def group(start: Double, end: Double, step: Double, groupSize: Int): Iterator[Seq[Double]] =
      Range.Double(start, end, step).toSeq.grouped(groupSize)
  }
}
