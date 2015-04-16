package debop4s.rediscala.logback

import debop4s.rediscala.AbstractRedisFunSuite


/**
 * RedisAppenderTest
 * Created by debop on 2014. 2. 22.
 */
class RedisAppenderFunSuite extends AbstractRedisFunSuite {

  test("logging message") {
    (0 until 10).par.foreach { _ =>
      var i = 0
      while (i < 100) {
        log.trace(s"appender test [$i]")
        i += 1
      }
    }
  }

  test("clear logs") {
    redis.del(RedisAppender.DEFAULT_KEY)
    println("delete all logs")
  }
}
