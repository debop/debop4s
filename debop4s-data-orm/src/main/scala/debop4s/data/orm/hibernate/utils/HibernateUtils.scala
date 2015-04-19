package debop4s.data.orm.hibernate.utils

import java.lang.{Iterable => JIterable}
import java.util
import java.util.{Collection => JCollection, Map => JMap}

import debop4s.core.io.Serializers
import debop4s.data.orm.hibernate.HibernateParameter
import debop4s.data.orm.hibernate.listener.UpdatedTimestampListener
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.criterion.{Criterion, DetachedCriteria, Order}
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.internal.{CriteriaImpl, SessionFactoryImpl}
import org.hibernate.{Criteria, Query, Session, SessionFactory}
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable

import scala.annotation.varargs
import scala.collection.JavaConverters._

/**
 * HibernateUtils
 * @author Sunghyouk Bae
 */
object HibernateUtils {

  private lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * Hibernate 4 이상에서 [[org.hibernate.cfg.Configuration]]을 이용하여 [[org.hibernate.SessionFactory]]를 빌드합니다.
   *
   * @param cfg Hibernate 4 용 환경설정
   * @return    [[org.hibernate.SessionFactory]] 인스턴스
   */
  def buildSessionFactory(cfg: Configuration): SessionFactory = {
    require(cfg != null)

    log.info("SessionFactory를 빌드합니다...")

    // hibernate 4.2.x
    //        val registryBuilder = new ServiceRegistryBuilder().applySettings(cfg.getProperties)
    //        val factory = cfg.buildSessionFactory(registryBuilder.buildServiceRegistry)

    // hibernate 4.3.x
    val registryBuilder = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties)
    val factory = cfg.buildSessionFactory(registryBuilder.build())

