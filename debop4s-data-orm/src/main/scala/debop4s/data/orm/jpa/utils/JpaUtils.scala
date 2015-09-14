package debop4s.data.orm.jpa.utils

import java.sql.Connection
import java.util.{Calendar, Date}
import javax.persistence.criteria.{CriteriaQuery, Predicate, Root}
import javax.persistence.{EntityManager, EntityManagerFactory, TemporalType, TypedQuery}

import debop4s.core.Guard._
import debop4s.core.utils.Closer._
import debop4s.core.{JFunction, JFunction1}
import debop4s.data.orm.hibernate.utils.StatelessUtils
import debop4s.data.orm.jpa.JpaParameter
import debop4s.data.orm.model.HibernateEntity
import org.hibernate.internal.SessionImpl
import org.hibernate.{HibernateException, Session, StatelessSession}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.data.domain.{Pageable, Sort}
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support.{JpaEntityInformation, JpaEntityInformationSupport}
import org.springframework.transaction.annotation.Transactional

import scala.util.control.NonFatal

/**
 * JpaUtils
 * Created by debop on 2014. 1. 29..
 */
object JpaUtils {

  private val log = LoggerFactory.getLogger(getClass)

  val DELETE_ALL_QUERY_STRING: String = "delete from %s x"
  val COUNT_QUERY_STRING: String = "select count(%s) from %s x"

  def getQueryString(template: String, entityName: String): String = {
    shouldNotBeEmpty(template, "template")
    shouldNotBeEmpty(entityName, "entityName")
    template.format(entityName)
  }

  def getEntityInformation[T](em: EntityManager, entityClass: Class[T]): JpaEntityInformation[T, _] =
    JpaEntityInformationSupport.getEntityInformation(entityClass, em)

  def getQuery[T](em: EntityManager, resultClass: Class[T], spec: Specification[T], pageable: Pageable): TypedQuery[T] = {
    val sort = if (pageable == null) null else pageable.getSort
    getQuery(em, resultClass, spec, sort)
  }

  def getQuery[T](em: EntityManager, resultClass: Class[T], spec: Specification[T], sort: Sort): TypedQuery[T] = {
    val cb = em.getCriteriaBuilder
    val query = cb.createQuery(resultClass)

    val root = applySpecificationToCriteria(em, resultClass, spec, query)
    query.select(root)

    if (sort != null) {
      query.orderBy(QueryUtils.toOrders(sort, root, cb))
    }
    em.createQuery(query)
  }

  private def applySpecificationToCriteria[T](em: EntityManager,
                                              resultClass: Class[T],
                                              spec: Specification[T],
                                              query: CriteriaQuery[_]): Root[T] = {
    shouldNotBeNull(query, "query")

    val root = query.from(resultClass)
    if (spec != null) {
      val cb = em.getCriteriaBuilder
      val predicate: Predicate = spec.toPredicate(root, query, cb)

      if (predicate != null) {
        query.where(List(predicate): _*)
      }
    }
    root
  }

  def setParameters[X](query: TypedQuery[X], parameters: JpaParameter*) = {
    if (parameters != null) {
      parameters.foreach(p => {
        log.trace(s"파라미터 설정. $p")

        p.getValue match {
          case date: Date =>
            query.setParameter(p.getName, date, TemporalType.TIMESTAMP)

          case calendar: Calendar =>
            query.setParameter(p.getName, calendar, TemporalType.TIMESTAMP)

          case dateTime: DateTime =>
            query.setParameter(p.getName, dateTime.toDate, TemporalType.TIMESTAMP)

          case _ => query.setParameter(p.getName, p.getValue)
        }
      })
    }
    query
  }

  def setFirstResult[T](query: TypedQuery[T], firstResult: Int) = {
    if (firstResult >= 0)
      query.setFirstResult(firstResult)
    query
  }

  def setMaxResults[T](query: TypedQuery[T], maxResults: Int) = {
    if (maxResults > 0)
      query.setFirstResult(maxResults)
    query
  }

