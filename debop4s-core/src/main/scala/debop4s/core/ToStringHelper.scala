package debop4s.core

import java.util

import debop4s.core.utils.Strings
import scala.collection.JavaConverters._

object ToStringHelper {
  def apply(obj: Any) = new ToStringHelper(obj.getClass.getSimpleName)
}

/**
 * 문자열을 취합해 하나의 문자열로 표현해줍니다. 객체의 toString 에 사용합니다.
 * [[debop4s.core.ValueObject]] 를 상속받는 객체는 `buildStringHelper`를 재정의 하시면 됩니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
class ToStringHelper(val className: String) extends Serializable {

  def this(obj: AnyRef) = this(obj.getClass.getSimpleName)

  private[this] val map = new util.LinkedHashMap[String, Any]()

  def add(name: String, value: Any): ToStringHelper = {
    map.put(name, value)
    this
  }

  override def toString: String = {
    val builder = new StringBuilder(map.size() * 4 + 2)

    builder.append(className)
    builder.append("{")
    Strings.mkString(builder, map.asScala, ",")
    builder.append("}")
    builder.toString()
  }
}


