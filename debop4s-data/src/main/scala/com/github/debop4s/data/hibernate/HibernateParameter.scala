package com.github.debop4s.data.hibernate

import com.github.debop4s.data.AbstractNamedParameter
import org.hibernate.`type`.StandardBasicTypes

/**
 * [[org.hibernate.Query]]에 사용할 Hibernate용 Parameter 정보를 표현합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 19. 오후 9:39
 */
@SerialVersionUID(3975859046850492000L)
class HibernateParameter(name: String,
                         value: Any,
                         paramType: org.hibernate.`type`.Type)
    extends AbstractNamedParameter(name, value) {

    def this(name: String, value: Any) {
        this(name, value, StandardBasicTypes.SERIALIZABLE)
    }

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("paramType", paramType)
}