    log.info("SessionFactory를 빌드했습니다.")
    factory
  }
  /**
   * Hibernate SessionFactory에 event listener 를 등록합니다.
   *
   * @param sessionFactory SessionFactory instance
   * @param listener       Listener instance
   * @param eventTypes     리스닝할 이벤트 종류
   * @tparam T Listener 수형
   */
  @SafeVarargs
  @varargs
  def registerEventListener[T <: Serializable](sessionFactory: SessionFactory,
                                               listener: T,
                                               eventTypes: EventType[T]*) {
    val registry = sessionFactory.asInstanceOf[SessionFactoryImpl]
                   .getServiceRegistry
                   .getService(classOf[EventListenerRegistry])

    for (eventType <- eventTypes) {
      registry.getEventListenerGroup(eventType).appendListener(listener)
    }
  }
  /**
   * Hibernate SessionFactory에 event listener 를 등록합니다.
   *
   * @param sessionFactory SessionFactory instance
   * @param listener       Listener instance
   * @param eventTypes     리스닝할 이벤트 종류
   * @tparam T Listener 수형
   */
  def registerEventListener[T <: Serializable](sessionFactory: SessionFactory,
                                               listener: T,
                                               eventTypes: Iterable[EventType[T]]) {
    val registry = sessionFactory.asInstanceOf[SessionFactoryImpl]
                   .getServiceRegistry
                   .getService(classOf[EventListenerRegistry])

    for (eventType <- eventTypes) {
      registry.getEventListenerGroup(eventType).appendListener(listener)
    }
  }
  /**
   * SessionFactory 에 [[UpdatedTimestampListener]] 를 추가합니다.
   *
   * @param sessionFactory SessionFactory instance
   */
  def registerUpdateTimestampEventListener(sessionFactory: SessionFactory) {
    val listener = new UpdatedTimestampListener()
    val registry = sessionFactory.asInstanceOf[SessionFactoryImpl]
                   .getServiceRegistry
                   .getService(classOf[EventListenerRegistry])

    registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(listener)
    registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(listener)
  }
  /**
   * [[HibernateParameter]] 배열을 [[java.util.Map]] 으로 변환합니다.
   *
   * @param parameters Hibernate Parameters
   */
  @varargs
  def toMap(parameters: HibernateParameter*): JMap[String, Any] = {
    val map = new util.HashMap[String, Any]

    val iter = parameters.iterator
    while (iter.hasNext) {
      val p = iter.next()
      map.put(p.name, p.value)
    }
    map
  }

  def createDetachedCriteria(clazz: Class[_]): DetachedCriteria = {
    DetachedCriteria.forClass(clazz)
  }

  @varargs
  def createCriteria(clazz: Class[_],
                     session: Session,
                     orders: JIterable[Order],
                     criterions: Criterion*): Criteria = {
    val crit = session.createCriteria(clazz)
    addOrders(crit, orders.asScala.toSeq: _*)
    addCriterions(crit, criterions: _*)
    crit
  }

  def copyDetachedCriteria(src: DetachedCriteria): DetachedCriteria =
    Serializers.copyObject(src)

  def copyCriteria(src: Criteria): Criteria =
    Serializers.copyObject(src.asInstanceOf[CriteriaImpl]).asInstanceOf[CriteriaImpl]

  def getExecutableCriteria(session: Session, dc: DetachedCriteria): Criteria =
    dc.getExecutableCriteria(session)

  @varargs
  def getExecutableCriteria(dc: DetachedCriteria, session: Session, orders: Order*): Criteria = {
    val criteria = getExecutableCriteria(session, dc)
    orders.foreach(criteria.addOrder)
    criteria
  }
  def getExecutableCriteria(dc: DetachedCriteria, session: Session, orders: JIterable[Order]): Criteria = {
    val criteria = getExecutableCriteria(session, dc)
    orders.asScala.foreach(criteria.addOrder)
    criteria
  }

  @varargs
  def addOrders(dc: DetachedCriteria, orders: Order*): DetachedCriteria = {
    orders.foreach(dc.addOrder)
    dc
  }
  def addOrders(dc: DetachedCriteria, orders: JIterable[Order]): DetachedCriteria = {
    orders.asScala.foreach(dc.addOrder)
    dc
  }

  @varargs
  def addOrders(criteria: Criteria, orders: Order*): Criteria = {
    orders.foreach(criteria.addOrder)
    criteria
  }

  def addOrders(criteria: Criteria, orders: JIterable[Order]): Criteria = {
    orders.asScala.foreach(criteria.addOrder)
    criteria
  }

  @varargs
  def addCriterions(criteria: Criteria, criterions: Criterion*): Criteria = {
    criterions.foreach(criteria.add)
    criteria
  }
  def addCriterions(criteria: Criteria, criterions: JIterable[Criterion]): Criteria = {
    val it = criterions.iterator()
    while (it.hasNext) {
      criteria.add(it.next)
    }
    criteria
  }

  @varargs
  def setParameters(query: Query, parameters: HibernateParameter*): Query = {
    val iter = parameters.iterator
    while (iter.hasNext) {
      val p = iter.next()
      query.setParameter(p.name, p.value)
    }
    query
  }

  def setParameters(query: Query, parameters: JIterable[HibernateParameter]): Query = {
    val iter = parameters.iterator
    while (iter.hasNext) {
      val p = iter.next()
      query.setParameter(p.name, p.value)
    }
    query
  }

  def setFirstResult(criteria: Criteria, firstResult: Int): Criteria = {
    if (firstResult >= 0) criteria.setFirstResult(firstResult)
    criteria
  }
  def setFirstResult(query: Query, firstResult: Int): Query = {
    if (firstResult >= 0) query.setFirstResult(firstResult)
    query
  }
  def setMaxResults(criteria: Criteria, maxResults: Int): Criteria = {
    if (maxResults > 0) criteria.setMaxResults(maxResults)
    criteria
  }
  def setMaxResults(query: Query, maxResults: Int): Query = {
    if (maxResults > 0) query.setMaxResults(maxResults)
    query
  }

  def setPaging(criteria: Criteria, pageable: Pageable): Criteria =
    setPaging(criteria, pageable.getPageNumber * pageable.getPageSize, pageable.getPageSize)

  def setPaging(criteria: Criteria, firstResult: Int, maxResults: Int): Criteria = {
    setFirstResult(criteria, firstResult)
    setMaxResults(criteria, maxResults)
  }

  def setPaging(query: Query, pageable: Pageable): Query =
    setPaging(query, pageable.getPageNumber * pageable.getPageSize, pageable.getPageSize)

  def setPaging(query: Query, firstResult: Int, maxResults: Int): Query = {
    setFirstResult(query, firstResult)
    setMaxResults(query, maxResults)
  }
}
