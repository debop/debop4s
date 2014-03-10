package com.github.debop4s.data.jpa.utils

import javax.persistence.{EntityManagerFactory, EntityManager}
import org.slf4j.LoggerFactory

/**
 * JPA 작업을 병렬로 수행할 수 있도록 합니다.
 * Created by debop on 2014. 3. 10.
 */
object JpaParallels {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def run[T](emf: EntityManagerFactory, collection: Iterable[T])(action: (EntityManager, T) => Unit) {
    collection
    .par
    .foreach { elem => runUnit(emf.createEntityManager(), elem)(action) }
  }

  def runUnit[T](em: EntityManager, elem: T)(action: (EntityManager, T) => Unit) {
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
    collection
    .par
    .map { elem => callUnit(emf.createEntityManager(), elem)(func) }
    .toIndexedSeq
  }

  def callUnit[T, V](em: EntityManager, elem: T)(func: (EntityManager, T) => V): V = {
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
