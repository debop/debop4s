package org.hibernate.cache.redis.tests.jpa

import javax.persistence.{EntityManager, PersistenceContext}
import org.hibernate.cache.redis.tests.AbstractHibernateRedisTest
import org.hibernate.cache.redis.tests.jpa.repository.{EventRepository, ItemRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * JpaCacheTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
@ContextConfiguration(classes = Array(classOf[JpaRedisConfiguration]),
                         loader = classOf[AnnotationConfigContextLoader])
class JpaCacheTest extends AbstractHibernateRedisTest {

    @PersistenceContext var em: EntityManager = _
    @Autowired val repository: JpaAccountRepository = null
    @Autowired val itemRepository: ItemRepository = null
    @Autowired val eventRepository: EventRepository = null

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    before {
        repository.deleteAll()
        repository.flush()
        em.clear()
        em.getEntityManagerFactory.getCache.evict(classOf[JpaAccount])
    }

    test("configuration") {
        em should not equal null
        repository should not equal null
        eventRepository should not equal null
    }

}
