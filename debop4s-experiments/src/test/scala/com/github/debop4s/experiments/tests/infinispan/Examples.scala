package com.github.debop4s.experiments.tests.infinispan

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import org.infinispan.configuration.cache._
import org.infinispan.eviction.EvictionStrategy
import org.infinispan.manager.DefaultCacheManager

/**
 * Examples
 * Created by debop on 2014. 3. 15.
 */
class Examples extends AbstractExperimentTest {

    var m: DefaultCacheManager = _

    before {
        m = new DefaultCacheManager()
    }
    after {
        m.stop()
    }

    test("Local Cache") {
        val cache = m.getCache[Any, Any]()

        val oldValue = cache.put("Hello", "World")
        val worked = cache.replace("Hello", "World", "Mars")

        assert(oldValue == null)
        assert(worked)
    }

    test("환경 설정 테스트") {
        val cfg = new ConfigurationBuilder()
                  .eviction().maxEntries(20000).strategy(EvictionStrategy.LIRS).expiration()
                  .wakeUpInterval(5000L)
                  .maxIdle(120000L)
                  .build

        m.defineConfiguration("cluster", cfg)
        val cache = m.getCache[String, String]("cluster")

        val oldValue = cache.put("Hello", "World")
        val worked = cache.replace("Hello", "World", "Mars")

        assert(oldValue == null)
        assert(worked)
    }

}
