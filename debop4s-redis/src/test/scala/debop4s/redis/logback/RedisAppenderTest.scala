package debop4s.redis.logback

import debop4s.core.parallels.Parallels
import debop4s.redis.AbstractRedisTest
import org.slf4j.LoggerFactory

/**
 * RedisAppenderTest
 * Created by debop on 2014. 2. 22.
 */
class RedisAppenderTest extends AbstractRedisTest {

  lazy val log = LoggerFactory.getLogger(getClass)

  test("logging message") {
    Parallels.runAction(10) {
      (0 until 100).foreach(x => log.trace(s"appender test [$x]"))
    }
  }

  test("clear logs") {
    redis.del(RedisAppender.DEFAULT_KEY)
    println("delete all logs")
  }
}
