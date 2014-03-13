package org.hibernate.cache.rediscala.tests.region

import org.hibernate.cache.rediscala.HibernateRedisUtil
import org.hibernate.cache.rediscala.strategy.AbstractReadWriteRedisAccessStrategy
import org.hibernate.cache.rediscala.tests.domain.{Item, VersionedItem}
import org.hibernate.cfg.{AvailableSettings, Configuration}
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase
import org.junit.{Ignore, Assert, Test}
import org.slf4j.LoggerFactory

/**
 * AbstractRedisRegionTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
abstract class AbstractRedisRegionTest extends BaseCoreFunctionalTestCase {

    private lazy val log = LoggerFactory.getLogger(getClass)

    override def getAnnotatedClasses: Array[Class[_]] = {
        Array[Class[_]](classOf[Item], classOf[VersionedItem])
    }

    override def getCacheConcurrencyStrategy: String = "read-write"

    override def configure(cfg: Configuration) {
        super.configure(cfg)
        cfg.setProperty(AvailableSettings.HBM2DDL_AUTO, "create")
        cfg.setProperty(AvailableSettings.CACHE_REGION_PREFIX, "hibernate")
        cfg.setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "true")
        cfg.setProperty(AvailableSettings.GENERATE_STATISTICS, "true")
        cfg.setProperty(AvailableSettings.USE_STRUCTURED_CACHE, "true")
        cfg.setProperty(AvailableSettings.TRANSACTION_STRATEGY, classOf[JdbcTransactionFactory].getName)

        configCache(cfg)
    }

    protected def configCache(cfg: Configuration)

    protected def getMapFromCacheEntry(entry: Any): java.util.Map[Any, Any]

    @Test
    def queryCacheInvalidation() {
        val s = openSession()
        val t = s.beginTransaction()
        val item = new Item()
        item.name = "widget"
        item.description = "A really top-quality, full-featured widget. 동해물과 백두산이~"
        s.persist(item)
        t.commit()
        s.clear()

        val regionName = HibernateRedisUtil.getRegionName(sessionFactory(), classOf[Item])
        val slcs = sessionFactory().getStatistics.getSecondLevelCacheStatistics(regionName)

        assert(slcs.getElementCountInMemory == 1)

        val s2 = openSession()
        val t2 = s2.beginTransaction()
        val item2 = s2.get(classOf[Item], item.id).asInstanceOf[Item]

        assert(item2 != null)
        assert(item2.name == item.name)

        item2.description = "A blog standard item"

        t2.commit()
        s2.close()

        val s3 = openSession()
        val t3 = s3.beginTransaction()
        s3.delete(item2)
        t3.commit()
        s3.close()
    }

    @Test
    def emptySecondLevelCacheEntry() {
        //val regionName= HibernateRedisUtil.getRegionName(sessionFactory(), classOf[Item])
        sessionFactory().getCache.evictEntityRegion(classOf[Item])

        val stats = sessionFactory().getStatistics
        stats.clear()

        val regionName = HibernateRedisUtil.getRegionName(sessionFactory(), classOf[Item])
        val slcs = stats.getSecondLevelCacheStatistics(regionName)
        val cacheEntries = slcs.getEntries

        assert(cacheEntries != null)
        assert(cacheEntries.size() == 0)
    }

    @Test
    @Ignore("Not use CacheKey, cannot use SecondLevelCacheStatistics")
    def staleWritesLeaveCacheConsistent() {
        val s = openSession()
        val tx = s.beginTransaction()

        val item = new VersionedItem()
        item.name = "steve"
        item.description = "steve's name"
        s.save(item)
        tx.commit()
        s.close()

        val initialVersion = item.getVersion

        item.version = item.version - 1L

        val s2 = openSession()
        val tx2 = s2.beginTransaction()
        try {
            s2.update(item)
            tx2.commit()
            s2.close()
            Assert.fail("expected stale write to fail")
        } catch {
            case expected: Throwable => if (tx2 != null) tx2.rollback()
        } finally {
            if (s2 != null && s2.isOpen) {
                s2.close()
            }
        }

        val regionName = HibernateRedisUtil.getRegionName(sessionFactory(), classOf[VersionedItem])
        val slcs = sessionFactory().getStatistics.getSecondLevelCacheStatistics(regionName)

        val cacheEntries = slcs.getEntries
        val entry = cacheEntries.get(item.id)

        log.debug(s"entry=$entry")

        var cachedVersionValue: Long = 0
        val lockStr = classOf[AbstractReadWriteRedisAccessStrategy[_]].getName + "#Lock"
        val isLock = entry.getClass.getName.equals(lockStr)

        if (isLock) {
            // nothing to do
        } else {
            cachedVersionValue = getMapFromCacheEntry(entry).get("_version").toString.toLong
            assert(cachedVersionValue == initialVersion)
        }

        // clean up
        val s3 = openSession()
        val tx3 = s3.beginTransaction()
        val item3 = s3.load(classOf[VersionedItem], item.id)
        s3.delete(item3)
        tx3.commit()
        s3.close
    }
}
