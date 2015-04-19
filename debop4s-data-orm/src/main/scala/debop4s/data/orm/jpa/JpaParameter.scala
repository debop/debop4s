package debop4s.data.orm.jpa

import debop4s.data.orm.AbstractNamedParameter
import org.hibernate.`type`.StandardBasicTypes

import scala.beans.BeanProperty

/**
 * JpaParameter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 19. 오후 9:56
 */
@SerialVersionUID(2672746608417519438L)
class JpaParameter(@BeanProperty override val name: String,
                   @BeanProperty override val value: Any,
                   @BeanProperty val paramType: org.hibernate.`type`.Type = StandardBasicTypes.SERIALIZABLE)
  extends AbstractNamedParameter(name, value) {

  def this(name: String, value: Any) = this(name, value, StandardBasicTypes.SERIALIZABLE)

  override protected def buildStringHelper =
    super.buildStringHelper.add("paramType", paramType)
}

object JpaParameter {

  def apply(name: String, value: Any): JpaParameter = {
    new JpaParameter(name, value)
  }

  def apply(name: String, value: Any, paramType: org.hibernate.`type`.Type): JpaParameter = {
    new JpaParameter(name, value, paramType)
  }
}