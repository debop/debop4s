package com.github.debop4s.data.jpa.utils

import javax.persistence.{EntityManagerFactory, EntityManager}
import org.slf4j.LoggerFactory

/**
* 병렬로 JPA 관련 작업을 수행합니다.
 * Created by debop on 2014. 3. 11.
 */
trait JpaParRunnable[T] {

    /**
     * 병렬로 작업을 수행합니다.
     */
    def run(em: EntityManager, elem: T)

}

/**
* 병렬로 JPA 관련 작업을 수행하고, 결과를 반환합니다.
*/
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
object JpaParallels {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def run[T](emf: EntityManagerFactory, collection: Iterable[T])(action: (EntityManager, T) => Unit) {
        require(emf != null)
        collection.par.foreach { elem =>
            runUnit(emf.createEntityManager(), elem)(action)
        }
    }

    def runAction[T](emf: EntityManagerFactory, collection: Iterable[T], runnable: JpaParRunnable[T]) {
        require(emf != null)
        collection.par.foreach { elem =>
            val em = emf.createEntityManager()
            runUnit(em, elem) { (em, x) =>
                runnable.run(em, x)
            }
        }
    }

    @inline
    private def runUnit[T](em: EntityManager, elem: T)(action: (EntityManager, T) => Unit) {
        val tx = em.getTransaction
        tx.begin()
        try {
            action(em, elem)
            tx.commit()
        } catch {
            case e: Throwable =>
                tx.rollback()
                log.error(s"병렬 작업에 실패했습니다.", e)
        } finally {
            em.close()
        }
    }

    def call[T, V](emf: EntityManagerFactory, collection: Iterable[T])(func: (EntityManager, T) => V): IndexedSeq[V] = {
        require(emf != null)
        collection.par.map { elem =>
            callUnit(emf.createEntityManager(), elem)(func)
        }.toIndexedSeq
    }

    def callFunc[T, V](emf: EntityManagerFactory, collection: Iterable[T], callable: JpaParCallable[T, V]): IndexedSeq[V] = {
        require(emf != null)
        collection.par.map { elem =>
            val em = emf.createEntityManager()
            callUnit(em, elem) { (em, x) =>
                callable.call(em, x)
            }
        }.toIndexedSeq
    }

    @inline
    private def callUnit[T, V](em: EntityManager, elem: T)(func: (EntityManager, T) => V): V = {
        val tx = em.getTransaction
        tx.begin()
        try {
            val result = func(em, elem)
            tx.commit()
            return result
        } catch {
            case e: Throwable =>
                tx.rollback()
                log.error(s"병렬 작업에 실패했습니다.", e)
        } finally {
            em.close()
        }
        null.asInstanceOf[V]
    }
}
