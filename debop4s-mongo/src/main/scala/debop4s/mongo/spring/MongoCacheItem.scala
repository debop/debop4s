package debop4s.mongo.spring

import debop4s.core.{ToStringHelper, ValueObject}

/**
 * MongoDB에 저장될 캐시 항목을 표현합니다.
 * @author sunghyouk.bae@gmail.com
 */
@SerialVersionUID(3238564999573575444L)
class MongoCacheItem(val key: Any,
                     val value: Array[Byte],
                     val expireAt: Long) extends ValueObject {

  def this() = this(null, null, 0)

  override protected def buildStringHelper: ToStringHelper = {
    super.buildStringHelper
    .add("key", key)
    .add("value", value)
    .add("expireAt", expireAt)
  }
}
