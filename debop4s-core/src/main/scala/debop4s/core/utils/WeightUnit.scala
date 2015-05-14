package debop4s.core.utils

import org.scalactic.TolerantNumerics._
import org.scalactic.TripleEquals._
import org.slf4j.LoggerFactory

import scala.annotation.switch
import scala.util.control.NonFatal

/**
 * 무게에 대한 단위를 표현합니다.
 * @author sunghyouk.bae@gmail.com
 */
class WeightUnit(val gram: Double) extends Ordered[WeightUnit] {

  // mm 까지만 비교할 수 있도록 한다.
  private[this] implicit val dblEquality = tolerantDoubleEquality(0.001)

  def inMilligram: Double = gram * 1000
  def inGram: Double = gram
  def inKilogram: Double = gram / 1000
  def inTon: Double = gram / 1000000

  def inGrain: Double = gram * 15.432
  def inOnce: Double = gram / 28.3495
  def inFound: Double = gram / 453.592

  def +(that: WeightUnit): WeightUnit = new WeightUnit(this.gram + that.gram)
  def -(that: WeightUnit): WeightUnit = new WeightUnit(this.gram - that.gram)
  def *(scala: Double): WeightUnit = new WeightUnit(this.gram * scala)
  def /(scala: Double): WeightUnit = new WeightUnit(this.gram / scala)

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: WeightUnit => gram === other.gram
      case _ => false
    }
  }
  override def hashCode: Int = gram.hashCode()
  override def toString: String = gram + ".gram"

  override def compare(that: WeightUnit): Int = {
    if (gram < that.gram) -1
    else if (gram > that.gram) 1
    else 0
  }

  /**
   * 수치에 따라 사람이 볼 때 쉽게 볼 수 있도록 합니다.
   * @return
   */
  def toHuman: String = {
    var prefix = ""
    var display = gram.toDouble.abs

    if (display < 1.0) {
      prefix = "m"
      display *= 1000
    }
    else if (display > 1000) {
      prefix = "k"
      display /= 1000
    } else if (display > 1000000) {
      display /= 1000000
      return "%.1f ton".format(display * gram.signum)
    }
    "%.1f %sg".format(display * gram.signum, prefix)
  }
}

object WeightUnit {

  private[this] val log = LoggerFactory.getLogger(getClass)

  lazy val positiveInfinite = new WeightUnit(Double.PositiveInfinity)
  lazy val negativeInfinite = new WeightUnit(Double.NegativeInfinity)

  def parse(str: String): WeightUnit = {
    try {
      var (v, u) = str.splitAt(str.lastIndexOf("."))
      if (Strings.isEmpty(v)) v = ""
      if (u startsWith ".") u = u drop 1

      log.debug(s"parsing weight. v=$v, u=$u")

      val vv = v.toDouble
      val uu = factor(u)
      new WeightUnit(vv * uu)
    } catch {
      case NonFatal(e) =>
        throw new NumberFormatException(s"알 수 없는 무게 표시 문자열입니다. str=$str")
    }
  }

  private def factor(unit: String): Double = {
    var lower = unit.toLowerCase
    if (lower endsWith "s")
      lower = lower dropRight 1

    lower match {
      case "milligram" => 0.001
      case "gram" => 1
      case "kilogram" => 1000
      case badUnit => throw new NumberFormatException(s"알 수 없는 무게 표시 단위입니다. unit=$unit")
    }
  }
}
