package debop4s.rediscala.client

import akka.util.ByteString
import debop4s.core.concurrent.Asyncs
import debop4s.rediscala.{MemberScore, _}
import redis._
import redis.api._
import redis.protocol.Status

import scala.annotation.varargs
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Redis 통신 자체는 비동기 작업이지만, 사용자의 편의를 위해 응답을 동기방식으로 처리할 수 있도록 했습니다.
 * @author sunghyouk.bae@gmail.com
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "2.0.0")
trait RedisSynchronizedSupport {
  self: RedisSyncClient =>

  val timeout = 60 seconds

  def redis: RedisCommands

  def redisBlocking: RedisBlockingClient

  def auth(password: String): Status = Asyncs.result(redis.auth(password))

  def echo[V: ByteStringSerializer, R: ByteStringDeserializer](msg: V): Option[R] =
    Asyncs.result(redis.echo(msg))


  def ping(): String = Asyncs.result(redis.ping())

  def quit(): Boolean = Asyncs.result(redis.quit())

  def select(db: Int): Boolean = Asyncs.result(redis.select(db))

  /**
   * 지정한 키를 삭제합니다.
   * @param key 삭제할 키
   * @return 삭제 여부
   */
  @varargs
  def del(key: String*): Long = Asyncs.result(redis.del(key: _*))

  def dump(key: String): Option[ByteString] = Asyncs.result(redis.dump(key))

  def exists(key: String): Boolean = Asyncs.result(redis.exists(key))

  def expire(key: String, seconds: Long): Boolean =
    Asyncs.result(redis.expire(key, seconds))

  def expireAt(key: String, timestamp: Long): Boolean =
    Asyncs.result(redis.expireat(key, timestamp))

  def keys(pattern: String): Seq[String] =
    Asyncs.result(redis.keys(pattern))

  def migrate(host: String, port: Int, key: String, destinationDb: Int, timeout: Int): Boolean =
    Asyncs.result(redis.migrate(host, port, key, destinationDb, timeout.seconds))

  def move(key: String, db: Int): Boolean =
    Asyncs.result(redis.move(key, db))

  def objectEncoding(key: String): Option[String] =
    Asyncs.result(redis.objectEncoding(key))

  def objectRefcount(key: String): Option[Long] =
    Asyncs.result(redis.objectRefcount(key))

  def objectIdletime(key: String): Option[Long] =
    Asyncs.result(redis.objectIdletime(key))

  def persist(key: String): Boolean =
    Asyncs.result(redis.persist(key))

  def pexpire(key: String, millis: Long): Boolean =
    Asyncs.result(redis.pexpire(key, millis))

  def pexpireAt(key: String, timestamp: Long): Boolean =
    Asyncs.result(redis.pexpireat(key, timestamp))

  def pttl(key: String): Long =
    Asyncs.result(redis.pttl(key))

  def randomKey[R: ByteStringDeserializer](): Option[R] =
    Asyncs.result(redis.randomkey())

  def rename(key: String, newKey: String): Boolean =
    Asyncs.result(redis.rename(key, newKey))

  def renameNX(key: String, newKey: String): Boolean =
    Asyncs.result(redis.renamenx(key, newKey))

  def restore[V: ByteStringSerializer](key: String, rawValue: V, ttl: Int = 0): Boolean =
    Asyncs.result(redis.restore(key, ttl, rawValue))

  def sort[R: ByteStringDeserializer](key: String,
                                      byPattern: Option[String] = None,
                                      limit: Option[LimitOffsetCount] = None,
                                      getPatterns: Seq[String] = Seq(),
                                      order: Option[Order] = None,
                                      alpha: Boolean = false): Seq[R] = {
    Asyncs.result {
      redis.sort(key, byPattern, limit, getPatterns, order, alpha)
    }
  }

