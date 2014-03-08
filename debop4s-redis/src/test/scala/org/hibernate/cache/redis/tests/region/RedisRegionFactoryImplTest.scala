package org.hibernate.cache.redis.tests.region

import java.util
import org.hibernate.cache.redis.RedisRegionFactory
import org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy
import org.hibernate.cfg.{AvailableSettings, Configuration}

/**
 * RedisRegionFactoryImplTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
class RedisRegionFactoryImplTest extends AbstractRedisRegionTest {

  val ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME =
    classOf[AbstractReadWriteRedisAccessStrategy[_]].getName + "#Item"

  override protected def configCache(cfg: Configuration) {
    cfg.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[RedisRegionFactory].getName)
    cfg.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")
  }

  override protected def getMapFromCacheEntry(entry: Any): util.Map[Any, Any] = {
    val isReadWriteStrategy = entry.getClass.getName.equals(ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME)

    if (isReadWriteStrategy) {
      val field = entry.getClass.getDeclaredField("value")
      field.setAccessible(true)
      field.get(entry).asInstanceOf[util.Map[Any, Any]]
    }
    else {
      entry.asInstanceOf[util.Map[Any, Any]]
    }
  }
}
