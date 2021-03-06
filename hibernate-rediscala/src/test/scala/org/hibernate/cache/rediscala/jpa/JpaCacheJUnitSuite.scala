package org.hibernate.cache.rediscala.jpa

import javax.persistence.{EntityManager, PersistenceContext}

import org.hibernate.cache.rediscala.domain.Item
import org.hibernate.cache.rediscala.jpa.repository.{EventRepository, ItemRepository}
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.scalatest.junit.JUnitSuite
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.transaction.annotation.Transactional


/**
 * NOTE: JPA 관련은 @Transactional 을 수행해야 해서 JUnitSuite를 사용해야 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[JpaRedisConfiguration]), loader = classOf[AnnotationConfigContextLoader])
@Transactional
class JpaCacheJUnitSuite extends JUnitSuite with Matchers with OptionValues with BeforeAndAfterAll {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @PersistenceContext val em: EntityManager = null
  @Autowired val repository: JpaAccountRepository = null
  @Autowired val itemRepository: ItemRepository = null
  @Autowired val eventRepository: EventRepository = null

  override def beforeAll(): Unit = {
    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  @Before
  def before() {
    repository.deleteAll()
    repository.flush()
  }

  @Test
  def configurationTest() {
    em should not be null
    repository should not be null
    eventRepository should not be null
  }

  @Test
  def loadEventByTitle() {
    val events = eventRepository.findByTitle("abc")
    events should not be null
  }

  @Test
  def emptySecondLevelCacheEntry() {
    em.getEntityManagerFactory.getCache.evict(classOf[Item])
  }

  @Test
  def queryCacheInvalidation() {
    val item = new Item()
    item.name = "Widget"
    item.description = "A realy top-quality, full-featured widget."
    em.persist(item)
    em.flush()
    em.clear()

    val loaded = em.find(classOf[Item], item.id)
    loaded should not be null
    loaded.id shouldEqual item.id

    em.clear()

    val loaded2 = em.find(classOf[Item], item.id)
    loaded2 should not be null
    loaded2.id shouldEqual item.id
  }

  @Test
  def simpleEntityCaching() {
    em.getEntityManagerFactory.getCache.evict(classOf[Item])

    log.debug("Item 저장 - #1")

    val item = new Item()
    item.name = "레디스"
    item.description = "레디스 캐시 항목"
    em.persist(item)
    em.flush()
    em.clear()

    log.debug("Item 조회 - #1")
    val loaded = em.find(classOf[Item], item.id)
    loaded should not be null
    loaded.id shouldEqual item.id

    log.debug("Item Update - #1")
    loaded.description = "Update description..."
    em.persist(loaded)
    em.flush()
    em.clear()

    log.debug("Item 조회 - #2")
    val loaded2 = em.find(classOf[Item], item.id)
    loaded2 should not be null
    loaded2.id shouldEqual item.id
    loaded2.description should not be item.description
    em.clear()
  }

  @Test
  def hqlLoad() {
    em.getEntityManagerFactory.getCache.evict(classOf[Item])

    log.debug("Item 저장 - #1")

    val item = new Item()
    item.name = "레디스"
    item.description = "레디스 캐시 항목"
    em.persist(item)
    em.flush()
    em.clear()

    log.debug("Item 조회 - #1")
    val query = em.createQuery("select e from Item e where e.id=:id")
                .setParameter("id", item.id)
                .setHint("org.hibernate.cacheable", true)
    val loaded = query.getSingleResult.asInstanceOf[Item]
    loaded should not be null
    loaded.id shouldEqual item.id
    em.clear()

    log.debug("Item 조회 - #2")
    val query2 = em.createQuery("select e from Item e where e.id=:id")
                 .setParameter("id", item.id)
                 .setHint("org.hibernate.cacheable", true)
    val loaded2 = query2.getSingleResult.asInstanceOf[Item]
    loaded2 should not be null
    loaded2.id shouldEqual item.id
    em.clear()


    log.debug("Item 조회 - #3")
    val loaded3 = em.find(classOf[Item], item.id)
    loaded3 should not be null
    loaded3.id shouldEqual item.id
    em.clear()

    log.debug("Item 조회 - #4")
    val query4 = em.createQuery("select e from Item e where e.id=:id")
                 .setParameter("id", item.id)
                 .setHint("org.hibernate.cacheable", true)
    val loaded4 = query4.getSingleResult.asInstanceOf[Item]
    loaded4 should not be null
    loaded4.id shouldEqual item.id
    em.clear()
  }

  @Test
  def springRepository() {

    em.getEntityManagerFactory.getCache.evict(classOf[Item])

    log.debug("Item 저장 - #1")

    val item = new Item()
    item.name = "redis"
    item.description = "redis cache item"
    em.persist(item)
    em.flush()
    em.clear()

    log.debug("Item 조회 - #1")

    val items = itemRepository.findByName("redis")
    items should not be null
    items.size shouldBe 1

    log.debug("Item 조회 - #2")

    val items2 = itemRepository.findByName("redis")
    items2 should not be null
    items2.size shouldBe 1
  }

}
