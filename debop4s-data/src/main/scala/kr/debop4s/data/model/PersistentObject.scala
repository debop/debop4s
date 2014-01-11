package kr.debop4s.data.model

import java.beans.Transient
import kr.debop4s.core.ValueObject
import kr.debop4s.core.utils.ToStringHelper

/**
 * kr.debop4s.data.model.PersistentObject
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:42
 */
@SerialVersionUID(-7066710546641101707L)
trait PersistentObject extends ValueObject {

    @Transient private var persisted: Boolean = false

    def isPersisted = persisted

    protected def setPersisted(v: Boolean) {
        persisted = v
    }

    def onPersist() {
        persisted = true
    }

    def onLoad() {
        persisted = true
    }

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
            .add("persisted", persisted)
}
