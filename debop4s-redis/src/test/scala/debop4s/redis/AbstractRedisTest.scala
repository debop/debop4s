package debop4s.redis

import akka.actor.ActorSystem
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfter, Matchers, FunSuite }
import redis.RedisClient

/**
 * AbstractRedisTest
 * Created by debop on 2014. 2. 22.
 */
class AbstractRedisTest extends FunSuite with Matchers with BeforeAndAfterAll with BeforeAndAfter {

  implicit var akkaSystem: ActorSystem = _
  var redis: RedisClient = _

  override def beforeAll() {
    akkaSystem = akka.actor.ActorSystem()
    redis = RedisClient()
  }

  override def afterAll() {
    akkaSystem.shutdown()
  }

}
