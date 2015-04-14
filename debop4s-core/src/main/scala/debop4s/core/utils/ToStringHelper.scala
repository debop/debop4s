package debop4s.core.utils

import scala.collection.mutable

/**
 * 문자열을 취합해 하나의 문자열로 표현해줍니다. 객체의 toString 에 사용합니다.
 * [[debop4s.core.ValueObject]] 를 상속받는 객체는 `buildStringHelper`를 재정의 하시면 됩니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
class ToStringHelper(val className: String) {

  def this(obj: AnyRef) = this(obj.getClass.getSimpleName)

  val map = new mutable.LinkedHashMap[String, Any]()

  def add(name: String, value: Any): ToStringHelper = {
    map.put(name, value)
    this
  }

  override def toString: String = {
    val builder = new StringBuilder(32)

    builder.append(className)
    builder.append("{")
    var sep = ""
    var first = true
    for ((n, v) <- map) {
      builder.append(sep)
      builder.append(n).append("=").append(v)
      if (first) {
        first = false
        sep = ","
      }
    }
    builder.append("}")
    builder.toString()
  }

  @inline
  private def addMap(name: String, value: Any) {
    map.put(name, value)
  }
}

object ToStringHelper {
  def apply(obj: Any) = new ToStringHelper(obj.getClass.getSimpleName)
}
