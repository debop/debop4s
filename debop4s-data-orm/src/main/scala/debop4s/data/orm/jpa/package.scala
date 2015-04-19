package debop4s.data.orm

import java.sql.Connection
import java.util.{Calendar, Collections, Date}
import javax.persistence._
import javax.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import com.mysema.query.QueryMetadata
import com.mysema.query.jpa.impl.{JPADeleteClause, JPAQuery, JPAUpdateClause}
import com.mysema.query.types.{EntityPath, OrderSpecifier}
import debop4s.core.Logging
import debop4s.data.orm.jpa.utils.JpaUtils
import debop4s.data.orm.model.{HConnectPageImpl, HibernateEntity}
import org.hibernate.{HibernateException, StatelessSession}
import org.joda.time.DateTime
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.{Page, PageRequest, Pageable, Sort}
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support.{JpaEntityInformation, JpaEntityInformationSupport}

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
 * package
 * @author sunghyouk.bae@gmail.com
 */
package object jpa {
  implicit class EntityManagerFactoryExtensions(val emf: EntityManagerFactory) extends Logging {

    /**
     * 새로운 `EntityManager`를 생성하여, DB 작업을 수행하고, `EntityManager`는 소멸시킵니다.
     * @param block 실행할 코드 블럭
     * @tparam T 실행 결과 값의 수형
     * @return 실행 결과
     */
    def withNewEntityManager[T](block: EntityManager => T): T = {
      JpaUtils.withNewEntityManager(emf)(block)
    }

    /**
     * 새로운 `EntityManager` 를 생성하여, DB 읽기 전용 작업을 수행하고, `EntityManager`를 소멸시킵니다.
     * @param block 실행할 코드 블럭
     * @tparam T 실행 결과 값의 수형
     * @return 실행 결과
     */
    def withNewEntityManagerReadOnly[T](block: EntityManager => T): T = {
      JpaUtils.withNewEntityManagerReadOnly(emf)(block)
    }
  }

  /**
   * [[EntityManager]] 의 확장 메소드를 지원하는 implicit class 입니다.
   * @param em `EntityManager` instance
   */
  implicit class EntityManagerExtensions(val em: EntityManager) extends Logging {

    val countQueryPlaceholder = "*"
    val DELETE_ALL_QUERY_STRING: String = "delete from %s x"
    val COUNT_QUERY_STRING: String = "select count(%s) from %s x"

    def currentConnection: Connection = {
      JpaUtils.currentConnection(em)
    }

    /**
     * 지정한 `block`을 읽기 전용으로 수행합니다.
     * {{{
     *   em.withReadOnly {
     *    em.find(classOf[Employee], id)
     *   }
     * }}}
     * @param block 수행할 DB 작업
     * @tparam T 읽기 작업 결과 값의 수형
     * @return 읽기 작업 결과
     */
    def withReadOnly[T](block: => T): T = {
      JpaUtils.withReadOnly(em)(block)
    }

    def withTransaction[T](block: => T): T = {
      JpaUtils.withTransaction(em)(block)
    }

    /**
     * `StatelessSession` 을 이용하여 DB 작업을 수행합니다.
     */
    def withStateless[T](block: StatelessSession => T): T = {
      JpaUtils.withStateless(em)(block)
    }
    /**
     * `StatelessSession` 을 이용하여 DB 읽기전용 작업을 수행합니다.
     */
    def withReadOnlyStateless[T](block: StatelessSession => T): T = {
      JpaUtils.withStatelessReadOnly(em)(block)
    }

    /**
     * 엔티티 메타정보를 가져옵니다.
     * @param entityClass  엔티티 수형 정보
     * @tparam T 엔티티 수형
     * @return 엔티티의 메타 정보
     */
    def getEntityInfo[T](entityClass: Class[T]): JpaEntityInformation[T, _] = {
      JpaEntityInformationSupport.getEntityInformation[T](entityClass, em)
    }

    /**
     * 대상 엔티티에 대한 쿼리를 빌드합니다.
     * @param resultClass 엔티티 수형
     * @param spec  질의를 위한 spec (hibernate criteria 와 유사)
     * @tparam T 엔티티 수형
     * @return JPA 용 쿼리 수형
     */
    def getQuery[T](resultClass: Class[T], spec: Specification[T]): TypedQuery[T] = {
      getQuery(resultClass, spec, null.asInstanceOf[Sort])
    }

    /**
     * 대상 엔티티에 대한 쿼리를 빌드합니다.
     * @param resultClass 엔티티 수형
     * @param spec  질의를 위한 spec (hibernate criteria 와 유사)
     * @param pageable paging 처리를 위한 정보
     * @tparam T 엔티티 수형
     * @return JPA 용 쿼리 수형
     */
    def getQuery[T](resultClass: Class[T], spec: Specification[T], pageable: Pageable): TypedQuery[T] = {
      val sort = if (pageable == null) null else pageable.getSort
      getQuery(resultClass, spec, sort)
    }

    /**
     * 대상 엔티티에 대한 쿼리를 빌드합니다.
     * @param resultClass 엔티티 수형
     * @param spec  질의를 위한 spec (hibernate criteria 와 유사)
     * @param sort 정렬 정보
     * @tparam T 엔티티 수형
     * @return JPA 용 쿼리 수형
     */
    def getQuery[T](resultClass: Class[T], spec: Specification[T], sort: Sort): TypedQuery[T] = {
      val cb = em.getCriteriaBuilder
      val query = cb.createQuery(resultClass)

      val root = applySpecificationToCriteria(resultClass, spec, query)
      query.select(root)

      if (sort != null) {
        query.orderBy(QueryUtils.toOrders(sort, root, cb))
      }
      em.createQuery(query)
    }

    private def applySpecificationToCriteria[T](resultClass: Class[T],
                                                spec: Specification[T],
                                                query: CriteriaQuery[_]): Root[T] = {
      require(query != null)

      val root = query.from(resultClass)
      if (spec != null) {
        val cb = em.getCriteriaBuilder
        val predicate = spec.toPredicate(root, query, cb)

        if (predicate != null) {
          query.where(Seq(predicate): _*)
        }
      }
      root
    }

    /**
     * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
     * {{{
     *     em.isLoaded(dept)
     * }}}
     * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
     */
    def isLoaded(entity: AnyRef): Boolean = {
      em.getEntityManagerFactory.getPersistenceUnitUtil.isLoaded(entity)
    }

    /**
     * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
     * {{{
     *   em.isLoaded(dept, "employees")
     * }}}
     * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
     */
    def isLoaded(entity: AnyRef, propertyName: String): Boolean = {
      em.getEntityManagerFactory.getPersistenceUnitUtil.isLoaded(entity, propertyName)
    }

    def save[T <: HibernateEntity[_]](entity: T): T = {
      JpaUtils.save(em, entity)
    }

    /**
     * 지정한 엔티티를 삭제합니다.
     * @param entity  삭제할 엔티티
     * @tparam T 엔티티 수형
     */
    def delete[T <: HibernateEntity[_]](entity: T) {
      if (entity != null)
        em.remove(if (em.contains(entity)) entity else em.merge(entity))
    }

    /**
     * 지정한 id 값을 가진 엔티티를 삭제합니다.
     * @param entityClass  삭제할 엔티티의 수형
     * @param id 삭제할 엔티티의 id 값
     */
    def delete[T <: HibernateEntity[_]](entityClass: Class[T], id: java.io.Serializable) {
      require(id != null)

      val entity = em.find(entityClass, id)
      if (entity == null)
        throw new EmptyResultDataAccessException(s"No [$entityClass] entity with id [$id] exists.", 1)

      delete(entity)
    }

    /**
     * 해당하는 엔티티들을 삭제합니다.
     * @param entityClass 삭제할 엔티티의 수형
     * @param entities 삭제할 엔티티들
     * @tparam T  엔티티 수형
     */
    def deleteInBatch[T <: HibernateEntity[_]](entityClass: Class[T], entities: java.lang.Iterable[T]) {
      require(entities != null)

      val deleteQuery = DELETE_ALL_QUERY_STRING.format(getEntityInfo(entityClass).getEntityName)
      QueryUtils.applyAndBind(deleteQuery, entities, em).executeUpdate()
    }

    /**
     * 지정된 수형의 엔티티들을 모두 삭제합니다.
     * @param entityClass 삭제할 엔티티의 수형
     * @tparam T 엔티티 수형
     */
    def deleteAll[T <: HibernateEntity[_]](entityClass: Class[T]) {
      findAll(entityClass).asScala.foreach(delete(_))
    }

    /**
     * 지정한 id 값을 가지는 엔티티가 존재하는지 조회합니다.
     * @param entityClass 조회할 엔티티 수형
     * @param id  조회할 id 값
     * @tparam T 엔티티 수형
     * @return  지정한 엔티티 존재 여부
     */
    def exists[T <: HibernateEntity[_]](entityClass: Class[T], id: java.io.Serializable): Boolean = {
      trace(s"엔티티 존재여부 확인 중... entityClass=$entityClass, id=$id")

      val entityInfo = getEntityInfo(entityClass)
      if (entityInfo != null) {
        val placeHolder = countQueryPlaceholder
        val entityName = entityInfo.getEntityName
        val idAttributeNames = entityInfo.getIdAttributeNames
        val existsQuery = QueryUtils.getExistsQueryString(entityName, placeHolder, idAttributeNames)

        val query = em.createQuery(existsQuery, classOf[java.lang.Long])

        if (entityInfo.hasCompositeId) {
          idAttributeNames.asScala.foreach { name =>
            query.setParameter(name, entityInfo.getCompositeIdAttributeValue(id, name))
          }
        } else {
          query.setParameter(idAttributeNames.iterator().next(), id)
        }
        query.getSingleResult == 1L
      } else {
        findOne(entityClass, id) != null
      }
    }

    /**
     * 지정한 id 값을 가지는 엔티티를 조회합니다.
     *
     * @param entityClass 엔티티 수형
     * @param id          조회할 id
     * @param lockMode    lock mode
     * @tparam T          엔티티 수형
     * @return 조회한 엔티티
     */
    def findOne[T <: HibernateEntity[_]](entityClass: Class[T],
                                         id: java.io.Serializable,
                                         lockMode: LockModeType = LockModeType.NONE): T = {
      em.find(entityClass, id, lockMode)
    }

    /**
     * 지정한 수형의 엔티티를 조회합니다.
     * @param entityClass 조회할 엔티티 수형
     * @tparam T 엔티티 수형
     * @return 조회된 엔티티 컬렉션
     */
    def findAll[T <: HibernateEntity[_]](entityClass: Class[T]): java.util.List[T] = {
      getQuery[T](entityClass, null, null.asInstanceOf[Sort]).getResultList
    }

    /**
     * 지정한 id들을 가진 엔티티를 조회합니다.
     * @param entityClass 조회할 엔티티 수형
     * @tparam T 엔티티 수형
     * @return 조회된 엔티티 컬렉션
     */
    def findAll[T](entityClass: Class[T], ids: java.lang.Iterable[_]): java.util.List[T] = {
      if (ids == null || !ids.iterator().hasNext)
        return Collections.emptyList()

      val spec = new Specification[T]() {
        override def toPredicate(root: Root[T], query: CriteriaQuery[_], cb: CriteriaBuilder): Predicate = {
          val path = root.get(getEntityInfo(entityClass).getIdAttribute)
          path.in(cb.parameter(classOf[java.lang.Iterable[_]], "ids"))
        }
      }

      getQuery(entityClass, spec, null.asInstanceOf[Sort])
      .setParameter("ids", ids)
      .getResultList
    }
  }

  /**
   * `TypeQuery` 에 대한 Extension methods 를 제공합니다.
   * @param query `TypeQuery` instance.
   * @tparam T 대상 엔티티 수형
   */
  implicit class TypedQueryExtensions[T](val query: TypedQuery[T]) extends Logging {

    /**
     * 쿼리에 조건에 해당하는 파라미터 정보를 지정합니다.
     * @param parameters 설정할 파라미터들
     * @return [[TypedQuery]] 객체
     */
    @varargs
    def setParameters(parameters: JpaParameter*): TypedQuery[T] = {
      if (parameters != null) {
        parameters.foreach(p => {
          trace(s"파라미터 설정. $p")

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

    /**
     * 쿼리에 offset 정보를 설정합니다.
     * @param firstResult 첫번째 레코드의 index (0 부터 시작)
     * @return 쿼리 객체
     */
    def setFirstResult(firstResult: Int): TypedQuery[T] = {
      if (firstResult >= 0)
        query.setFirstResult(firstResult)
      query
    }

    /**
     * 쿼리에 offset 정보를 설정합니다.
     * @param maxResults 조회할 ResultSet의 최대 값
     * @return 쿼리 객체
     */
    def setMaxResults(maxResults: Int): TypedQuery[T] = {
      if (maxResults > 0)
        query.setMaxResults(maxResults)
      query
    }

    /**
     * 쿼리에 offset 정보를 설정합니다.
     * @param firstResult 첫번째 레코드의 index (0 부터 시작)
     * @param maxResults 조회할 ResultSet의 최대 값
     * @return 쿼리 객체
     */
    def setRange(firstResult: Int, maxResults: Int): TypedQuery[T] = {
      setFirstResult(firstResult)
      setMaxResults(maxResults)
    }

    /**
     * 쿼리에 Paging 설정을 합니다.
     * @param pageNo   Page No (0 부터 시작)
     * @param pageSize  Page 크기
     * @return 쿼리 객체
     */
    def setPage(pageNo: Int, pageSize: Int): TypedQuery[T] = {
      setFirstResult(pageNo * pageSize)
      setMaxResults(pageSize)
    }
  }

  /**
   * QueryDSL for JPA 에 대한 Extension Class 입니다.
   * @param em `EntityManager` instance
   */
  implicit class QueryDslExtensions(val em: EntityManager) extends Logging {

    /**
     * QueryDSL 의 `JPAQuery` 를 빌드합니다.
     * @return `JPAQuery` 인스턴스
     */
    def jpaQuery: JPAQuery = new JPAQuery(em)

    /**
     * QueryDSL 의 `JPAQuery` 를 빌드합니다.
     * @param metadata 메타데이터 정보
     * @return `JPAQuery` 인스턴스
     */
    def jpaQuery(metadata: QueryMetadata): JPAQuery = new JPAQuery(em, metadata)

    /**
     * QueryDSL from 절을 설정합니다.
     * @param path entity path
     * @return `JPAQuery` 인스턴스
     */
    def from(path: EntityPath[_]): JPAQuery = jpaQuery.from(path)

    /**
     * QueryDSL from 절을 설정합니다.
     * @param paths entity paths
     * @return `JPAQuery` 인스턴스
     */
    def from(paths: EntityPath[_]*): JPAQuery = jpaQuery.from(paths: _*)

    /**
     * 삭제를 위한 구문을 생성합니다.
     * @param path entity path
     * @return 삭제 구문
     */
    def deleteFrom(path: EntityPath[_]): JPADeleteClause =
      new JPADeleteClause(em, path)

    /**
     * 정보 갱신을 위한 Update 구문을 생성합니다.
     * @param path entity path
     * @return Update 구문
     */
    def updateFrom(path: EntityPath[_]): JPAUpdateClause =
      new JPAUpdateClause(em, path)
  }

  /**
   * QueryDSL `JPAQuery` 에 대한 Extension Methods 를 제공하는 implicit class 입니다.
   * @param query `JPAQuery` instance
   */
  implicit class JPAQueryExtensions(val query: JPAQuery) extends Logging {

    /**
     * `JPAQuery` 를 이용하여 엔티티를 조회하는 메소드입니다.
     * @param path   entity path
     * @param orders 정렬 방식
     * @tparam T 엔티티 수형
     * @return 조회한 엔티티의 컬렉션
     */
    def findAll[T <: Comparable[_]](path: EntityPath[T],
                                    orders: OrderSpecifier[T]*): java.util.List[T] = {
      query
      .orderBy(orders: _*)
      .list(path)
    }

    /**
     * `JPAQuery` 를 이용하여 엔티티를 조회하는 메소드입니다.
     * @param path   entity path
     * @param firstResult 첫번쩨 index (0부터 시작)
     * @param maxResults  최대 결과 레코드 수
     * @param orders 정렬 방식
     * @tparam T 엔티티 수형
     * @return 조회한 엔티티의 컬렉션
     */
    def findAll[T <: Comparable[_]](path: EntityPath[T],
                                    firstResult: Int,
                                    maxResults: Int,
                                    orders: OrderSpecifier[T]*): java.util.List[T] = {
      query
      .offset(firstResult)
      .limit(maxResults)
      .orderBy(orders: _*)
      .list(path)
    }

    /**
     * `JPAQuery` 를 이용하여 엔티티를 조회하는 메소드입니다.
     * @param path      entity path
     * @param pageNo    페이지 번호
     * @param pageSize  페이지 크기
     * @param orders    정렬 방식
     * @tparam T        엔티티 수형
     * @return          조회한 엔티티의 컬렉션
     */
    def getPage[T <: Comparable[_]](path: EntityPath[T],
                                    pageNo: Int,
                                    pageSize: Int,
                                    orders: OrderSpecifier[T]*): Page[T] = {
      val totalCount = query.count()
      val entities = findAll(path, pageNo * pageSize, pageSize, orders: _*)

      new HConnectPageImpl[T](entities, new PageRequest(pageNo, pageSize), totalCount)
    }
  }

  /**
   * `StatelessSession` 을 쉽게 사용할 수 있도록 하는 Extensions 입니다.
   * @param stateless [[StatelessSession]] 인스턴스
   */
  implicit class StatelessSessionExtensions(val stateless: StatelessSession) extends Logging {

    /**
     * Transaction 하에서 Steless Session 을 통해 DB 작업을 수행합니다.
     *
     * @param block 실제 작업 코드
     * @tparam T  결과 값 수형
     * @return 결과 값
     */
    def withTransaction[T](block: => T): T = {
      trace("새로운 Tx 하에서 Stateless 작업을 시작합니다...")

      val tx = stateless.beginTransaction()
      Try {
        block
      } match {
        case Success(result) =>
          tx.commit()
          trace("Stateless 작업에 성공했습니다.")
          result
        case Failure(e) =>
          Try { tx.rollback() } match {
            case Success(r) => warn("StatelessSession 작업을 rollback 했습니다.")
            case Failure(re) => throw new HibernateException("StatelessSession rollback 에 실패했습니다.", re)
          }
          throw new HibernateException("StatelessSession 작업에 실패했습니다.", e)
      }
    }

    /**
     * Transaction 하에서 Steless Session 을 통해 읽기 전용의 DB 작업을 수행합니다.
     *
     * {{{
     * @Autowired val stateless: StatelessSession = null
     * ...
     * stateless.withReadOnly { stateless =>
     * }
     * }}}
     *
     * @param block 실제 작업 코드
     * @tparam T  결과 값 수형
     * @return 결과 값
     */
    def withReadOnly[T](block: => T): T = {
      trace("Stateless 읽기 전용 작업을 시작합니다...")

      val tx = stateless.beginTransaction()
      val conn = stateless.connection()
      val originReadOnly = conn.isReadOnly
      val originAutoCommit = conn.getAutoCommit

      try {
        trace(s"Connection의 isReadOnly 속성값을 읽기전용으로 설정합니다. 기존 readOnly=$originReadOnly")
        conn.setReadOnly(true)
        conn.setAutoCommit(false)

        Try {
          block
        } match {
          case Success(result) =>
            tx.commit()
            trace("Stateless 작업에 성공했습니다.")
            result
          case Failure(e) =>
            Try { tx.rollback() } match {
              case Success(r) => warn("StatelessSession 작업을 rollback 했습니다.")
              case Failure(re) => throw new HibernateException("StatelessSession rollback에 실패했습니다.", re)
            }
            throw new HibernateException("StatelessSession 작업에 실패했습니다.", e)
        }
      } finally {
        conn.setReadOnly(originReadOnly)
        conn.setAutoCommit(originAutoCommit)
        trace(s"Connection의 isReadOnly 속성값을 기존 값으로 복원합니다. 기존 readOnly=$originReadOnly")
      }
    }
  }
}