  def sortStore(key: String,
                byPattern: Option[String] = None,
                limit: Option[LimitOffsetCount] = None,
                getPatterns: Seq[String] = Seq(),
                order: Option[Order] = None,
                alpha: Boolean = false,
                store: String): Long = {
    Asyncs.result {
      redis.sortStore(key, byPattern, limit, getPatterns, order, alpha, store)
    }
  }

  def ttl(key: String): Long = Asyncs.result(redis.ttl(key))

  def `type`(key: String): String = Asyncs.result(redis.`type`(key))

  /**
   * Redis 서버 시각을 반환합니다.
   * @return (Unix timestamp, microseconds)
   */
  def time(): (Long, Long) = Asyncs.result(redis.time())

  def dbsize(): Long = Asyncs.result(redis.dbsize())

  def append(key: String, value: String): Long =
    Asyncs.result(redis.append(key, value))

  def bitcount(key: String): Long =
    Asyncs.result(redis.bitcount(key))

  def bitcount(key: String, start: Long, end: Long): Long =
    Asyncs.result(redis.bitcount(key, start, end))


  @varargs
  def bitop(operation: BitOperator, destkey: String, keys: String*): Long =
    Asyncs.result(redis.bitop(operation, destkey, keys: _*))

  @varargs
  def bitopAND(destkey: String, keys: String*): Long =
    Asyncs.result(redis.bitopAND(destkey, keys: _*))

  @varargs
  def bitopOR(destkey: String, keys: String*): Long =
    Asyncs.result(redis.bitopOR(destkey, keys: _*))

  @varargs
  def bitopXOR(destkey: String, keys: String*): Long =
    Asyncs.result(redis.bitopXOR(destkey, keys: _*))

  def bitopNOT(destkey: String, srcKey: String): Long =
    Asyncs.result(redis.bitopNOT(destkey, srcKey))

  def decr(key: String): Long =
    Asyncs.result(redis.decr(key))

  def decrBy(key: String, decrement: Long): Long =
    Asyncs.result(redis.decrby(key, decrement))

  def get[R: ByteStringDeserializer](key: String): Option[R] =
    Asyncs.result(redis.get(key))

  def getbit(key: String, offset: Long): Boolean =
    Asyncs.result(redis.getbit(key, offset))

  def getRange[R: ByteStringDeserializer](key: String, start: Int, end: Int): Option[R] =
    Asyncs.result(redis.getrange(key, start, end))

  def getSet[V: ByteStringSerializer, R: ByteStringDeserializer](key: String, newValue: V): Option[R] =
    Asyncs.result(redis.getset(key, newValue))

  def incr(key: String): Long = Asyncs.result(redis.incr(key))

  def incrBy(key: String, increment: Long): Long =
    Asyncs.result(redis.incrby(key, increment))

  def incrByFloat(key: String, increment: Double): Double =
    Asyncs.result(redis.incrbyfloat(key, increment) map { x => x.getOrElse(0D) })

  def mget[R: ByteStringDeserializer](keys: String*) = Asyncs.result {
    redis.mget[R](keys: _*)
  }

  def mset[V: ByteStringSerializer](keysValues: Map[String, V]): Boolean =
    Asyncs.result(redis.mset(keysValues))

  def msetnx[V: ByteStringSerializer](keysValues: Map[String, V]): Boolean =
    Asyncs.result(redis.msetnx(keysValues))

  def psetex[V: ByteStringSerializer](key: String, milliseconds: Long, value: V): Boolean =
    Asyncs.result(redis.psetex(key, milliseconds, value))

  def set[V: ByteStringSerializer](key: String,
                                   value: V,
                                   exSeconds: Option[Long] = None,
                                   pxMilliseconds: Option[Long] = None,
                                   NX: Boolean = false,
                                   XX: Boolean = false): Boolean = {
    val x = redis.set(key, value, exSeconds, pxMilliseconds, NX, XX)
    Await.result(x, timeout)
  }

