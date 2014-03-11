package com.github.debop4s.data.hibernate

import com.github.debop4s.data.AbstractNamedParameter
import org.hibernate.`type`.StandardBasicTypes
import scala.beans.BeanProperty

/**
 * [[org.hibernate.Query]]에 사용할 Hibernate용 Parameter 정보를 표현합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 19. 오후 9:39
 */
@SerialVersionUID(3975859046850492000L)
class HibernateParameter(@BeanProperty override val name: String,
                         @BeanProperty override val value: Any,
                         @BeanProperty val paramType: org.hibernate.`type`.Type = StandardBasicTypes.SERIALIZABLE)
  extends AbstractNamedParameter(name, value) {

  override protected def buildStringHelper =
    super.buildStringHelper
    .add("paramType", paramType)
}

object HibernateParameter {

  def apply(name: String, value: Any): HibernateParameter = {
    new HibernateParameter(name, value)
  }

  def apply(name: String, value: Any, paramType: org.hibernate.`type`.Type): HibernateParameter = {
    new HibernateParameter(name, value, paramType)
  }
}
