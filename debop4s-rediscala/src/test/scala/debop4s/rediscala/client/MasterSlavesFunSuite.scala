package debop4s.rediscala.client

import debop4s.core._
import debop4s.rediscala.AbstractRedisFunSuite
import redis.{RedisClientMasterSlaves, RedisServer}

/**
 * Redis 서버가 Master/Slave 로 구성되어 있을 시에 Master 에 쓰고, Slave에서 읽습니다.

 * @author sunghyouk.bae@gmail.com 2014. 7. 29.
 */
//@Ignore
class MasterSlavesFunSuite extends AbstractRedisFunSuite {

  // Master 는 127.0.0.1 6379
  // Slave 는 127.0.0.1 6380
  lazy val redisMasterSlaves: RedisClientMasterSlaves = {
    val master = RedisServer()
    val slaves = Seq(RedisServer(), RedisServer(port = 6379))

    RedisClientMasterSlaves(master, slaves)
  }

  test("master slaves") {
    val key = "master_slaves"
    val value = System.currentTimeMillis()

    val isSet = redisMasterSlaves.set(key, value).await

    isSet shouldEqual true

    val millis = redisMasterSlaves.get[String](key).await.getOrElse("").toLong

    millis shouldEqual value

  }
}
