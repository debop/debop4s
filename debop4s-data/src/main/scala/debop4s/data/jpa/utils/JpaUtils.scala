package debop4s.data.jpa.utils

import debop4s.data.jpa.JpaParameter
import java.util.{ Calendar, Date }
import javax.persistence.criteria.{ Predicate, Root, CriteriaQuery }
import javax.persistence.{ TemporalType, TypedQuery, EntityManager }
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.data.domain.{ Sort, Pageable }
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support.{ JpaEntityInformationSupport, JpaEntityInformation }


/**
 * JpaUtils
 * Created by debop on 2014. 2. 25.
 */
object JpaUtils {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val DELETE_ALL_QUERY_STRING: String = "delete from %s x"
  val COUNT_QUERY_STRING: String = "select count(%s) from %s x"

  def getQueryString(template: String, entityName: String): String = {
    template.format(entityName)
  }

  def getEntityInformation[T](em: EntityManager, entityClass: Class[T]): JpaEntityInformation[T, _] =
    JpaEntityInformationSupport.getMetadata(entityClass, em)

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

  private def applySpecificationToCriteria[T, S](em: EntityManager,
                                                 resultClass: Class[T],
                                                 spec: Specification[T],
                                                 query: CriteriaQuery[_]): Root[T] = {
    require(query != null)

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

  def setParameters[X](query: TypedQuery[X], parameters: JpaParameter*): TypedQuery[X] = {
    if (parameters == null || parameters.length == 0)
      return query


    parameters.foreach { p =>
      log.trace(s"파라미터 설정. $p")

      p.value match {
        case date: Date =>
          query.setParameter(p.name, date, TemporalType.TIMESTAMP)

        case calendar: Calendar =>
          query.setParameter(p.name, calendar, TemporalType.TIMESTAMP)

        case dateTime: DateTime =>
          query.setParameter(p.name, dateTime.toDate, TemporalType.TIMESTAMP)

        case _ => query.setParameter(p.name, p.value)
      }
    }
    query
  }

  def setFirstResult[T](query: TypedQuery[T], firstResult: Int): TypedQuery[T] = {
    if (firstResult >= 0) {
      query.setFirstResult(firstResult)
    }
    query
  }

  def setMaxResults[T](query: TypedQuery[T], maxResults: Int): TypedQuery[T] = {
    if (maxResults > 0) {
      query.setFirstResult(maxResults)
    }
    query
  }

  def setPaging[T](query: TypedQuery[T], firstResult: Int, maxResults: Int): TypedQuery[T] = {
    val q = setFirstResult(query, firstResult)
    setMaxResults(q, maxResults)
  }

  /**
   * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
   * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
   */
  def isLoaded(em: EntityManager, entity: AnyRef): Boolean = {
    val unitUtil = em.getEntityManagerFactory.getPersistenceUnitUtil
    unitUtil.isLoaded(entity)
  }

  /**
   * Lazy Initialize 속성에 대해 Initialize 가 되었는지 확인합니다.
   * {{{
   *   isLoaded(dept, "employees")
   * }}}
   * @see Hibernate#initialize 를 사용하는게 더 낫습니다.
   */
  def isLoaded(em: EntityManager, entity: AnyRef, propertyName: String): Boolean = {
    val unitUtil = em.getEntityManagerFactory.getPersistenceUnitUtil
    unitUtil.isLoaded(entity, propertyName)
  }
}
