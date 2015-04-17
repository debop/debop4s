package org.hibernate.cache.rediscala.hibernate

import java.util.{List => JList}

import org.hibernate.SessionFactory
import org.hibernate.cache.rediscala.domain.{Account, Item, Person}
import org.hibernate.cache.rediscala.utils.RedisCacheUtil
import org.hibernate.stat.SecondLevelCacheStatistics
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.{Matchers, OptionValues}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[HibernateRedisConfiguration]), loader = classOf[AnnotationConfigContextLoader])
@Transactional
class HibernateCacheJUnitSuite extends JUnitSuite with Matchers with OptionValues {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val sessionFactory: SessionFactory = null

  // 테스트 시에 Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  @Before
  def before() {
    sessionFactory.getStatistics.setStatisticsEnabled(true)
    sessionFactory.getStatistics.clear()
  }

  private def getSecondLevelCacheStatistics(entityClass: Class[_]): SecondLevelCacheStatistics = {
    val regionName = RedisCacheUtil.getRegionName(sessionFactory, entityClass)
    sessionFactory.getStatistics.getSecondLevelCacheStatistics(regionName)
  }

  @Test
  def emptySecondLevelCacheEntry() {
    sessionFactory.getCache.evictEntityRegion(classOf[Item])
    val stats = sessionFactory.getStatistics
    stats.clear()

    val regionName = RedisCacheUtil.getRegionName(sessionFactory, classOf[Item])
    val statistics = getSecondLevelCacheStatistics(classOf[Item])
    log.info(s"SecondLevel Cache region=$regionName, " +
             s"elementInMemory=${ statistics.getElementCountInMemory }, hitCount=${ statistics.getHitCount }")
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
    slcs.getPutCount shouldEqual 1
    slcs.getElementCountInMemory shouldEqual 1
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
    loaded2 should not be null
    loaded2.id shouldEqual item.id
    loaded2.description should not be item.description

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
    loaded should not be null
    loaded.id shouldEqual item.id
    session.clear()

    log.debug("Item 조회 - #2")
    val query2 = session.createQuery("select e from Item e where e.id=:id")
                 .setParameter("id", item.id)
                 .setCacheable(true)
    val loaded2 = query2.uniqueResult().asInstanceOf[Item]
    loaded2 should not be null
    loaded2.id shouldEqual item.id
    session.clear()


    log.debug("Item 조회 - #3")
    val loaded3 = session.get(classOf[Item], item.id).asInstanceOf[Item]
    loaded3 should not be null
    loaded3.id shouldEqual item.id

    session.clear()

    log.debug("Item 조회 - #4")
    val query4 = session.createQuery("select e from Item e where e.id=:id")
                 .setParameter("id", item.id)
                 .setCacheable(true)
    val loaded4 = query4.uniqueResult().asInstanceOf[Item]
    loaded4 should not be null
    loaded4.id shouldEqual item.id

    session.close()
  }

  @Test
  def nonrestrictCaching() {

    sessionFactory.getCache.evictEntityRegion(classOf[Account])
    sessionFactory.getCache.evictEntityRegion(classOf[Person])

    val session = sessionFactory.openSession()

    val person = new Person()
    person.age = 47
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
    acc2 should not be null
    acc2.id shouldEqual account.id
    acc2.person.id shouldEqual person.id

    session2.close()

    val session3 = sessionFactory.openSession()
    val acc3 = session3.get(classOf[Account], account.id).asInstanceOf[Account]
    acc3 should not be null
    acc3.id shouldEqual account.id
    acc3.person.id shouldEqual person.id
    session3.close()

    Thread.sleep(100)

    val slcs = getSecondLevelCacheStatistics(classOf[Account])
    val slcs2 = getSecondLevelCacheStatistics(classOf[Person])

    log.info("Account: " + slcs.toString)
    log.info("Person: " + slcs2.toString)
    println(slcs)
    println(slcs2)

    slcs.getPutCount shouldEqual 1
    slcs.getElementCountInMemory shouldEqual 1

    slcs2.getPutCount shouldEqual 1
    slcs2.getElementCountInMemory shouldEqual 1
  }

  @Test
  def massiveCaching() {
    sessionFactory.getCache.evictEntityRegion(classOf[Item])

    var session = sessionFactory.openSession()
    var tx = session.beginTransaction()

    val count = 100
    var i = 0
    while (i < 100) {
      val item = new Item()
      item.name = "client-" + i
      item.description = "client cache item - " + i
      session.save(item)
      i += 1
    }
    tx.commit()
    session.close()

    session = sessionFactory.openSession()
    tx = session.beginTransaction()
    var items = session.createCriteria(classOf[Item]).list().asInstanceOf[JList[Item]]

    items.asScala.foreach(session.update(_))

    tx.commit()
    session.close()

    val slcs = getSecondLevelCacheStatistics(classOf[Item])
    log.info(slcs.toString)
    slcs.getPutCount shouldEqual count
    slcs.getElementCountInMemory shouldEqual count

    session = sessionFactory.openSession()
    tx = session.beginTransaction()
    items = session.createCriteria(classOf[Item]).list().asInstanceOf[JList[Item]]

    items.asScala.foreach(item => log.trace(item.name))

    tx.commit()
    session.close()

    val session4 = sessionFactory.openSession()
    val tx4 = session4.beginTransaction()
    val items4 = session4.createCriteria(classOf[Item]).list().asInstanceOf[JList[Item]]

    items4.asScala.foreach(session4.delete(_))

    tx4.commit()
    session4.close()

  }

}
