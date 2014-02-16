package kr.debop4s.core.stests.pool

import kr.debop4s.core.pool.AbstractObjectPoolConfig

/**
 * kr.debop4s.core.tests.pool.ObjectPoolConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:43
 */
class ObjectPoolConfig extends AbstractObjectPoolConfig {

  testWhileIdle = true
  minEvictableIdleTimeMillis = 60000
  timeBetweenEvictionRunsMillis = 30000
  numTestsPerEvictionRun = -1
}
