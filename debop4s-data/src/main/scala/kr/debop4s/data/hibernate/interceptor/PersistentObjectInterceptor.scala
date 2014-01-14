package kr.debop4s.data.hibernate.interceptor

import java.io.Serializable
import kr.debop4s.data.model.PersistentObject
import org.hibernate.EmptyInterceptor
import org.hibernate.`type`.Type
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.data.hibernate.interceptor.PersistentObjectInterceptor
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:38
 */
class PersistentObjectInterceptor extends EmptyInterceptor {

    lazy val log = LoggerFactory.getLogger(getClass)

    def isPersisted(entity: AnyRef): Boolean = entity match {
        case p: PersistentObject => true
        case _ => false
    }

    override
    def onLoad(entity: Any,
               id: Serializable,
               state: Array[AnyRef],
               propertyNames: Array[String],
               types: Array[Type]): Boolean = {
        log.debug(s"엔티티 로드 후 PersistentObject의 상태를 갱신합니다.")

        entity match {
            case p: PersistentObject => p.onLoad()
            case _ =>
        }
        false
    }

    override
    def onSave(entity: scala.Any,
               id: Serializable,
               state: Array[AnyRef],
               propertyNames: Array[String],
               types: Array[Type]): Boolean = {
        log.debug(s"엔티티 로드 후 PersistentObject의 상태를 갱신합니다.")

        entity match {
            case p: PersistentObject => p.onPersist()
            case _ =>
        }
        false
    }
}
