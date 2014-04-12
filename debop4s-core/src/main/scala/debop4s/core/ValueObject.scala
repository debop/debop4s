package debop4s.core

import debop4s.core.utils.ToStringHelper
import scala.annotation.switch

/**
 * DDD 의 Value Object를 표현합니다.
 * Java에서 상속 시 trait는 순수 interface가 아니라서 abstract class를 정의해야 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:30
 */
abstract class AbstractValueObject extends ValueObject

/**
 * DDD 의 Value Object를 표현합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:30
 */
trait ValueObject extends AnyRef with Serializable {

  @inline
  override def equals(obj: Any): Boolean = {
    (obj: @switch) match {
      case vo: ValueObject => hashCode() == obj.hashCode()
      case _ => false
    }
  }

  override def hashCode(): Int = System.identityHashCode(this)

  override def toString: String = buildStringHelper.toString

  protected def buildStringHelper: ToStringHelper = ToStringHelper(this)
}


