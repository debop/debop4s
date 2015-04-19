package debop4s.data.orm.jpa.repository

import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import com.mysema.query.jpa.impl.{JPADeleteClause, JPAQuery, JPAUpdateClause}
import com.mysema.query.types.{EntityPath, OrderSpecifier}
import debop4s.core.{JFunction, JFunction1}
import debop4s.data.orm.jpa._
import debop4s.data.orm.model.HConnectPageImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import scala.annotation.varargs

/**
 * QueryDSL for JPA 를 손쉽게 사용할 수 있도록 해줍니다.
 * Created by debop on 2014. 1. 29..
 */
@Repository
@Transactional
class JpaQueryDslDao {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val emf: EntityManagerFactory = null
  @PersistenceContext val em: EntityManager = null

  /**
   * 새로운 `EntityManager`를 만들고, Tx 하에서 실행합니다.
   */
  def withNewEntityManager[T](block: EntityManager => T): T = {
    emf.withNewEntityManager { em =>
      block(em)
    }
  }

  /**
   * 새로운 `EntityManager`를 만들고, Tx 하에서 실행합니다.
   */
  def withNewEntityManager[T](func: JFunction1[EntityManager, T]): T = {
    emf.withNewEntityManager { em =>
      func.execute(em)
    }
  }

  /**
   * Transaction 환경 하에서 작업을 수행합니다.
   */
  @Transactional
  def withTransaction[T](block: => T): T = block
  /**
   * Transaction 환경 하에서 작업을 수행합니다.
   */
  @Transactional
  def withTransaction[T](func: JFunction[T]): T = func.execute()

  /**
   * 명시적으로 ReadOnly 모드로 코드 블럭을 실행합니다.
   */
  @Transactional(readOnly = true)
  def withReadOnly[T](block: => T): T = {
    em.withReadOnly {
      block
    }
  }

  /**
   * 지정한 Id 값을 가지는 Entity를 로드합니다.
   * @param entityClass entity class type
   * @param id identifier
   * @return entity
   */
  @Transactional(readOnly = true)
  def findOne[T](entityClass: Class[T], id: AnyRef) = {
    log.trace(s"findOne entity=$entityClass, id=$id")
    // em.withReadOnly {
    em.find(entityClass, id)
    // }
  }

  /**
   * Entity 에 대한 조회를 수행하여 엔티티의 컬렉션을 반환합니다.
   */
  @Transactional(readOnly = true)
  def findAll[T](path: EntityPath[T], query: JPAQuery, firstResult: Int, maxResults: Int): java.util.List[T] = {
    // em.withReadOnly {
    query.offset(firstResult)
    .limit(maxResults)
    .list(path)
    // }
  }

  @Transactional(readOnly = true)
  def getPage[T <: Comparable[_]](path: EntityPath[T],
                                  query: JPAQuery,
                                  pageNo: Int,
                                  pageSize: Int,
                                  orders: OrderSpecifier[T]*): Page[T] = {
    // em.withReadOnly {
    val totalCount = query.count()
    val entities = query.offset(pageNo * pageSize)
                   .limit(pageSize)
                   .orderBy(orders: _*)
                   .list(path)

    new HConnectPageImpl[T](entities, new PageRequest(pageNo, pageSize), totalCount)
    // }
  }

  def getQuery: JPAQuery = new JPAQuery(em)

  def getQuery(em: EntityManager) = new JPAQuery(em)

  def from(path: EntityPath[_]): JPAQuery = getQuery.from(path)

  @varargs
  def from(paths: EntityPath[_]*): JPAQuery = getQuery.from(paths: _*)

  @Transactional
  def deleteFrom(path: EntityPath[_]) = new JPADeleteClause(em, path)

  @Transactional
  def updateFrom(path: EntityPath[_]) = new JPAUpdateClause(em, path)
}