  def setPaging[T](query: TypedQuery[T], firstResult: Int, maxResults: Int) = {
    val q = setFirstResult(query, firstResult)
    setMaxResults(q, maxResults)
  }

  /**
   * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
   * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
   */
  def isLoaded(em: EntityManager, entity: AnyRef): Boolean = {
    em.getEntityManagerFactory.getPersistenceUnitUtil.isLoaded(entity)
  }

  /**
   * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
   * {{{
   *   isLoaded(dept, "employees")
   * }}}
   * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
   */
  def isLoaded(em: EntityManager, entity: AnyRef, propertyName: String): Boolean = {
    em.getEntityManagerFactory.getPersistenceUnitUtil.isLoaded(entity, propertyName)
  }

  /** transient object 이면 save 하고, detached 된 entity 면 merge 한다. */
  def save[T <: HibernateEntity[_]](em: EntityManager, entity: T): T = {
    if (entity.isPersisted && !em.contains(entity)) {
      em.merge(entity)
    } else {
      em.persist(entity)
      entity
    }
  }

  /** persistent object 이면 삭제합니다. */
  def delete[T <: HibernateEntity[_]](em: EntityManager, entity: T): Unit = {
    if (entity.isPersisted) {
      if (!em.contains(entity)) {
        em.remove(em.merge(entity))
      } else {
        em.remove(entity)
      }
    }
  }

  /**
   * 현 Thread 에서 사용하는 `EntityManager`로부터 `Connection` 정보를 얻는다.
   * @param em `EntityManager` instance.
   * @return database connection instance.
   */
  def currentConnection(em: EntityManager): Connection = {
    require(em != null, s"entity manager 가 null 입니다. @Transactional 을 지정해 주세요.")

    val session = em.unwrap(classOf[Session]).asInstanceOf[SessionImpl]

    session
    .getTransactionCoordinator
    .getJdbcCoordinator
    .getLogicalConnection
    .getConnection
  }

  /**
   * 지정한 코드 블럭을 읽기 전용으로 작업합니다.
   * @param em    EntityManager instance
   * @param block 읽기 전용으로 수행할 DB 작업
   * @tparam T    읽은 결과 값의 수형
   * @return 읽기 작업 결과
   */
  def withReadOnly[T](em: EntityManager)(block: => T): T = {
    require(em != null, "메소드나 Class 에 @Transactional 을 정의해 주세요.")

    val conn = currentConnection(em)
    val readOnly = conn.isReadOnly
    val autoCommit = conn.getAutoCommit

    try {
      log.trace(s"읽기전용 DB 작업을 위해 connection 설정을 읽기전용으로 변경합니다.")
      conn.setReadOnly(true)
      conn.setAutoCommit(false)

      block // 읽기 작업

    } finally {
      conn.setReadOnly(readOnly)
      conn.setAutoCommit(autoCommit)
      log.trace(s"읽기전용 DB 작업을 완료하고, 설정을 복원했습니다. readOnly=$readOnly, autoCommit=$autoCommit")
    }
  }

  /**
   * 지정한 코드 블럭을 읽기 전용으로 작업합니다.
   * @param em    EntityManager instance
   * @param func 읽기 전용으로 수행할 DB 작업
   * @tparam T    읽은 결과 값의 수형
   * @return 읽기 작업 결과
   */
  def withReadOnly[T](em: EntityManager, func: JFunction[T]): T = {
    withReadOnly[T](em) {
      func.execute()
    }
  }

  /**
   * 지정한 코드 블럭을 새로운 Transaction 에서 작업합니다.
   * @param em    EntityManager instance
   * @param block 새로운 Transaction하에서 으로 수행할 DB 작업
   * @tparam T    읽은 결과 값의 수형
   * @return 작업 결과
   */
  @Transactional
  def withTransaction[T](em: EntityManager)(block: => T): T = {
    block
  }

  @Transactional
  def withTransaction[T](em: EntityManager, func: JFunction[T]): T = {
    func.execute()
  }

