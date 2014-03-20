package com.github.debop4s.redis

import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import redis.RedisClient

/**
 * AbstractRedisTest
 * Created by debop on 2014. 2. 22.
 */
class AbstractRedisTest extends FunSuite with Matchers with BeforeAndAfter {

    implicit val akkaSystem = akka.actor.ActorSystem()

    lazy val redis = RedisClient()

}
