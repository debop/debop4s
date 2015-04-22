package debop4s.core.utils

import org.scalactic.Equality
import org.scalactic.TolerantNumerics._
import org.scalactic.TripleEquals._
import org.slf4j.LoggerFactory

import scala.annotation.switch
import scala.util.control.NonFatal

/**
 * 길이 관련 단위를 나타냅니다.
 * @author sunghyouk.bae@gmail.com
 */
class LengthUnit(val meter: Double) extends Ordered[LengthUnit] {

  // mm 까지만 비교할 수 있도록 한다.
  implicit val dblEquality: Equality[Double] = tolerantDoubleEquality(0.001)

  def inMilliMeters: Double = meter * 1000
  def inCentiMeters: Double = meter * 10
  def inMeters: Double = meter
  def inKiloMeters: Double = inMeters / 1000

  def inInch: Double = meter * 39.37
  def inFeet: Double = meter * 1.0936
  def inYard: Double = meter * 1.0936
  def inMile: Double = meter / 1609.3

  def +(that: LengthUnit): LengthUnit = new LengthUnit(this.meter + that.meter)
  def -(that: LengthUnit): LengthUnit = new LengthUnit(this.meter - that.meter)
  def *(scala: Double): LengthUnit = new LengthUnit(this.meter * scala)
  def /(scala: Double): LengthUnit = new LengthUnit(this.meter / scala)

  @inline
  override def equals(obj: Any): Boolean = {
    (obj: @switch) match {
      case other: LengthUnit => meter === other.meter
      case _ => false
    }
  }
  override def hashCode: Int = meter.hashCode()
  override def toString: String = f"$meter%.2f.meter"
  override def compare(that: LengthUnit): Int = {
    if (meter < that.meter) -1
    else if (meter > that.meter) 1
    else 0
  }

  def toHuman: String = {
    var prefix = ""
    var display = meter.abs

    if (display < 0.1) {
      prefix = "m"
      display *= 1000.0
    } else if (display < 1.0) {
      prefix = "c"
      display *= 10
    } else if (display > 1000.0) {
      prefix = "k"
      display /= 1000.0
    }

    "%.1f %sm".format(display * meter.signum, prefix)
  }
}

object LengthUnit {

  private val log = LoggerFactory.getLogger(getClass)

  lazy val positiveInfinite = new LengthUnit(Double.PositiveInfinity)
  lazy val negativeInfinite = new LengthUnit(Double.NegativeInfinity)

  def parse(str: String): LengthUnit = {
    try {
      var (v, u) = str.splitAt(str.lastIndexOf("."))
      if (Strings.isEmpty(v)) v = ""
      if (u startsWith ".") u = u drop 1

      log.debug(s"parsing distance. v=$v, u=$u")

      val vv = v.toDouble
      val uu = factor(u)
      new LengthUnit(vv * uu)
    } catch {
      case NonFatal(e) =>
        throw new NumberFormatException(s"알 수 없는 길이 표시 문자열입니다. str=$str")
    }
  }

  private def factor(unit: String): Double = {
    var lower = unit.toLowerCase
    if (lower endsWith "s")
      lower = lower dropRight 1

    lower match {
      case "millimeter" => 0.001
      case "centimeter" => 0.1
      case "meter" => 1
      case "kilometer" => 1000
      case badUnit => throw new NumberFormatException(s"알 수 없는 길이 표시 단위입니다. unit=$unit")
    }
  }
}