  /**
   * 새로운 `EntityManager`를 생성하여, DB 작업을 수행하고, `EntityManager`는 소멸시킵니다.
   * @param emf `EntityManagerFactory` 인스턴스
   * @param block 실행할 코드 블럭
   * @tparam T 실행 결과 값의 수형
   * @return 실행 결과
   */
  def withNewEntityManager[T](emf: EntityManagerFactory)(block: EntityManager => T): T = {
    log.debug("새로운 entitymanager 를 생성하여, DB 작업을 수행합니다...")
    using(emf.createEntityManager) { em =>
      em.getTransaction.begin()
      try {
        val result = block(em)
        em.getTransaction.commit()
        log.debug("새로운 entitymanager 를 생성하여, DB 작업을 완료했습니다.")
        result
      } catch {
        case NonFatal(e) =>
          em.getTransaction.rollback()
          throw new HibernateException("새로운 entitymanager 으로 DB 작업 중에 예외가 발생했습니다.", e)
      }
    }
  }

  /**
   * 새로운 `EntityManager`를 생성하여, DB 작업을 수행하고, `EntityManager`는 소멸시킵니다.
   * @param func 실행할 코드 블럭
   * @tparam T 실행 결과 값의 수형
   * @return 실행 결과
   */
  def withNewEntityManager[T](emf: EntityManagerFactory, func: JFunction1[EntityManager, T]): T = {
    withNewEntityManager(emf) { em =>
      func.execute(em)
    }
  }

  /**
   * 지정한 코드 블럭을 새로운 EntityManager를 생성하여, 읽기 전용으로 작업합니다.
   * @param emf `EntityManagerFactory` 인스턴스
   * @param block 실행할 코드 블럭
   * @tparam T    읽은 결과 값의 수형
   * @return 읽기 작업 결과
   */
  def withNewEntityManagerReadOnly[T](emf: EntityManagerFactory)(block: EntityManager => T): T = {
    log.trace("새로운 entitymanager 를 생성하여, DB 읽기전용 작업을 수행합니다...")
    using(emf.createEntityManager) { em =>
      em.getTransaction.begin()
      try {
        val result = withReadOnly(em) { block(em) }
        em.getTransaction.commit()
        log.debug("새로운 entitymanager 를 생성하여, DB 읽기전용 작업을 완료했습니다.")
        result
      } catch {
        case NonFatal(e) =>
          em.getTransaction.rollback()
          throw new HibernateException("새로운 entitymanager로 DB 읽기전용 작업 시 예외가 발생했습니다.", e)
      }
    }
  }

  /**
   * 지정한 코드 블럭을 새로운 EntityManager를 생성하여, 읽기 전용으로 작업합니다.
   * @param emf `EntityManagerFactory` 인스턴스
   * @param func 실행할 코드 블럭
   * @tparam T    읽은 결과 값의 수형
   * @return 읽기 작업 결과
   */
  def withNewEntityManagerReadOnly[T](emf: EntityManagerFactory, func: JFunction1[EntityManager, T]): T = {
    withNewEntityManagerReadOnly(emf) { em =>
      withReadOnly(em) {
        func.execute(em)
      }
    }
  }

  /**
   * `StatelessSession` 을 이용하여 DB 읽기전용 작업을 할 수 있도록 합니다.
   */
  def withStatelessReadOnly[T](em: EntityManager)(block: StatelessSession => T): T = {
    StatelessUtils.withReadOnly(em)(block)
  }
  /**
   * `StatelessSession` 을 이용하여 DB 읽기전용 작업을 할 수 있도록 합니다.
   */
  def withStatelessReadOnly[T](em: EntityManager, func: JFunction1[StatelessSession, T]): T = {
    StatelessUtils.withReadOnly(em, func)
  }

  /**
   * `StatelessSession` 을 이용하여 DB 작업을 수행합니다.
   */
  def withStateless[T](em: EntityManager)(block: StatelessSession => T): T = {
    StatelessUtils.withTransaction(em)(block)
  }

  /**
   * `StatelessSession` 을 이용하여 DB 작업을 수행합니다.
   */
  def withStateless[T](em: EntityManager, func: JFunction1[StatelessSession, T]): T = {
    StatelessUtils.withReadOnly(em, func)
  }
}