  def setbit(key: String, offset: Long, bit: Boolean): Boolean =
    Asyncs.result(redis.setbit(key, offset, bit))

  def setEx[V: ByteStringSerializer](key: String, seconds: Long, value: V): Boolean =
    Asyncs.result(redis.setex(key, seconds, value))

  def setNx[V: ByteStringSerializer](key: String, value: V): Boolean =
    Asyncs.result(redis.setnx(key, value))

  def setRange[V: ByteStringSerializer](key: String, offset: Long, value: V): Long =
    Asyncs.result(redis.setrange(key, offset, value))

  def strlen(key: String): Long =
    Asyncs.result(redis.strlen(key))

  @varargs
  def hdel(key: String, fields: String*): Long =
    Asyncs.result(redis.hdel(key, fields: _*))

  def hexists(key: String, field: String): Boolean =
    Asyncs.result(redis.hexists(key, field))

  def hgetall(key: String): Map[String, String] = Asyncs.result {
    redis.hgetall(key) map {
      _ map {
        case (k: String, v: ByteString) => (k, v.utf8String)
        case (k: String, _) => (k, null)
      }
    }
  }

  def hget[R: ByteStringDeserializer](key: String, field: String): Option[R] =
    Asyncs.result(redis.hget(key, field))

  def hincrBy(key: String, field: String, increment: Long): Long =
    Asyncs.result(redis.hincrby(key, field, increment))

  def hincrByFloat(key: String, field: String, increment: Double): Double =
    Asyncs.result(redis.hincrbyfloat(key, field, increment))

  def hkeys(key: String): Seq[String] =
    Asyncs.result(redis.hkeys(key))

  def hlen(key: String): Long = Asyncs.result(redis.hlen(key))

  def hmget[R: ByteStringDeserializer](key: String, fields: String*): Seq[Option[R]] =
    Asyncs.result(redis.hmget[R](key, fields: _*))

  def hset[V: ByteStringSerializer](key: String, field: String, value: V): Boolean =
    Asyncs.result(redis.hset(key, field, value))

  def hsetnx[V: ByteStringSerializer](key: String, field: String, value: V): Boolean =
    Asyncs.result(redis.hsetnx(key, field, value))

  def hmset[V: ByteStringSerializer](key: String, props: Map[String, V]): Boolean =
    Asyncs.result(redis.hmset(key, props))

  def hvals[R: ByteStringDeserializer](key: String): Seq[R] =
    Asyncs.result(redis.hvals(key))

  def blpop[R: ByteStringDeserializer](keys: Seq[String], timeout: Long): Option[(String, R)] =
    blpop[R](keys, timeout seconds)

  def blpop[R: ByteStringDeserializer](keys: Seq[String], timeout: FiniteDuration = Duration.Zero): Option[(String, R)] =
    Asyncs.result {
      redisBlocking.blpop[R](keys, timeout)
    }

  def brpop[R: ByteStringDeserializer](keys: Seq[String], timeout: Long): Option[(String, R)] =
    brpop[R](keys, timeout seconds)

  def brpop[R: ByteStringDeserializer](keys: Seq[String], timeout: FiniteDuration = Duration.Zero): Option[(String, R)] =
    Asyncs.result {
      redisBlocking.brpop[R](keys.toSeq, timeout)
    }

  def brpoplpush[R: ByteStringDeserializer](source: String,
                                            destination: String,
                                            timeout: Long): Option[R] =
    brpoplpush[R](source, destination, timeout seconds)

  def brpoplpush[R: ByteStringDeserializer](source: String,
                                            destination: String,
                                            timeout: FiniteDuration = Duration.Zero): Option[R] =
    Asyncs.result {
      redisBlocking.brpopplush[R](source, destination, timeout)
    }

  def lindex[R: ByteStringDeserializer](key: String, index: Long): Option[R] =
    Asyncs.result { redis.lindex[R](key, index) }

