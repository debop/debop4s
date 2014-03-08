package org.hibernate.cache.redis.tests.hibernate

import org.hibernate.SessionFactory
import org.hibernate.cache.redis.HibernateRedisUtil
import org.hibernate.cache.redis.tests.HibernateRedisConfiguration
import org.hibernate.cache.redis.tests.domain.{Person, Account, Item}
import org.hibernate.stat.SecondLevelCacheStatistics
import org.junit.runner.RunWith
import org.junit.{Test, Before}
import org.scalatest.junit.JUnitSuite
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}
import scala.collection.JavaConversions._

/**
 * HibernateCacheTest
 * Created by debop on 2014. 2. 26.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[HibernateRedisConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class HibernateCacheTest extends JUnitSuite {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val sessionFactory: SessionFactory = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Before
  def before() {
    sessionFactory.getStatistics.setStatisticsEnabled(true)
    sessionFactory.getStatistics.clear()
  }

  private def getSecondLevelCacheStatistics(entityClass: Class[_]): SecondLevelCacheStatistics = {
    val regionName = HibernateRedisUtil.getRegionName(sessionFactory, entityClass)
    sessionFactory.getStatistics.getSecondLevelCacheStatistics(regionName)
  }

  @Test
  def emptySecondLevelCacheEntry() {
    sessionFactory.getCache.evictEntityRegion(classOf[Item])
    val stats = sessionFactory.getStatistics
    stats.clear()

    val regionName = HibernateRedisUtil.getRegionName(sessionFactory, classOf[Item])
    val statistics = getSecondLevelCacheStatistics(classOf[Item])
    log.info(s"SecondLevel Cache region=$regionName, " +
             s"elementInMemory=${statistics.getElementCountInMemory }, hitCount=${statistics.getHitCount }")
  }

  @Test
  def queryCacheInvalidation() {

    sessionFactory.getCache.evictEntityRegion(classOf[Item])

    val session = sessionFactory.openSession()
    val tx = session.beginTransaction()
    val item = new Item()
    item.name = "Widget"
    item.description = "A realy top-quality, full-featured widget."

    session.save(item)
    tx.commit()
    session.close()

    Thread.sleep(100)

    log.info(getSecondLevelCacheStatistics(classOf[Item]).toString)

    val session2 = sessionFactory.openSession()
    val loaded = session2.get(classOf[Item], item.id).asInstanceOf[Item]
    assert(loaded != null)
    assert(loaded.id == item.id)

    val loaded2 = session2.get(classOf[Item], item.id).asInstanceOf[Item]
    assert(loaded2 != null)
    assert(loaded2.id == item.id)

    session2.close()

    val slcs = getSecondLevelCacheStatistics(classOf[Item])
    log.info(slcs.toString)
    assert(slcs.getPutCount == 1)
    assert(slcs.getElementCountInMemory == 1)
  }

  @Test
  def simpleEntityCaching() {

    sessionFactory.getCache.evictEntityRegion(classOf[Item])

    val session = sessionFactory.openSession()

    log.debug("Item 저장 - #1")

    val item = new Item()
    item.name = "레디스"
    item.description = "레디스 캐시 항목"
    session.save(item)
    session.flush()
    session.clear()

    log.debug("Item 조회 - #1")
    val loaded = session.get(classOf[Item], item.id).asInstanceOf[Item]
    assert(loaded != null)
    assert(loaded.id == item.id)

    log.debug("Item Update - #1")
    loaded.description = "Update description..."
    session.save(loaded)
    session.flush()
    session.clear()

    log.debug("Item 조회 - #2")
    val loaded2 = session.get(classOf[Item], item.id).asInstanceOf[Item]
    assert(loaded2 != null)
    assert(loaded2.id == item.id)
    assert(loaded2.description != item.description)

    session.close()
  }

  @Test
  def hqlLoad() {
    sessionFactory.getCache.evictEntityRegion(classOf[Item])
    val session = sessionFactory.openSession()

    log.debug("Item 저장 - #1")

    val item = new Item()
    item.name = "레디스"
    item.description = "레디스 캐시 항목"
    session.save(item)
    session.flush()
    session.clear()

    log.debug("Item 조회 - #1")
    val query = session.createQuery("select e from Item e where e.id=:id")
      .setParameter("id", item.id)
      .setCacheable(true)
    val loaded = query.uniqueResult().asInstanceOf[Item]
    assert(loaded != null)
    assert(loaded.id == item.id)
    session.clear()

    log.debug("Item 조회 - #2")
    val query2 = session.createQuery("select e from Item e where e.id=:id")
      .setParameter("id", item.id)
      .setCacheable(true)
    val loaded2 = query2.uniqueResult().asInstanceOf[Item]
    assert(loaded2 != null)
    assert(loaded2.id == item.id)
    session.clear()


    log.debug("Item 조회 - #3")
    val loaded3 = session.get(classOf[Item], item.id).asInstanceOf[Item]
    assert(loaded3 != null)
    assert(loaded3.id == item.id)
    session.clear()

    log.debug("Item 조회 - #4")
    val query4 = session.createQuery("select e from Item e where e.id=:id")
      .setParameter("id", item.id)
      .setCacheable(true)
    val loaded4 = query4.uniqueResult().asInstanceOf[Item]
    assert(loaded4 != null)
    assert(loaded4.id == item.id)

    session.close()
  }

  @Test
  def nonrestrictCaching() {
    sessionFactory.getCache.evictEntityRegion(classOf[Account])
    sessionFactory.getCache.evictEntityRegion(classOf[Person])

    val slcs = getSecondLevelCacheStatistics(classOf[Account])
    val slcs2 = getSecondLevelCacheStatistics(classOf[Person])

    val session = sessionFactory.openSession()

    val person = new Person()
    person.age = Some(47)
    person.lastName = "Bae"
    person.firstName = "Sunghyouk"

    session.saveOrUpdate(person)

    val account = new Account()
    account.person = person

    session.saveOrUpdate(account)

    session.flush()
    session.clear()

    val session2 = sessionFactory.openSession()
    val acc2 = session2.get(classOf[Account], account.id).asInstanceOf[Account]
    assert(acc2 != null)
    assert(acc2.id == account.id)
    assert(acc2.getPerson.id == person.id)
    session2.close()

    val session3 = sessionFactory.openSession()
    val acc3 = session3.get(classOf[Account], account.id).asInstanceOf[Account]
    assert(acc3 != null)
    assert(acc3.id == account.id)
    assert(acc3.getPerson.id == person.id)
    session3.close()

    log.info("Account: " + slcs.toString)
    log.info("Person: " + slcs2.toString)

    assert(slcs.getPutCount == 1)
    assert(slcs.getElementCountInMemory == 1)

    assert(slcs2.getPutCount == 1)
    assert(slcs2.getElementCountInMemory == 1)
  }

  @Test
  def massiveCaching() {
    sessionFactory.getCache.evictEntityRegion(classOf[Item])

    val session = sessionFactory.openSession()
    val tx = session.beginTransaction()

    val count = 100
    for (i <- 0 until 100) {
      val item = new Item()
      item.name = "client-" + i
      item.description = "client cache item - " + i
      session.save(item)
    }
    tx.commit()
    session.close()

    val session2 = sessionFactory.openSession()
    val tx2 = session2.beginTransaction()
    val items2 = session2.createCriteria(classOf[Item]).list().asInstanceOf[java.util.List[Item]]

    items2.foreach(session2.update(_))

    tx2.commit()
    session2.close()

    val slcs = getSecondLevelCacheStatistics(classOf[Item])
    log.info(slcs.toString)
    assert(slcs.getPutCount == count)
    assert(slcs.getElementCountInMemory == count)

    val session3 = sessionFactory.openSession()
    val tx3 = session3.beginTransaction()
    val items3 = session3.createCriteria(classOf[Item]).list().asInstanceOf[java.util.List[Item]]

    items3.foreach(item => log.trace(item.name))

    tx3.commit()
    session3.close()

    val session4 = sessionFactory.openSession()
    val tx4 = session4.beginTransaction()
    val items4 = session4.createCriteria(classOf[Item]).list().asInstanceOf[java.util.List[Item]]

    items4.foreach(session4.delete(_))

    tx4.commit()
    session4.close()

  }

}
