package org.hibernate.cache.rediscala

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * org.hibernate.cache.rediscala.tests.AbstractRedisTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 5:18
 */
class AbstractHibernateRedisFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfter with BeforeAndAfterAll {
  protected lazy val log = LoggerFactory.getLogger(getClass)
}
