package debop4s.data.jpa.repository

import debop4s.data.jpa.utils.JpaUtils
import java.util.Collections
import javax.persistence.criteria.{Predicate, CriteriaBuilder, CriteriaQuery, Root}
import javax.persistence.{EntityManager, PersistenceContext}
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support._
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import scala.collection.JavaConversions
import scala.collection.JavaConversions._

/**
 * JpaDaoHelper
 * Created by debop on 2014. 1. 29..
 */
@Repository
@Transactional(readOnly = true)
class JpaDao {

    private lazy val log = LoggerFactory.getLogger(getClass)

    @PersistenceContext val em: EntityManager = null

    var lockProvider: LockMetadataProvider = null
    protected var countQueryPlaceholder = "*"

    private def persistenceProvider: PersistenceProvider = PersistenceProvider.fromEntityManager(em)

    private
    def getEntityInformation[T](entityClass: Class[T]): JpaEntityInformation[T, _] =
        JpaEntityInformationSupport.getMetadata[T](entityClass, em)

    private def getDeleteAllQueryString[T](entityClass: Class[T]) = {
        JpaUtils.getQueryString(JpaUtils.DELETE_ALL_QUERY_STRING, getEntityInformation(entityClass).getEntityName)
    }

    private def getCountQueryString[T](entityClass: Class[T]) = {
        val query = JpaUtils.COUNT_QUERY_STRING.format(countQueryPlaceholder, "%s")
        JpaUtils.getQueryString(query, getEntityInformation(entityClass).getEntityName)
    }

    @Transactional
    def delete[T](entityClass: Class[T], id: java.io.Serializable) {
        require(id != null)

        val entity = em.find(entityClass, id)
        if (entity == null) {
            val msg = s"No ${ getEntityInformation(entityClass) } entity with id [$id] exists"
            throw new EmptyResultDataAccessException(msg, 1)
        }
        delete(entity)
    }

    @Transactional
    def delete[T](entity: T) {
        require(entity != null)
        em.remove(if (em.contains(entity)) entity else em.merge(entity))
    }

    @Transactional
    def deleteInBatch[T](entityClass: Class[T], entities: Iterable[T]) {
        require(entities != null)

        if (entities.iterator.hasNext) {
            val deleteQuery = QueryUtils.getQueryString(JpaUtils.DELETE_ALL_QUERY_STRING,
                getEntityInformation(entityClass).getEntityName)
            val javaIter = JavaConversions.asJavaIterable(entities)
            QueryUtils.applyAndBind(deleteQuery, javaIter, em).executeUpdate()
        }
    }

    @Transactional
    def deleteAll[T](entityClass: Class[T]) {
        findAll(entityClass).foreach(delete(_))
    }

    @Transactional
    def deleteAllInBatch[T](entityClass: Class[T]) {
        em.createQuery(getDeleteAllQueryString(entityClass)).executeUpdate()
    }

    def exists[T](entityClass: Class[T], id: java.io.Serializable): Boolean = {
        require(id != null)
        log.trace(s"엔티티 존재여부 확인 중... entityClass=[$entityClass], id=[$id]")

        val entityInformation = getEntityInformation(entityClass)

        if (entityInformation != null) {
            val placeHolder = countQueryPlaceholder
            val entityName = entityInformation.getEntityName
            val idAttributeNames = entityInformation.getIdAttributeNames
            val existsQuery = QueryUtils.getExistsQueryString(entityName, placeHolder, idAttributeNames)

            val query = em.createQuery(existsQuery, classOf[java.lang.Long])

            if (entityInformation.hasCompositeId) {
                idAttributeNames.foreach {
                    name =>
                        query.setParameter(name, entityInformation.getCompositeIdAttributeValue(id, name))
                }
            } else {
                query.setParameter(idAttributeNames.iterator().next(), id)
            }
            query.getSingleResult == 1L
        } else {
            findOne(entityClass, id) != null
        }
    }

    def findOne[T](entityClass: Class[T], id: java.io.Serializable): T = {
        require(id != null)
        val lockMode = if (lockProvider == null) null else lockProvider.getLockModeType

        if (lockMode == null) em.find(entityClass, id)
        else em.find(entityClass, id, lockMode)
    }

    def findAll[T](entityClass: Class[T]): java.util.List[T] = {
        JpaUtils.getQuery(em, entityClass, null, null.asInstanceOf[Sort]).getResultList
    }

    def findAll[T](entityClass: Class[T], ids: java.lang.Iterable[_]): java.util.List[T] = {
        if (ids == null || !ids.iterator().hasNext) {
            return Collections.emptyList()
        }

        val spec = new Specification[T]() {
            override
            def toPredicate(root: Root[T], query: CriteriaQuery[_], cb: CriteriaBuilder): Predicate = {
                val path = root.get(getEntityInformation(entityClass).getIdAttribute)
                path.in(cb.parameter(classOf[java.lang.Iterable[_]], "ids"))
            }
        }
        JpaUtils.getQuery(em, entityClass, spec, null.asInstanceOf[Sort])
        .setParameter("ids", ids.toSeq)
        .getResultList

    }

    def persist(entity: AnyRef) {
        em.persist(entity)
    }

    def flush() {
        em.flush()
    }

    def clear() {
        em.clear()
    }
}