  def linsert[V: ByteStringSerializer](key: String,
                                       beforeAfter: ListPivot,
                                       pivot: String,
                                       value: V): Long =
    Asyncs.result(redis.linsert(key, beforeAfter, pivot, value))

  def linsertAfter[V: ByteStringSerializer](key: String, pivot: String, value: V) =
    Asyncs.result(redis.linsertAfter(key, pivot, value))

  def linsertBefore[V: ByteStringSerializer](key: String, pivot: String, value: V) =
    Asyncs.result(redis.linsertBefore(key, pivot, value))


  def llen(key: String): Long = Asyncs.result(redis.llen(key))

  def lpop[R: ByteStringDeserializer](key: String): Option[R] =
    Asyncs.result(redis.lpop(key))

  @varargs
  def lpush[V: ByteStringSerializer](key: String, values: V*): Long =
    Asyncs.result(redis.lpush(key, values: _*))

  def lpushx[V: ByteStringSerializer](key: String, value: V): Long =
    Asyncs.result(redis.lpushx(key, value))

  def lrange[R: ByteStringDeserializer](key: String, start: Long, end: Long): Seq[R] =
    Asyncs.result(redis.lrange(key, start, end))

  /**
   * 목록에서 지정한 값과 일치하는 항목을 삭제합니다.
   * 예: hello, hello, foo, hello
   *
   * @param key   리스트 키
   * @param count 최소한의 갯수 ( 부호는 방향을 뜻함 - (+)는 head->tail, (-)는 tail->head, 0은 모든 값)
   * @param value 삭제할 값
   * @return 삭제한 항목의 수
   * @see #lremFirst(String, String, long)
   * @see #lremLast(String, String, long)
   * @see #lremAll(String, String)
   */
  def lrem[V: ByteStringSerializer](key: String, count: Long, value: V): Long =
    Asyncs.result(redis.lrem(key, count, value))

  def lremFirst[V: ByteStringSerializer](key: String, value: V, count: Long): Long =
    Asyncs.result(redis.lrem(key, count, value))

  def lremLast[V: ByteStringSerializer](key: String, value: V, count: Long): Long =
    Asyncs.result(redis.lrem(key, -count, value))

  def lremAll[V: ByteStringSerializer](key: String, value: V): Long =
    Asyncs.result(redis.lrem(key, 0, value))

  def lset[V: ByteStringSerializer](key: String, index: Long, value: V): Boolean =
    Asyncs.result(redis.lset(key, index, value))

  def ltrim(key: String, start: Long, end: Long = -1): Boolean =
    Asyncs.result(redis.ltrim(key, start, end))

  def rpop[R: ByteStringDeserializer](key: String): Option[R] =
    Asyncs.result(redis.rpop[R](key))

  def rpoplpush[R: ByteStringDeserializer](srcKey: String, destKey: String): Option[R] =
    Asyncs.result(redis.rpoplpush[R](srcKey, destKey))

  @varargs
  def rpush[V: ByteStringSerializer](key: String, values: V*): Long =
    Asyncs.result(redis.rpush(key, values: _*))

  def rpushx[V: ByteStringSerializer](key: String, value: V): Long =
    Asyncs.result(redis.rpushx(key, value))

  @varargs
  def sadd[V: ByteStringSerializer](key: String, members: V*): Long =
    Asyncs.result(redis.sadd(key, members: _*))

  def scard(key: String): Long =
    Asyncs.result(redis.scard(key))

  def sdiff[R: ByteStringDeserializer](key: String, keys: String*): Seq[R] =
    Asyncs.result(redis.sdiff[R](key, keys: _*))

  @varargs
  def sdiffStore(destKey: String, key: String, keys: String*): Long =
    Asyncs.result(redis.sdiffstore(destKey, key, keys: _*))

  def sinter[R: ByteStringDeserializer](key: String, keys: String*): Seq[R] =
    Asyncs.result(redis.sinter[R](key, keys: _*))

