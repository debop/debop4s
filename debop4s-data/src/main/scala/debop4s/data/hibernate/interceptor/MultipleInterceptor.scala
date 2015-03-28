package debop4s.data.hibernate.interceptor

import java.io.Serializable
import java.util
import org.hibernate.`type`.Type
import org.hibernate.{ Interceptor, EmptyInterceptor }
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer


object MultipleInterceptor {

  def apply(interceptors: Interceptor*): MultipleInterceptor = {
    val multiInterceptor = new MultipleInterceptor()
    multiInterceptor.interceptors ++= interceptors

    multiInterceptor
  }
}

/**
 * Hibernate [[Interceptor]]룰 복수로 등록하여 사용할 수 있도록 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:43
 */
class MultipleInterceptor extends EmptyInterceptor {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val interceptors = ArrayBuffer[Interceptor]()

  def addInterceptor(interceptor: Interceptor) {
    if (interceptor != null)
      this.interceptors += interceptor
  }

  def removeInterceptor(interceptor: Interceptor) {
    if (interceptor != null)
      this.interceptors -= interceptor
  }

  def exists: Boolean = interceptors != null && interceptors.size > 0

  override def onDelete(entity: Any,
                        id: Serializable,
                        state: Array[AnyRef],
                        propertyNames: Array[String],
                        types: Array[Type]) {
    if (exists) {
      interceptors.foreach {
        x =>
          x.onDelete(entity, id, state, propertyNames, types)
      }
    }
  }

  override def onFlushDirty(entity: Any,
                            id: Serializable,
                            currentState: Array[AnyRef],
                            previousState: Array[AnyRef],
                            propertyNames: Array[String],
                            types: Array[Type]): Boolean = {
    if (exists) {
      interceptors.foreach {
        x =>
          x.onFlushDirty(entity, id, currentState, previousState, propertyNames, types)
      }
    }
    false
  }

  override def onSave(entity: Any,
                      id: Serializable,
                      state: Array[AnyRef],
                      propertyNames: Array[String],
                      types: Array[Type]): Boolean = {
    if (exists) {
      interceptors.foreach {
        x =>
          x.onSave(entity, id, state, propertyNames, types)
      }
    }
    false
  }

  override def onLoad(entity: Any,
                      id: Serializable,
                      state: Array[AnyRef],
                      propertyNames: Array[String],
                      types: Array[Type]): Boolean = {
    if (exists) {
      interceptors.foreach {
        x =>
          x.onLoad(entity, id, state, propertyNames, types)
      }
    }
    false
  }

  override def postFlush(entities: util.Iterator[_]) {
    if (exists) {
      interceptors.foreach(x => x.postFlush(entities))
    }
  }

  override def preFlush(entities: util.Iterator[_]) {
    if (exists) {
      interceptors.foreach(x => x.preFlush(entities))
    }
  }
}
