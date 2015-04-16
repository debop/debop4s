package debop4s.rediscala

import akka.actor.ActorSystem
import debop4s.core.Logging
import debop4s.rediscala.config.RedisConfiguration
import org.scalatest._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import redis.RedisClient

/**
 * AbstractRedisTest
 * Created by debop on 2014. 2. 22.
 */
@ContextConfiguration(classes = Array(classOf[RedisConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class AbstractRedisFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfterAll with BeforeAndAfter with Logging {

  implicit var actorSystem: ActorSystem = null

  @Autowired val redis: RedisClient = null

  override def beforeAll(): Unit = {
    actorSystem = ActorSystem("rediscala")

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  override def afterAll(): Unit = {
    if (actorSystem != null)
      actorSystem.shutdown()
  }

}