  def sinterStore(destKey: String, key: String, keys: String*): Long =
    Asyncs.result(redis.sinterstore(destKey, key, keys: _*))

  def sisMember[V: ByteStringSerializer](key: String, member: V): Boolean =
    Asyncs.result(redis.sismember(key, member))

  def smembers[R: ByteStringDeserializer](key: String): Seq[R] =
    Asyncs.result(redis.smembers[R](key))

  def smove[V: ByteStringSerializer](srcKey: String, destKey: String, member: V): Boolean =
    Asyncs.result(redis.smove(srcKey, destKey, member))

  def spop[R: ByteStringDeserializer](key: String): Option[R] =
    Asyncs.result(redis.spop[R](key))

  def srandmember[R: ByteStringDeserializer](key: String): Option[R] =
    Asyncs.result(redis.srandmember[R](key))

  def srandmember[R: ByteStringDeserializer](key: String, count: Long): Seq[R] =
    Asyncs.result(redis.srandmember[R](key, count))

  @varargs
  def srem[V: ByteStringSerializer](key: String, members: V*): Long =
    Asyncs.result(redis.srem(key, members: _*))

  def sunion[R: ByteStringDeserializer](key: String, keys: String*): Seq[R] =
    Asyncs.result(redis.sunion[R](key, keys: _*))

  @varargs
  def sunionStore(destKey: String, key: String, keys: String*): Long =
    Asyncs.result(redis.sunionstore(destKey, key, keys: _*))

  def zadd[V: ByteStringSerializer](key: String, score: Double, member: V): Long =
    Asyncs.result(redis.zadd(key, (score, member)))

  @varargs
  def zadd(key: String, memberScores: MemberScore*): Long =
    Asyncs.result {
      redis.zadd(key, memberScores.map(ms => (ms.score, ms.member)).toSeq: _*)
    }

  @varargs
  def zaddAll[V: ByteStringSerializer](key: String, scoreMembers: (Double, V)*): Long =
    Asyncs.result(redis.zadd(key, scoreMembers: _*))

  def zcard(key: String): Long = Asyncs.result(redis.zcard(key))

  def zcount(key: String,
             min: Double,
             max: Double): Long =
    Asyncs.result(redis.zcount(key, Limit(min), Limit(max)))

  def zcount(key: String,
             min: Limit = NegativeInfinity,
             max: Limit = PositiveInfinity): Long =
    Asyncs.result(redis.zcount(key, min, max))

  def zincrBy[V: ByteStringSerializer](key: String, increment: Double, member: V): Double =
    Asyncs.result(redis.zincrby(key, increment, member))

  def zinterStore(destination: String,
                  key: String,
                  keys: Seq[String],
                  aggregate: Aggregate = SUM): Long =
    Asyncs.result(redis.zinterstore(destination, key, keys, aggregate))

  def zinterStoreWeighted(destination: String,
                          keys: Map[String, Double],
                          aggregate: Aggregate = SUM): Long =
    Asyncs.result(redis.zinterstoreWeighted(destination, keys, aggregate))

  def zrange[R: ByteStringDeserializer](key: String, start: Long = 0, end: Long = -1): Seq[R] =
    Asyncs.result { redis.zrange(key, start, end) }

  def zrangeByScore[R: ByteStringDeserializer](key: String,
                                               min: Double,
                                               max: Double): Seq[R] =
    Asyncs.result {
      redis.zrangebyscore(key, Limit(min), Limit(max), None)
    }
  def zrangeByScore[R: ByteStringDeserializer](key: String,
                                               min: Double,
                                               max: Double,
                                               limit: Option[(Long, Long)]): Seq[R] =
    Asyncs.result {
      redis.zrangebyscore(key, Limit(min), Limit(max), limit)
    }
  def zrangeByScore[R: ByteStringDeserializer](key: String,
                                               min: Limit,
                                               max: Limit): Seq[R] =
    Asyncs.result {
      redis.zrangebyscore(key, min, max, None)
    }
  def zrangeByScore[R: ByteStringDeserializer](key: String,
                                               min: Limit,
                                               max: Limit,
                                               limit: Option[(Long, Long)]): Seq[R] =
    Asyncs.result {
      redis.zrangebyscore(key, min, max, limit)
    }


