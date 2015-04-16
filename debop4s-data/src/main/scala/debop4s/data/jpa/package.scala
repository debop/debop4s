package debop4s.data

import java.util.{Calendar, Collections, Date}
import javax.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import javax.persistence.{EntityManager, LockModeType, TemporalType, TypedQuery}

import com.mysema.query.QueryMetadata
import com.mysema.query.jpa.impl.{JPADeleteClause, JPAQuery, JPAUpdateClause}
import com.mysema.query.types.{EntityPath, OrderSpecifier}
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain._
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support.{JpaEntityInformation, JpaEntityInformationSupport}

import scala.annotation.varargs
import scala.collection.JavaConversions._

/**
 * package
 * @author debop created at 2014. 5. 20.
 */
package object jpa {

  implicit class EntityManagerExtensions(val em: EntityManager) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    val countQueryPlaceholder = "*"
    val DELETE_ALL_QUERY_STRING: String = "delete from %s x"
    val COUNT_QUERY_STRING: String = "select count(%s) from %s x"

    def getEntityInfo[T](entityClass: Class[T]): JpaEntityInformation[T, _] = {
      JpaEntityInformationSupport.getEntityInformation[T](entityClass, em)
    }

    def getQuery[T](resultClass: Class[T], spec: Specification[T], pageable: Pageable): TypedQuery[T] = {
      val sort = if (pageable == null) null else pageable.getSort
      getQuery(resultClass, spec, sort)
    }

    def getQuery[T](resultClass: Class[T], spec: Specification[T], sort: Sort): TypedQuery[T] = {
      val cb = em.getCriteriaBuilder
      val query = cb.createQuery(resultClass)

      val root = em.applySpecificationToCriteria(resultClass, spec, query)
      query.select(root)

      if (sort != null) {
        query.orderBy(QueryUtils.toOrders(sort, root, cb))
      }
      em.createQuery(query)
    }

    def applySpecificationToCriteria[T](resultClass: Class[T],
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

    def delete[T](entity: T) {
      if (entity != null)
        em.remove(if (em.contains(entity)) entity else em.merge(entity))
    }
    def delete(entityClass: Class[_], id: java.io.Serializable) {
      require(id != null)

      val entity = em.find(entityClass, id)
      if (entity == null)
        throw new EmptyResultDataAccessException(s"No [$entityClass] entity with id [$id] exists.", 1)

      delete(entity)
    }

    def deleteInBatch[T](entityClass: Class[T], entities: java.lang.Iterable[T]) {
      require(entities != null)

      for (entity <- entities) {
        val deleteQuery = DELETE_ALL_QUERY_STRING.format(getEntityInfo(entityClass).getEntityName)
        QueryUtils.applyAndBind(deleteQuery, entities, em).executeUpdate()
      }
    }

    def deleteAll[T](entityClass: Class[T]) {
      findAll(entityClass).foreach(delete(_))
    }

    def exists[T](entityClass: Class[T], id: java.io.Serializable): Boolean = {
      log.trace(s"엔티티 존재여부 확인 중... entityClass=$entityClass, id=$id")

      val entityInfo = getEntityInfo(entityClass)
      if (entityInfo != null) {
        val placeHolder = countQueryPlaceholder
        val entityName = entityInfo.getEntityName
        val idAttributeNames = entityInfo.getIdAttributeNames
        val existsQuery = QueryUtils.getExistsQueryString(entityName, placeHolder, idAttributeNames)

        val query = em.createQuery(existsQuery, classOf[java.lang.Long])

        if (entityInfo.hasCompositeId) {
          idAttributeNames.foreach { name =>
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

    def findOne[T](entityClass: Class[T],
                   id: java.io.Serializable,
                   lockMode: LockModeType = LockModeType.NONE): T = {
      em.find(entityClass, id, lockMode)
    }

    def findAll[T](entityClass: Class[T]): java.util.List[T] = {
      getQuery[T](entityClass, null, null.asInstanceOf[Sort]).getResultList
    }

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
      .setParameter("ids", ids.toSeq)
      .getResultList
    }
  }

  implicit class TypedQueryExtensions[T](val query: TypedQuery[T]) {

    private lazy val log = LoggerFactory.getLogger(getClass)

    @varargs
    def setParameters(parameters: JpaParameter*): TypedQuery[T] = {
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

    def setFirstResult(firstResult: Int) = {
      if (firstResult >= 0)
        query.setFirstResult(firstResult)
      query
    }

    def setMaxResults(maxResults: Int) = {
      if (maxResults > 0)
        query.setMaxResults(maxResults)
      query
    }

    def setPaging(firstResult: Int, maxResults: Int) = {
      setFirstResult(firstResult)
      setMaxResults(maxResults)
    }
  }

  implicit class QueryDslExtensions(val em: EntityManager) {

    def jpaQuery: JPAQuery = new JPAQuery(em)

    def jpaQuery(metadata: QueryMetadata): JPAQuery = new JPAQuery(em, metadata)

    def from(path: EntityPath[_]): JPAQuery = jpaQuery.from(path)

    def from(paths: EntityPath[_]*): JPAQuery = jpaQuery.from(paths: _*)

    def deleteFrom(path: EntityPath[_]): JPADeleteClause =
      new JPADeleteClause(em, path)

    def updateFrom(path: EntityPath[_]): JPAUpdateClause =
      new JPAUpdateClause(em, path)
  }

  implicit class JPAQueryExtensions(val query: JPAQuery) {

    def findAll[T <: Comparable[_]](path: EntityPath[T],
                                    orders: OrderSpecifier[T]*): java.util.List[T] = {
      query
      .orderBy(orders: _*)
      .list(path)
    }

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

    def getPage[T <: Comparable[_]](path: EntityPath[T],
                                    pageNo: Int,
                                    pageSize: Int,
                                    orders: OrderSpecifier[T]*): Page[T] = {
      val totalCount = query.count()
      val entities = findAll(path, pageNo * pageSize, pageSize, orders: _*)

      new PageImpl[T](entities, new PageRequest(pageNo, pageSize), totalCount)
    }
  }
}
