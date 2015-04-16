package debop4s.core.conversions

import debop4s.core.utils.{LengthUnit, WeightUnit}

/**
 * 단위 변환 관련
 * @author sunghyouk.bae@gmail.com
 */
object units {

  /** 온도 변환 관련 확장 클래스 */
  class RichTemperature(val underlying: Double) {

    /** 섭시를 화씨로 변환 */
    def toFahrenheit: Double = underlying * 1.8 + 32.0

    /** 화씨를 섭시로 변환 */
    def toCelsius: Double = (underlying - 32.0) / 1.8
  }

  implicit def intToRichTemperature(i: Int): RichTemperature = new RichTemperature(i.toDouble)
  implicit def longToRichTemperature(l: Long): RichTemperature = new RichTemperature(l.toDouble)
  implicit def floatToRichTemperature(f: Float): RichTemperature = new RichTemperature(f.toDouble)
  implicit def doubleToRichTemperature(d: Double): RichTemperature = new RichTemperature(d)

  /**
   * 거리 단위 변환을 위한 클래스입니다.
   * `DistanceUnit` 을 사용합니다.
   */
  class RichLength(val distance: Double) {
    def millimeter = new LengthUnit(distance / 1000.0)
    def centimeter = new LengthUnit(distance / 10.0)
    def meter = new LengthUnit(distance)
    def kilometer = new LengthUnit(distance * 1000.0)

    def inch = new LengthUnit(distance / 39.37)
    def feet = new LengthUnit(distance / 3.2809)
    def yard = new LengthUnit(distance / 1.0936)
    def mile = new LengthUnit(distance * 1609.3)
  }

  implicit def intToRichDistance(i: Int): RichLength = new RichLength(i.toDouble)
  implicit def longToRichDistance(l: Long): RichLength = new RichLength(l.toDouble)
  implicit def floatToRichDistance(f: Float): RichLength = new RichLength(f.toDouble)
  implicit def doubleToRichDistance(d: Double): RichLength = new RichLength(d)

  /**
   * 무게 단위 변환을 위한 클래스입니다.
   * `WeightUnit` 을 사용합니다.
   */
  class RichWeight(val weight: Double) {
    def milligram = new WeightUnit(weight / 1000.0)
    def gram = new WeightUnit(weight)
    def kilogram = new WeightUnit(weight * 1000.0)
    def ton = new WeightUnit(weight * 1000000.0)

    def grain = new WeightUnit(weight / 15.432)
    def once = new WeightUnit(weight * 28.3495)
    def found = new WeightUnit(weight * 453.592)
  }

  implicit def intToRichWeight(i: Int): RichWeight = new RichWeight(i.toDouble)
  implicit def longToRichWeight(l: Long): RichWeight = new RichWeight(l.toDouble)
  implicit def floatToRichWeight(f: Float): RichWeight = new RichWeight(f.toDouble)
  implicit def doubleToRichWeight(d: Double): RichWeight = new RichWeight(d)
}