  def zrangeByScoreWithScores(key: String,
                              min: Double,
                              max: Double): Seq[MemberScore] =
    Asyncs.result {
      redis.zrangebyscoreWithscores(key, Limit(min), Limit(max), None) map toMemberScore
    }
  def zrangeByScoreWithScores(key: String,
                              min: Double,
                              max: Double,
                              limit: Option[(Long, Long)]): Seq[MemberScore] =
    Asyncs.result {
      redis.zrangebyscoreWithscores(key, Limit(min), Limit(max), limit) map toMemberScore
    }
  def zrangeByScoreWithScores(key: String,
                              min: Limit,
                              max: Limit): Seq[MemberScore] =
    Asyncs.result {
      redis.zrangebyscoreWithscores(key, min, max, None) map toMemberScore
    }
  def zrangeByScoreWithScores(key: String,
                              min: Limit,
                              max: Limit,
                              limit: Option[(Long, Long)]): Seq[MemberScore] =
    Asyncs.result {
      redis.zrangebyscoreWithscores(key, min, max, limit) map toMemberScore
    }

  def zrangeWithScores(key: String, start: Long = 0L, end: Long = -1L): Seq[MemberScore] =
    Asyncs.result {
      redis.zrangeWithscores(key, start, end) map toMemberScore
    }

  def zrevrange[R: ByteStringDeserializer](key: String, start: Long = 0, end: Long = -1L): Seq[R] =
    Asyncs.result {
      redis.zrevrange(key, start, end)
    }

  def zrevrangeByScore[R: ByteStringDeserializer](key: String,
                                                  min: Double,
                                                  max: Double): Seq[R] =
    Asyncs.result {
      redis.zrevrangebyscore(key, Limit(min), Limit(max), None)
    }
  def zrevrangeByScore[R: ByteStringDeserializer](key: String,
                                                  min: Double,
                                                  max: Double,
                                                  limit: Option[(Long, Long)]): Seq[R] =
    Asyncs.result {
      redis.zrevrangebyscore(key, Limit(min), Limit(max), limit)
    }
  def zrevrangeByScore[R: ByteStringDeserializer](key: String,
                                                  min: Limit,
                                                  max: Limit): Seq[R] =
    Asyncs.result {
      redis.zrevrangebyscore(key, min, max, None)
    }
  def zrevrangeByScore[R: ByteStringDeserializer](key: String,
                                                  min: Limit,
                                                  max: Limit,
                                                  limit: Option[(Long, Long)]): Seq[R] =
    Asyncs.result {
      redis.zrevrangebyscore(key, min, max, limit)
    }

  def zrevrangeByScoreWithScores(key: String,
                                 min: Double,
                                 max: Double): Seq[MemberScore] =
    Asyncs.result {
      redis.zrevrangebyscoreWithscores(key, Limit(min), Limit(max), None) map toMemberScore
    }
  def zrevrangeByScoreWithScores(key: String,
                                 min: Double,
                                 max: Double,
                                 limit: Option[(Long, Long)]): Seq[MemberScore] =
    Asyncs.result {
      redis.zrevrangebyscoreWithscores(key, Limit(min), Limit(max), limit) map toMemberScore
    }
  def zrevrangeByScoreWithScores(key: String,
                                 min: Limit,
                                 max: Limit): Seq[MemberScore] =
    Asyncs.result {
      redis.zrevrangebyscoreWithscores(key, min, max, None) map toMemberScore
    }
  def zrevrangeByScoreWithScores(key: String,
                                 min: Limit,
                                 max: Limit,
                                 limit: Option[(Long, Long)]): Seq[MemberScore] =
    Asyncs.result {
      redis.zrevrangebyscoreWithscores(key, min, max, limit) map toMemberScore
    }

