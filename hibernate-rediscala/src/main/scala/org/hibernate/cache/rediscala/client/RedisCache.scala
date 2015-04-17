package org.hibernate.cache.rediscala.client

import org.hibernate.cache.rediscala._
import redis.commands.Transactions
import redis.{RedisClient, RedisCommands}

/**
 * HibernateRedisCache Companion Object
 */
object RedisCache {

  // Cache expiration 기본 값 (0 이면 expire 하지 않는다)
  val DEFAULT_EXPIRY_IN_SECONDS = 0

  // default resion name
  val DEFAULT_REGION_NAME = "hibernate"

  def apply(): RedisCache =
    apply(RedisClient())

  def apply(redis: RedisCommands with Transactions): RedisCache =
    new RedisCache(redis)

}

/**
 * Redis-Server 에 Cache 정보를 저장하고 로드하는 Class 입니다.
 * 참고: rediscala 라이브러리를 사용합니다 ( https://github.com/etaty/rediscala )
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 1:14
 */
class RedisCache(override val redis: RedisCommands with Transactions)
  extends RedisCacheSupport with RedisTransactionSupport {

  override val transactionalRedis: Transactions = redis

}


