package org.hibernate.cache.redis.tests.domain

import java.util.Objects


class PhoneNumber extends Serializable {

    var personId: Long = 0
    var numberType: String = "home"
    var phone: Long = 0


    override def equals(obj: Any): Boolean = {
        if ((obj != null) && obj.isInstanceOf[PhoneNumber]) hashCode == obj.hashCode
        else false
    }

    override def hashCode: Int = Objects.hash(personId.asInstanceOf[AnyRef], numberType)

    override def toString: String = numberType + ":" + phone
}