  def zrevrangeWithScores(key: String, start: Long = 0, end: Long = -1): Seq[MemberScore] =
    Asyncs.result {
      redis.zrevrangeWithscores(key, start, end) map toMemberScore
    }

  def zrank(key: String, member: String): Option[Long] =
    Asyncs.result(redis.zrank(key, member))

  def zrevrank(key: String, member: String): Option[Long] =
    Asyncs.result(redis.zrevrank(key, member))

  @varargs
  def zrem[V: ByteStringSerializer](key: String, members: V*): Long =
    Asyncs.result(redis.zrem(key, members: _*))

  def zremRangeByRank(key: String, start: Long = 0, end: Long = -1): Long =
    Asyncs.result(redis.zremrangebyrank(key, start, end))

  def zremRangeByScore(key: String, min: Double, max: Double): Long =
    Asyncs.result(redis.zremrangebyscore(key, Limit(min), Limit(max)))

  def zremRangeByScore(key: String, min: Limit, max: Limit): Long =
    Asyncs.result(redis.zremrangebyscore(key, min, max))

  def zscore[V: ByteStringSerializer](key: String, member: V): Option[Double] =
    Asyncs.result(redis.zscore(key, member))

  def zunionStore(destination: String,
                  key: String,
                  keys: Seq[String],
                  aggregate: Aggregate = SUM): Long =
    Asyncs.result {
      redis.zunionstore(destination, key, keys, aggregate)
    }

  def zunionStoreWeighted(destination: String,
                          keys: Map[String, Double],
                          aggregate: Aggregate = SUM): Long =
    Asyncs.result {
      redis.zunionstoreWeighted(destination, keys, SUM)
    }

  def bgRewriteAof(): String = Asyncs.result(redis.bgrewriteaof())

  def bgSave(): String = Asyncs.result(redis.bgsave())

  def clientKill(ip: String, port: Int): Boolean =
    Asyncs.result(redis.clientKill(ip, port))

  def clientList(): Seq[Map[String, String]] = Asyncs.result(redis.clientList())

  def clientGetname(): Option[String] =
    Asyncs.result { redis.clientGetname() }


  def clientSetname(connectionName: String): Boolean =
    Asyncs.result(redis.clientSetname(connectionName))

  def configGet(parameter: String): Map[String, String] =
    Asyncs.result(redis.configGet(parameter))

  def configSet(parameter: String, value: String): Boolean =
    Asyncs.result(redis.configSet(parameter, value))

  def configResetstat(): Boolean =
    Asyncs.result(redis.configResetstat())

  def dbSize(): Long = Asyncs.result(redis.dbsize())

  def debugObject(key: String): String = Asyncs.result(redis.debugObject(key))

  def debugSegfault(): String = Asyncs.result(redis.debugSegfault())

  def flushAll(): Boolean = Asyncs.result(redis.flushall())

  def flushDb(): Boolean = Asyncs.result(redis.flushdb())

  def info(): String = Asyncs.result(redis.info())
  def info(section: String): String = Asyncs.result(redis.info(section))

  def lastSave(): Long = Asyncs.result(redis.lastsave())

  def save(): Boolean = Asyncs.result(redis.save())

  def shutdown(): Boolean = Asyncs.result(redis.shutdown())
  def shutdown(modifier: ShutdownModifier): Boolean =
    Asyncs.result(redis.shutdown(modifier))

  def slaveOf(host: String, port: Int): Boolean =
    Asyncs.result(redis.slaveof(host, port))

  def slaveOffNoOne(): Boolean =
    Asyncs.result(redis.slaveofNoOne())

  def publish(channel: String, value: String): Long =
    Asyncs.result(redis.publish(channel, value))


}
