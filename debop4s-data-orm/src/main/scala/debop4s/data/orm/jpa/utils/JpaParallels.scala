package debop4s.data.orm.jpa.utils

import java.lang.{Iterable => JIterable}
import java.util.{Collection => JCollection, List => JList}
import javax.persistence.{EntityManager, EntityManagerFactory}

import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * 병렬로 JPA 관련 작업을 수행합니다.
 * Created by debop on 2014. 3. 11.
 */
@deprecated("현재 테스트 중입니다. 멀티스레드로 JPA 작업은 하지 마시기 바랍니다.", "1.3.6")
trait JpaParRunnable[T] {

  /**
   * 병렬로 작업을 수행합니다.
   */
  def run(em: EntityManager, elem: T)

}

/**
 * 병렬로 JPA 관련 작업을 수행하고, 결과를 반환합니다.
 */
@deprecated("현재 테스트 중입니다. 멀티스레드로 JPA 작업은 하지 마시기 바랍니다.", "2.0.0")
trait JpaParCallable[T, R] {
  /**
   * 병렬로 JPA 관련 작업을 수행하고, 결과를 반환합니다.
   */
  def call(em: EntityManager, elem: T): R
}


/**
 * JPA 작업을 병렬로 수행할 수 있도록 합니다.
 * Created by debop on 2014. 3. 10.
 */
@deprecated("현재 테스트 중입니다. 멀티스레드로 JPA 작업은 하지 마시기 바랍니다.", "1.3.6")
object JpaParallels {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def run[T](emf: EntityManagerFactory, collection: JIterable[T])
            (action: (EntityManager, T) => Unit) {
    require(emf != null)

    collection.asScala.par.foreach { elem =>
      runUnit(emf.createEntityManager(), elem)(action)
    }
  }

  def runAction[T](emf: EntityManagerFactory,
                   collection: JIterable[T],
                   runnable: JpaParRunnable[T]) {
    require(emf != null)

    collection.asScala.par.foreach { elem =>
      val em = emf.createEntityManager()
      runUnit(em, elem) { (em, x) =>
        runnable.run(em, x)
      }
    }
  }

  private def runUnit[T](em: EntityManager, elem: T)
                        (action: (EntityManager, T) => Unit) {
    val tx = em.getTransaction
    tx.begin()
    try {
      action(em, elem)
      tx.commit()
    } catch {
      case NonFatal(e) =>
        tx.rollback()
        log.error(s"병렬 작업에 실패했습니다.", e)
    } finally {
      em.close()
    }
  }

  def call[T, V](emf: EntityManagerFactory, collection: JIterable[T])
                (func: (EntityManager, T) => V): JList[V] = {
    require(emf != null)

    collection.asScala.par.map { elem =>
      callUnit(emf.createEntityManager(), elem)(func)
    }.toList
    .asJava
  }

  def callFunc[T, V](emf: EntityManagerFactory,
                     collection: JIterable[T],
                     callable: JpaParCallable[T, V]): JList[V] = {
    require(emf != null)

    collection.asScala.par.map { elem =>
      val em = emf.createEntityManager()
      callUnit(em, elem) { (em, x) =>
        callable.call(em, x)
      }
    }.toList
    .asJava
  }

  private def callUnit[T, V](em: EntityManager, elem: T)
                            (func: (EntityManager, T) => V): V = {
    val tx = em.getTransaction
    tx.begin()
    try {
      val result = func(em, elem)
      tx.commit()
      return result
    } catch {
      case NonFatal(e) =>
        tx.rollback()
        log.error(s"병렬 작업에 실패했습니다.", e)
    } finally {
      em.close()
    }
    null.asInstanceOf[V]
  }
}