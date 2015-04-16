package debop4s.rediscala.client

import debop4s.core._
import debop4s.rediscala.config.RedisConfiguration
import debop4s.rediscala.serializer.SnappyFstValueFormatter
import org.scalatest.{FunSuite, Matchers, OptionValues}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import redis.RedisClient

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * PerformanceFunSuite
 * @author sunghyouk.bae@gmail.com 2014. 9. 4.
 */
@ContextConfiguration(classes = Array(classOf[RedisConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class PerformanceFunSuite extends FunSuite with Matchers with OptionValues {

  lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val redis: RedisClient = null
  @Autowired val syncRedis: RedisSyncClient = null

  // NOTE: Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  implicit val valueFormatter = new SnappyFstValueFormatter[Any]()

  val RUN_COUNT = 100000

  test("RedisClient Multithread set") {

    {
      val tasks = (0 until RUN_COUNT).par.map { x =>
        async {
          redis.set(s"key-$x", s"value-$x")
        }
      }.seq
      tasks.awaitAll
      log.debug("쓰기 완료")
    }
    {
      val tasks = (0 until RUN_COUNT).par.map { x =>
        async {
          val value = await(redis.get[String](s"key-$x"))
          value.orNull shouldEqual s"value-$x"
        }
      }.seq
      tasks.awaitAll
      log.debug("읽기 완료")
    }

    {
      val tasks = (0 until RUN_COUNT).par.map { x =>
        async {
          await(redis.del(s"key-$x"))
        }
      }.seq
      tasks.awaitAll
    }

    log.debug("삭제 완료 완료")

    {
      val tasks = (0 until RUN_COUNT).par.map { x =>
        async {
          val value = await(redis.get[String](s"key-$x"))
          value.orNull shouldEqual s"value-$x"
        }
      }.seq
      tasks.await
    }
    log.debug("삭제 확인 완료")
  }

  test("SyncClient Multithread set") {
    (0 until RUN_COUNT).par.foreach { x =>
      syncRedis.set(s"key-$x", s"value-$x")
    }

    log.debug("쓰기 완료")

    (0 until RUN_COUNT).par.foreach { x =>
      syncRedis.get[String](s"key-$x").getOrElse("") shouldEqual s"value-$x"
    }

    log.debug("읽기 완료")

    (0 until RUN_COUNT).par.foreach { x =>
      syncRedis.del(s"key-$x")
    }

    log.debug("삭제 완료 완료")

    (0 until RUN_COUNT).par.foreach { x =>
      syncRedis.get[String](s"key-$x") should not be defined
    }
    log.debug("삭제 확인 완료")
  }

}
