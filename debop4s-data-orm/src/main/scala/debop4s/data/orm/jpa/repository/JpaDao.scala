package debop4s.data.orm.jpa.repository

import java.lang.{Iterable => JIterable}
import java.util.{Collection => JCollection, Collections, List => JList}
import javax.persistence.criteria._
import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import debop4s.core.{JFunction, JFunction1}
import debop4s.data.orm.jpa._
import debop4s.data.orm.jpa.utils.JpaUtils
import debop4s.data.orm.model.HibernateEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.provider.PersistenceProvider
import org.springframework.data.jpa.repository.query.QueryUtils
import org.springframework.data.jpa.repository.support._
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Repository
@Transactional
class JpaDao {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val emf: EntityManagerFactory = null
  @PersistenceContext val em: EntityManager = null

  // var lockProvider: LockMetadataProvider = null
  protected var countQueryPlaceholder = "*"

  private def persistenceProvider: PersistenceProvider = PersistenceProvider.fromEntityManager(em)

  private def getEntityInformation[T <: HibernateEntity[_]](entityClass: Class[T]): JpaEntityInformation[T, _] =
    JpaEntityInformationSupport.getEntityInformation(entityClass, em)

  private def getDeleteAllQueryString[T <: HibernateEntity[_]](entityClass: Class[T]) = {
    JpaUtils.getQueryString(JpaUtils.DELETE_ALL_QUERY_STRING,
      getEntityInformation(entityClass).getEntityName)
  }

  private def getCountQueryString[T <: HibernateEntity[_]](entityClass: Class[T]) = {
    val query = JpaUtils.COUNT_QUERY_STRING.format(countQueryPlaceholder, "%s")
    JpaUtils.getQueryString(query, getEntityInformation(entityClass).getEntityName)
  }


  /**
   * 새로운 `EntityManager`를 만들고, Tx 하에서 실행합니다.
   */
  @Transactional
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

  /** Transaction 환경 하에서 작업을 수행합니다. */
  @Transactional
  def withTransaction[T](block: => T): T = block

  /** Transaction 환경 하에서 작업을 수행합니다. */
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
   * 명시적으로 ReadOnly 모드로 코드 블럭을 실행합니다.
   */
  @Transactional(readOnly = true)
  def withReadOnly[T](func: JFunction[T]): T = {
    em.withReadOnly {
      func.execute()
    }
  }

  @Transactional
  def flush() = em.flush()

  @Transactional
  def clear() = em.clear()

  @Transactional
  def save[T <: HibernateEntity[_]](entity: T): T = {
    JpaUtils.save(em, entity)
  }

  @Transactional
  def delete[T <: HibernateEntity[_]](entityClass: Class[T], id: Any) {
    require(id != null)

    val entity = em.find(entityClass, id)
    if (entity == null) {
      val msg = s"No $entityClass entity with id [$id] exists"
      throw new EmptyResultDataAccessException(msg, 1)
    }
    delete(entity)
  }

  @Transactional
  def delete[T <: HibernateEntity[_]](entity: T) {
    if (entity != null && em.contains(entity))
      em.remove(entity)
  }

  @Transactional
  def deleteInBatch[T <: HibernateEntity[_]](entityClass: Class[T], entities: JIterable[T]) {
    require(entities != null)

    if (entities.iterator.hasNext) {
      val deleteQuery = JpaUtils.DELETE_ALL_QUERY_STRING.format(getEntityInformation(entityClass).getEntityName)
      QueryUtils.applyAndBind(deleteQuery, entities, em).executeUpdate()
    }
  }

  @Transactional
  def deleteAll[T <: HibernateEntity[_]](entityClass: Class[T]) {
    findAll(entityClass).asScala.foreach(delete(_))
  }

  @Transactional
  def deleteAllInBatch[T <: HibernateEntity[_]](entityClass: Class[T]) {
    em.createQuery(getDeleteAllQueryString(entityClass)).executeUpdate()
  }

  @Transactional(readOnly = true)
  def exists[T <: HibernateEntity[_]](entityClass: Class[T], id: Serializable): Boolean = {
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
        idAttributeNames.asScala.foreach { name =>
          query.setParameter(name,
            entityInformation.getCompositeIdAttributeValue(id, name))
        }
      } else {
        query.setParameter(idAttributeNames.iterator().next(), id)
      }
      query.getSingleResult == 1L
    } else {
      findOne(entityClass, id) != null
    }
  }

  @Transactional(readOnly = true)
  def findOne[T <: HibernateEntity[_]](entityClass: Class[T], id: Any): T = {
    require(id != null, "id 값이 null 입니다.")
    em.find(entityClass, id)
  }

  @Transactional(readOnly = true)
  def findAll[T <: HibernateEntity[_]](entityClass: Class[T]): JList[T] = {
    JpaUtils.getQuery(em, entityClass, null, null.asInstanceOf[Sort]).getResultList
  }

  @Transactional(readOnly = true)
  def findAll[T <: HibernateEntity[_]](entityClass: Class[T], ids: JIterable[_]): JList[T] = {
    if (ids == null || !ids.iterator().hasNext) {
      return Collections.emptyList()
    }

    val spec = new Specification[T]() {
      override
      def toPredicate(root: Root[T], query: CriteriaQuery[_], cb: CriteriaBuilder): Predicate = {
        val path = root.get(getEntityInformation(entityClass).getIdAttribute)
        path.in(cb.parameter(classOf[JIterable[_]], "ids"))
      }
    }

    JpaUtils
    .getQuery(em, entityClass, spec, null.asInstanceOf[Sort])
    .setParameter("ids", ids)
    .getResultList
  }

  @Transactional
  def detach[T <: HibernateEntity[_]](entity: T): Unit = {
    em.detach(entity)
  }

  @Transactional
  def merge[T <: HibernateEntity[_]](entity: T): T = {
    em.merge(entity)
  }
}
