package debop4s.data.orm.hibernate.interceptor

import java.io.Serializable

import debop4s.data.orm.model.PersistentObject
import org.hibernate.EmptyInterceptor
import org.hibernate.`type`.Type
import org.slf4j.LoggerFactory

/**
 * Hibernate Entity의 저장 상태를 관리하는 Interceptor입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:38
 */
class PersistentObjectInterceptor extends EmptyInterceptor {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override
  def onLoad(entity: Any,
             id: Serializable,
             state: Array[AnyRef],
             propertyNames: Array[String],
             types: Array[Type]): Boolean = {
    entity match {
      case p: PersistentObject => p.onLoad()
      case _ =>
    }
    false
  }

  override
  def onSave(entity: Any,
             id: Serializable,
             state: Array[AnyRef],
             propertyNames: Array[String],
             types: Array[Type]): Boolean = {
    entity match {
      case p: PersistentObject => p.onPersist()
      case _ =>
    }
    false
  }

  private def isPersisted(entity: AnyRef): Boolean = entity match {
    case p: PersistentObject => true
    case _ => false
  }
}
