package org.hibernate.cache.rediscala.tests.region

import java.util
import org.hibernate.cache.rediscala.RedisRegionFactory
import org.hibernate.cfg.{AvailableSettings, Configuration}

/**
 * RedisRegionTest 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
class RedisRegionTest extends AbstractRedisRegionTest {

  override protected def configCache(cfg: Configuration) {
    cfg.setProperty(AvailableSettings.CACHE_REGION_FACTORY, classOf[RedisRegionFactory].getName)
    cfg.setProperty(AvailableSettings.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties")
  }

  override protected def getMapFromCacheEntry(entry: Any): util.Map[Any, Any] = {
    //        val isReadWriteStrategy = entry.getClass.getName.equals(classOf[AbstractReadWriteRedisAccessStrategy].getName + "#Item")
    //
    //        if (isReadWriteStrategy)
    //            ItemValueExtractor.getValue(entry)
    //        else
    entry.asInstanceOf[util.Map[Any, Any]]
  }
}
