package org.hibernate.cache.rediscala.tests.domain

import java.util.Objects
import scala.beans.BeanProperty

class PhoneNumber extends Serializable {

    @BeanProperty
    var personId: java.lang.Long = 0

    @BeanProperty
    var numberType: String = "home"

    @BeanProperty
    var phone: Long = 0


    override def equals(obj: Any): Boolean = {
        if ((obj != null) && obj.isInstanceOf[PhoneNumber]) hashCode == obj.hashCode
        else false
    }

    override def hashCode: Int = Objects.hash(personId.asInstanceOf[AnyRef], numberType)

    override def toString: String = numberType + ":" + phone
}
