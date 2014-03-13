package com.github.debop4s.data.jpa.utils

import javax.persistence.{EntityManagerFactory, EntityManager}
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.{Isolation, Transactional}

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
@deprecated("아우 왜 부정기적으로 안되는지 잘 모르겠다...")
object JpaParallels {

    private lazy val log = LoggerFactory.getLogger(getClass)
    private lazy val processCount = Runtime.getRuntime.availableProcessors()

    def run[T](emf: EntityManagerFactory, collection: Iterable[T])
              (action: (EntityManager, T) => Unit) {
        require(emf != null)
        require(action != null)

        collection.par.foreach { elem =>
            val em = emf.createEntityManager()
            try {
                runInternal(em, elem)(action)
            } finally {
                em.close()
            }
        }
    }

    def runAction[T](emf: EntityManagerFactory,
                     collection: Iterable[T],
                     runnable: JpaParRunnable[T]) {
        require(emf != null)
        require(runnable != null)

        collection.par.foreach { elem =>
            val em = emf.createEntityManager()
            try {
                runInternal(em, elem) { (em1, x) => runnable.run(em1, x) }
            } finally {
                em.close()
            }
        }
    }

    private def runInternal[T](em: EntityManager, elem: T)(action: (EntityManager, T) => Unit) {
        val tx = em.getTransaction
        try {
            tx.begin()
            action(em, elem)
            tx.commit()
        } catch {
            case e: Throwable =>
                if (tx != null) tx.rollback()
                log.error(s"병렬 작업에 실패했습니다.", e)
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    def call[T, V](emf: EntityManagerFactory, collection: Iterable[T])
                  (func: (EntityManager, T) => V): IndexedSeq[V] = {
        require(emf != null)
        require(func != null)

        collection.par.map { elem =>
            val em = emf.createEntityManager()
            try {
                callInternal(em, elem)(func)
            } finally {
                em.close()
            }
        }.toIndexedSeq
    }

    def callFunc[T, V](emf: EntityManagerFactory,
                       collection: Iterable[T],
                       callable: JpaParCallable[T, V]): IndexedSeq[V] = {
        require(emf != null)
        require(callable != null)

        collection.par.map { elem =>
            val em = emf.createEntityManager()
            try {
                callInternal(em, elem) { (em1, x) => callable.call(em1, x) }
            } finally {
                em.close()
            }
        }.toIndexedSeq
    }

    private def callInternal[T, V](em: EntityManager, elem: T)
                                  (func: (EntityManager, T) => V): V = {
        val tx = em.getTransaction
        try {
            tx.begin()
            val result = func(em, elem)
            tx.commit()
            return result
        } catch {
            case e: Throwable =>
                if (tx != null) tx.rollback()
                log.error(s"병렬 작업에 실패했습니다.", e)
        }
        null.asInstanceOf[V]
    }
}
