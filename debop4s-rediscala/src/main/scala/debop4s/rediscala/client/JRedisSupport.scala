package debop4s.rediscala.client

import java.lang.{Boolean => JBool, Double => JDouble, Iterable => JIterable, Long => JLong}
import java.sql.Timestamp
import java.util
import java.util.{List => JList, Map => JMap, Set => JSet}

import debop4s.rediscala.{MemberScore, _}
import redis.api._
import redis.protocol.Status
import redis.{RedisBlockingClient, RedisCommands}

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * JRedisSupport
 * @author sunghyouk.bae@gmail.com
 */
trait JRedisSupport {

  /**
   * `RedisCommands` 를 상속받은 `RedisClient` 나 `RedisMasterSlavesClient` 를 반환합니다.
   */
  def redis: RedisCommands

  /**
   * `RedisBlockingClient` 를 반환합니다.
   */
  def redisBlocking: RedisBlockingClient

  def auth(password: String): Future[Status] = redis.auth(password)

  def echo(msg: String): Future[String] =
    redis.echo[String, String](msg).map(_.orNull)

  def ping(): Future[String] = redis.ping()

  def quit(): Future[JBool] = redis.quit().map(x => x)

  def select(db: Int): Future[JBool] = redis.select(db).map(x => x)

  @varargs
  def del(key: String*): Future[JLong] = redis.del(key: _*).map(x => x)

  def del(keys: JIterable[String]): Future[JLong] = redis.del(keys.asScala.toSeq: _*).map(x => x)

  def dump(key: String): Future[Array[Byte]] =
    redis.dump(key) map { x => x.get.toArray }
  //      case Some(x: ByteString) => x.toArray
  //      case _ => null.asInstanceOf[Array[Byte]]
  //    }

  def exists(key: String): Future[JBool] = redis.exists(key).map(x => x)

  def expire(key: String, seconds: Long): Future[JBool] = redis.expire(key, seconds).map(x => x)
  def expireAt(key: String, timestamp: Long): Future[JBool] = redis.expireat(key, timestamp).map(x => x)

  def keys(pattern: String): Future[JSet[String]] =
    redis.keys(pattern) map { x => x.toSet.asJava }

  def migrate(host: String, port: Int, key: String, destinationDb: Int, timeout: Int): Future[JBool] =
    redis.migrate(host, port, key, destinationDb, timeout seconds).map(x => x)

  def move(key: String, db: Int): Future[JBool] = redis.move(key, db).map(x => x)

  def objectEncoding(key: String): Future[String] =
    redis.objectEncoding(key) map { x => x.getOrElse("") }

  def objectRefcount(key: String): Future[JLong] =
    redis.objectRefcount(key).map(x => x)

  def objectIdletime(key: String): Future[JLong] =
    redis.objectIdletime(key).map(x => x)

  def persist(key: String): Future[JBool] = redis.persist(key).map(x => x)

  def pexpire(key: String, millis: Long): Future[JBool] =
    redis.pexpire(key, millis).map(x => x)

  def pexpireAt(key: String, timestamp: Long): Future[JBool] =
    redis.pexpireat(key, timestamp).map(x => x)

  def pttl(key: String): Future[JLong] = redis.pttl(key).map(_.toLong)

  def randomKey(): Future[String] =
    redis.randomkey[String]().map(_.orNull)

  def rename(key: String, newKey: String): Future[JBool] =
    redis.rename(key, newKey).map(x => x)

  def renameNX(key: String, newKey: String): Future[JBool] =
    redis.renamenx(key, newKey).map(x => x)

  def restore(key: String, rawValue: Array[Byte]): Future[JBool] =
    redis.restore(key, 0, rawValue).map(x => x)

  def restore(key: String, ttl: Int, rawValue: Array[Byte]): Future[JBool] =
    redis.restore(key, ttl, rawValue).map(x => x)

  def sort(key: String): Future[JList[String]] =
    redis.sort[String](key) map { xs => xs.asJava }

  def sort(key: String, order: Order): Future[JList[String]] =
    redis.sort[String](key, order = Some(order)) map { xs => xs.asJava }

  def sort(key: String, alpha: Boolean): Future[JList[String]] =
    redis.sort[String](key, alpha = alpha) map { xs => xs.asJava }

  def sort(key: String, limit: LimitOffsetCount): Future[JList[String]] =
    redis.sort[String](key, limit = Some(limit)) map { xs => xs.asJava }

  def sort(key: String, order: Order, alpha: Boolean): Future[JList[String]] =
    redis.sort[String](key, order = Some(order), alpha = alpha).map { xs => xs.asJava }

  def sort(key: String, order: Order, alpha: Boolean, limit: LimitOffsetCount): Future[JList[String]] =
    redis.sort[String](key, order = Some(order), alpha = alpha, limit = Some(limit)).map { xs => xs.asJava }

  def sortStore(key: String, destKey: String): Future[JLong] =
    sortStore(key, None, destKey)

  def sortStore(key: String, order: Option[Order], destKey: String): Future[JLong] =
    redis.sortStore(key, order = order, store = destKey).map(x => x)

  def ttl(key: String): Future[JLong] = redis.ttl(key).map(x => x)

  def `type`(key: String): Future[String] = redis.`type`(key)

  def time(): Future[Timestamp] = redis.time() map {
    case (unixtime: Long, microseconds: Long) =>
      val timestamp = new Timestamp(unixtime)
      timestamp.setNanos((microseconds * 1000L).toInt)
      timestamp
    case _ => new Timestamp(0)
  }

  def dbsize(): Future[JLong] = redis.dbsize().map(x => x)

  def append(key: String, value: String): Future[JLong] =
    redis.append(key, value).map(x => x)

  def bitcount(key: String): Future[JLong] =
    redis.bitcount(key).map(x => x)

  def bitcount(key: String, start: Long, end: Long): Future[JLong] =
    redis.bitcount(key, start, end).map(x => x)


  @varargs
  def bitop(operation: BitOperator, destkey: String, keys: String*): Future[JLong] =
    redis.bitop(operation, destkey, keys: _*).map(x => x)

  @varargs
  def bitopAND(destkey: String, keys: String*): Future[JLong] =
    redis.bitopAND(destkey, keys: _*).map(x => x)

  @varargs
  def bitopOR(destkey: String, keys: String*): Future[JLong] =
    redis.bitopOR(destkey, keys: _*).map(x => x)

  @varargs
  def bitopXOR(destkey: String, keys: String*): Future[JLong] =
    redis.bitopXOR(destkey, keys: _*).map(x => x)

  def bitopNOT(destkey: String, srcKey: String): Future[JLong] =
    redis.bitopNOT(destkey, srcKey).map(x => x)

  def decr(key: String): Future[JLong] =
    redis.decr(key).map(x => x)

  def decrBy(key: String, decrement: Long): Future[JLong] =
    redis.decrby(key, decrement).map(x => x)

  def get(key: String): Future[String] =
    redis.get[String](key) map { _.orNull }

  def getbit(key: String, offset: Long): Future[JBool] =
    redis.getbit(key, offset).map(x => x)

  def getRange(key: String, start: Int, end: Int): Future[String] =
    redis.getrange[String](key, start, end) map { _.orNull }

  def getSet(key: String, newValue: String): Future[String] =
    redis.getset[String, String](key, newValue) map { _.orNull }

  def incr(key: String): Future[JLong] =
    redis.incr(key).map(x => x)

  def incrBy(key: String, increment: Long): Future[JLong] =
    redis.incrby(key, increment).map(x => x)

  def incrByFloat(key: String, increment: Double): Future[JDouble] =
    redis.incrbyfloat(key, increment).map { x => x.getOrElse(0D) }.map(x => x)

  @varargs
  def mget(keys: String*): Future[JList[String]] =
    redis.mget[String](keys: _*) map { xs =>
      val results = new util.ArrayList[String](xs.length)
      val iter = xs.iterator
      while (iter.hasNext) {
        results add iter.next().orNull
      }
      results
      // xs.map { x => x.orNull }.asJava
    }

  def mset(keysValues: JMap[String, String]): Future[JBool] =
    redis.mset(keysValues.asScala.toMap).map(x => x)

  def msetnx(keysValues: JMap[String, String]): Future[JBool] =
    redis.msetnx(keysValues.asScala.toMap).map(x => x)

  def psetex(key: String, milliseconds: Long, value: String): Future[JBool] =
    redis.psetex(key, milliseconds, value).map(x => x)

  def set(key: String, value: String): Future[JBool] =
    redis.set(key, value).map(x => x)

  def set(key: String, value: String, seconds: Long): Future[JBool] =
    redis.set(key, value, exSeconds = Some(seconds)).map(x => x)

  def set(key: String, value: String, seconds: Long, millis: Long): Future[JBool] =
    redis.set(key, value, exSeconds = Some(seconds), pxMilliseconds = Some(millis)).map(x => x)

  def setbit(key: String, offset: Long, bit: Boolean): Future[JBool] =
    redis.setbit(key, offset, bit).map(x => x)

  def setEx(key: String, seconds: Long, value: String): Future[JBool] =
    redis.setex(key, seconds, value).map(x => x)

  def setNx(key: String, value: String): Future[JBool] =
    redis.setnx(key, value).map(x => x)

  def setRange(key: String, offset: Long, value: String): Future[JLong] =
    redis.setrange(key, offset, value).map(x => x)

  def strlen(key: String): Future[JLong] = redis.strlen(key).map(x => x)

  @varargs
  def hdel(key: String, fields: String*): Future[JLong] =
    redis.hdel(key, fields: _*).map(x => x)

  def hexists(key: String, field: String): Future[JBool] =
    redis.hexists(key, field).map(x => x)

  def hgetall(key: String): Future[JMap[String, String]] =
    redis.hgetall[String](key) map { xs =>
      val results = new util.HashMap[String, String](xs.length)
      val iter = xs.keysIterator
      while (iter.hasNext) {
        val k = iter.next()
        results.put(k, xs.get(k).orNull)
      }
      results
    }

  def hget(key: String, field: String): Future[String] =
    redis.hget[String](key, field) map { _.orNull }

  def hincrBy(key: String, field: String, increment: Long): Future[JLong] =
    redis.hincrby(key, field, increment).map(x => x)

  def hincrByFloat(key: String, field: String, increment: Double): Future[JDouble] =
    redis.hincrbyfloat(key, field, increment).map(x => x)

  def hkeys(key: String): Future[JList[String]] =
    redis.hkeys(key) map { xs => xs.asJava }

  def hlen(key: String): Future[JLong] =
    redis.hlen(key).map(x => x)

  @varargs
  def hmget(key: String, fields: String*): Future[JList[String]] =
    redis.hmget[String](key, fields: _*) map { xs =>
      val results = new util.ArrayList[String](xs.size)
      var i = 0
      while (i < xs.size) {
        results add xs(i).orNull
        i += 1
      }
      results
    }

  def hset(key: String, field: String, value: String): Future[JBool] =
    redis.hset(key, field, value).map(x => x)

  def hsetnx(key: String, field: String, value: String): Future[JBool] =
    redis.hsetnx(key, field, value).map(x => x)

  def hmset(key: String, props: JMap[String, String]): Future[JBool] =
    redis.hmset(key, props.asScala.toMap).map(x => x)

  @varargs
  def hmset(key: String, pairs: (String, String)*): Future[JBool] =
    redis.hmset(key, pairs.toMap).map(x => x)

  def hvals(key: String): Future[JList[String]] =
    redis.hvals[String](key) map { xs => xs.asJava }

  @varargs
  def blpop(seconds: Long, keys: String*): Future[(String, String)] =
    redisBlocking.blpop[String](keys.toSeq, seconds seconds).map(_.orNull)

  @varargs
  def brpop(seconds: Long, keys: String*): Future[(String, String)] =
    redisBlocking.brpop[String](keys.toSeq, seconds seconds) map { _.orNull }

  def brpoplpush(srcKey: String, destKey: String, seconds: Long): Future[String] =
    redisBlocking.brpopplush[String](srcKey, destKey, seconds seconds) map { _.orNull }

  def lindex(key: String, index: Long): Future[String] =
    redis.lindex[String](key, index) map { _.orNull }

  def linsert(key: String, beforeAfter: ListPivot, pivot: String, value: String): Future[JLong] =
    redis.linsert(key, beforeAfter, pivot, value).map(x => x)

  def linsertAfter(key: String, pivot: String, value: String): Future[JLong] =
    redis.linsertAfter(key, pivot, value).map(x => x)

  def linsertBefore(key: String, pivot: String, value: String): Future[JLong] =
    redis.linsertBefore(key, pivot, value).map(x => x)


  def llen(key: String): Future[JLong] = redis.llen(key).map(x => x)

  def lpop(key: String): Future[String] = redis.lpop[String](key) map { _.orNull }

  @varargs
  def lpush(key: String, values: String*): Future[JLong] =
    redis.lpush(key, values: _*).map(x => x)

  def lpushx(key: String, value: String): Future[JLong] =
    redis.lpushx(key, value).map(x => x)

  def lrange(key: String, start: Long, end: Long): Future[JList[String]] =
    redis.lrange[String](key, start, end).map { _.asJava }

  def lrem(key: String, count: Long, value: String): Future[JLong] =
    redis.lrem(key, count, value).map(x => x)


  def lremFirst(key: String, value: String, count: Long): Future[JLong] =
    redis.lrem(key, count, value).map(x => x)

  def lremLast(key: String, value: String, count: Long): Future[JLong] =
    redis.lrem(key, -count, value).map(x => x)

  def lremAll(key: String, value: String): Future[JLong] =
    redis.lrem(key, 0, value).map(x => x)

  def lset(key: String, index: Long, value: String): Future[JBool] =
    redis.lset(key, index, value).map(x => x)

  def ltrim(key: String, start: Long): Future[JBool] =
    redis.ltrim(key, start, -1).map(x => x)

  def ltrim(key: String, start: Long, end: Long): Future[JBool] =
    redis.ltrim(key, start, end).map(x => x)

  def rpop(key: String): Future[String] = redis.rpop[String](key).map { _.orNull }

  def rpoplpush(srcKey: String, destKey: String): Future[String] =
    redis.rpoplpush[String](srcKey, destKey) map { _.orNull }

  @varargs
  def rpush(key: String, values: String*): Future[JLong] =
    redis.rpush(key, values: _*).map(x => x)

  def rpushx(key: String, value: String): Future[JLong] =
    redis.rpushx(key, value).map(x => x)

  @varargs
  def sadd(key: String, members: String*): Future[JLong] =
    redis.sadd(key, members: _*).map(x => x)

  def sadd(key: String, members: JIterable[String]): Future[JLong] =
    redis.sadd(key, members.asScala.toSeq: _*).map(x => x)

  def scard(key: String): Future[JLong] = redis.scard(key).map(x => x)

  @varargs
  def sdiff(key: String, keys: String*): Future[JSet[String]] =
    redis.sdiff[String](key, keys: _*) map { xs => xs.toSet.asJava }

  @varargs
  def sdiffStore(destKey: String, key: String, keys: String*): Future[JLong] =
    redis.sdiffstore(destKey, key, keys: _*).map(x => x)

  @varargs
  def sinter(key: String, keys: String*): Future[JSet[String]] =
    redis.sinter[String](key, keys: _*) map { xs => xs.toSet.asJava }

  def sinter(key: String, keys: JIterable[String]): Future[JSet[String]] =
    redis.sinter[String](key, keys.asScala.toSeq: _*) map { xs => xs.toSet.asJava }

  def sinterStore(destKey: String, key: String, keys: String*): Future[JLong] =
    redis.sinterstore(destKey, key, keys: _*).map(x => x)

  def sisMember(key: String, member: String): Future[JBool] =
    redis.sismember(key, member).map(x => x)

  def smembers(key: String): Future[JSet[String]] =
    redis.smembers[String](key).map { xs => xs.toSet.asJava }

  def smove(srcKey: String, destKey: String, member: String): Future[JBool] =
    redis.smove(srcKey, destKey, member).map(x => x)

  def spop(key: String): Future[String] =
    redis.spop[String](key) map { _.orNull }

  def srandmember(key: String): Future[String] =
    redis.srandmember[String](key) map { _.orNull }

  def srandmember(key: String, count: Long): Future[JSet[String]] =
    redis.srandmember[String](key, count) map { xs => xs.toSet.asJava }

  @varargs
  def srem(key: String, members: String*): Future[JLong] =
    redis.srem(key, members: _*).map(x => x)

  @varargs
  def sunion(key: String, keys: String*): Future[JSet[String]] =
    redis.sunion[String](key, keys: _*) map { xs => xs.toSet.asJava }

  @varargs
  def sunionStore(destKey: String, key: String, keys: String*): Future[JLong] =
    redis.sunionstore(destKey, key, keys: _*).map(x => x)

  def zadd(key: String, score: Double, member: String): Future[JLong] =
    redis.zadd(key, (score, member)).map(x => x)

  @varargs
  def zaddAll(key: String, memberScores: MemberScore*): Future[JLong] =
    redis.zadd(key, memberScores.map { ms => (ms.score, ms.member) }.toSeq: _*).map(x => x)

  def zaddAll(key: String, memberScores: JMap[String, Double]): Future[JLong] =
    redis.zadd(key, memberScores.asScala.map { case (m, s) => (s, m) }.toSeq: _*).map(x => x)

  def zcard(key: String): Future[JLong] = redis.zcard(key).map(x => x)

  def zcount(key: String): Future[JLong] = redis.zcount(key).map(x => x)

  def zcount(key: String, minScore: Double, maxScore: Double): Future[JLong] =
    redis.zcount(key, Limit(minScore), Limit(maxScore)).map(x => x)

  def zcount(key: String, min: Limit, max: Limit): Future[JLong] =
    redis.zcount(key, min, max).map(x => x)

  def zincrBy(key: String, increment: Double, member: String): Future[JDouble] =
    redis.zincrby(key, increment, member).map(x => x)

  @varargs
  def zinterStore(destKey: String, key: String, keys: String*): Future[JLong] =
    redis.zinterstore(destKey, key, keys.toSeq, SUM).map(x => x)

  @varargs
  def zinterStore(destKey: String, aggregate: Aggregate, key: String, keys: String*): Future[JLong] =
    redis.zinterstore(destKey, key, keys.toSeq, aggregate).map(x => x)

  def zinterStoreWeighted(destKey: String, keys: JMap[String, JDouble]): Future[JLong] = {
    val weightedKeys = keys.asScala.map { case (k, v) => (k, v.toDouble) }
    redis.zinterstoreWeighted(destKey, weightedKeys.toMap, SUM).map(x => x)
  }
  def zinterStoreWeighted(destKey: String, keys: JMap[String, JDouble], aggregate: Aggregate): Future[JLong] = {
    val weightedKeys = keys.asScala.map { case (k, v) => (k, v.toDouble) }
    redis.zinterstoreWeighted(destKey, weightedKeys.toMap, aggregate).map(x => x)
  }

  def zrange(key: String): Future[JList[String]] = zrange(key, 0, -1)
  def zrange(key: String, start: Long, end: Long): Future[JList[String]] =
    redis.zrange[String](key, start, end).map { _.asJava }

  def zrangeByScore(key: String): Future[JList[String]] =
    zrangeByScore(key, Double.NegativeInfinity, Double.PositiveInfinity)

  def zrangeByScore(key: String, min: Double, max: Double): Future[JList[String]] =
    zrangeByScore(key, Limit(min), Limit(max))

  def zrangeByScore(key: String, min: Double, max: Double, offset: Long, count: Long): Future[JList[String]] =
    zrangeByScore(key, Limit(min), Limit(max), offset, count)

  def zrangeByScore(key: String, min: Limit, max: Limit): Future[JList[String]] =
    redis.zrangebyscore[String](key, min, max) map { _.asJava }

  def zrangeByScore(key: String, min: Limit, max: Limit, offset: Long, count: Long): Future[JList[String]] =
    redis.zrangebyscore[String](key, min, max, Some(new Tuple2(offset, count))) map { _.asJava }

  def zrangeByScoreWithScores(key: String): Future[JList[MemberScore]] =
    zrangeByScoreWithScores(key, Double.NegativeInfinity, Double.PositiveInfinity)

  def zrangeByScoreWithScores(key: String, min: Double, max: Double): Future[JList[MemberScore]] =
    zrangeByScoreWithScores(key, Limit(min), Limit(max))

  def zrangeByScoreWithScores(key: String, min: Limit, max: Limit): Future[JList[MemberScore]] = {
    redis.zrangebyscoreWithscores[String](key, min, max) map { xs =>
      val results = new util.ArrayList[MemberScore]()
      var i = 0
      while (i < xs.length) {
        val (m, s) = xs(i)
        results add MemberScore(m, s)
        i += 1
      }
      results
      // xs.map { case (m, s) => MemberScore(m, s) }.asJava
    }
  }

  def zrangeWithScores(key: String): Future[JList[MemberScore]] =
    zrangeWithScores(key, 0, -1)

  def zrangeWithScores(key: String, start: Long, end: Long): Future[JList[MemberScore]] = {
    redis.zrangeWithscores[String](key, start, end) map { xs =>
      val results = new util.ArrayList[MemberScore]()
      var i = 0
      while (i < xs.length) {
        val (m, s) = xs(i)
        results add MemberScore(m, s)
        i += 1
      }
      results
      // xs.map { case (m, s) => MemberScore(m, s) }.asJava
    }
  }

  def zrevrange(key: String): Future[JList[String]] = zrevrange(key, 0, -1)

  def zrevrange(key: String, start: Long, end: Long): Future[JList[String]] =
    redis.zrevrange[String](key, start, end) map { _.asJava }

  def zrevrangeByScore(key: String): Future[JList[String]] =
    zrevrangeByScore(key, Double.PositiveInfinity, Double.NegativeInfinity)

  def zrevrangeByScore(key: String, offset: Long, count: Long): Future[JList[String]] =
    zrevrangeByScore(key, Double.PositiveInfinity, Double.NegativeInfinity, offset, count)

  def zrevrangeByScore(key: String, min: Double, max: Double): Future[JList[String]] =
    zrevrangeByScore(key, Limit(min), Limit(max))

  def zrevrangeByScore(key: String, min: Double, max: Double, offset: Long, count: Long): Future[JList[String]] =
    zrevrangeByScore(key, Limit(min), Limit(max), offset, count)

  def zrevrangeByScore(key: String, min: Limit, max: Limit): Future[JList[String]] =
    redis.zrevrangebyscore[String](key, min, max) map { _.asJava }

  def zrevrangeByScore(key: String, min: Limit, max: Limit, offset: Long, count: Long): Future[JList[String]] =
    redis.zrevrangebyscore[String](key, min, max, Some(offset, count)) map { _.asJava }

  def zrevrangeByScoreWithScores(key: String): Future[JList[MemberScore]] =
    zrevrangeByScoreWithScores(key, Double.NegativeInfinity, Double.PositiveInfinity)

  def zrevrangeByScoreWithScores(key: String, min: Double, max: Double): Future[JList[MemberScore]] =
    zrevrangeByScoreWithScores(key, Limit(min), Limit(max))

  def zrevrangeByScoreWithScores(key: String, min: Limit, max: Limit): Future[JList[MemberScore]] =
    redis.zrevrangebyscoreWithscores[String](key, min, max) map { xs =>
      val results = new util.ArrayList[MemberScore]()
      var i = 0
      while (i < xs.length) {
        val (m, s) = xs(i)
        results add MemberScore(m, s)
        i += 1
      }
      results
      // xs.map { case (m, s) => MemberScore(m, s) }.asJava
    }

  def zrevrangeWithScores(key: String): Future[JList[MemberScore]] =
    zrevrangeWithScores(key, 0, -1)

  def zrevrangeWithScores(key: String, start: Long, end: Long): Future[JList[MemberScore]] =
    redis.zrevrangeWithscores[String](key, start, end) map { xs =>
      val results = new util.ArrayList[MemberScore]()
      var i = 0
      while (i < xs.length) {
        val (m, s) = xs(i)
        results add MemberScore(m, s)
        i += 1
      }
      results
      // xs.map { case (m, s) => MemberScore(m, s) }.asJava
    }

  def zrank(key: String, member: String): Future[JLong] =
    redis.zrank(key, member) map toJLong

  def zrevrank(key: String, member: String): Future[JLong] =
    redis.zrevrank(key, member) map toJLong

  @varargs
  def zrem(key: String, members: String*): Future[JLong] =
    redis.zrem(key, members: _*).map(x => x)

  def zremRangeByRank(key: String, start: Long, end: Long): Future[JLong] =
    redis.zremrangebyrank(key, start, end).map(x => x)

  def zremRangeByScore(key: String, min: Double, max: Double): Future[JLong] =
    redis.zremrangebyscore(key, Limit(min), Limit(max)).map(x => x)

  def zremRangeByScore(key: String, min: Limit, max: Limit): Future[JLong] =
    redis.zremrangebyscore(key, min, max).map(x => x)


  def zscore(key: String, member: String): Future[JDouble] =
    redis.zscore(key, member) map toJDouble

  @varargs
  def zunionStore(destKey: String, key: String, keys: String*): Future[JLong] =
    zunionStore(destKey, SUM, key, keys: _*)

  @varargs
  def zunionStore(destKey: String, aggregate: Aggregate, key: String, keys: String*): Future[JLong] =
    redis.zunionstore(destKey, key, keys.toSeq, aggregate).map(x => x)

  def zunionStoreWeighted(destKey: String, keys: JMap[String, JDouble]): Future[JLong] = {
    val weightedKeys = keys.asScala.map { x => (x._1, x._2.toDouble) }.toMap
    redis.zunionstoreWeighted(destKey, weightedKeys, SUM).map(x => x)
  }

  def zunionStoreWeighted(destKey: String, keys: JMap[String, JDouble], aggregate: Aggregate): Future[JLong] = {
    val weightedKeys = keys.asScala.map { x => (x._1, x._2.toDouble) }.toMap
    redis.zunionstoreWeighted(destKey, weightedKeys, aggregate).map(x => x)
  }

  def bgRewriteAof(): Future[String] = redis.bgrewriteaof()

  def bgSave(): Future[String] = redis.bgsave()

  def clientKill(ip: String, port: Int): Future[JBool] =
    redis.clientKill(ip, port).map(x => x)

  def clientList(): Future[JList[JMap[String, String]]] =
    redis.clientList().map { xs => xs.map(m => m.asJava).asJava }

  def clientGetname(): Future[String] =
    redis.clientGetname() map { x => x.orNull }

  def clientSetname(connectionName: String): Future[JBool] =
    redis.clientSetname(connectionName).map(x => x)

  def configGet(parameter: String): Future[JMap[String, String]] =
    redis.configGet(parameter) map { xs => xs.asJava }

  def configSet(parameter: String, value: String): Future[JBool] =
    redis.configSet(parameter, value).map(x => x)

  def configResetstat(): Future[JBool] =
    redis.configResetstat().map(x => x)

  def dbSize(): Future[JLong] = redis.dbsize().map(x => x)

  def debugObject(key: String): Future[String] = redis.debugObject(key)

  def debugSegfault(): Future[String] = redis.debugSegfault()

  def flushAll(): Future[JBool] = redis.flushall().map(x => x)

  def flushDb(): Future[JBool] = redis.flushdb().map(x => x)

  def info(): Future[String] = redis.info()

  def info(section: String): Future[String] = redis.info(section)

  def lastSave(): Future[JLong] = redis.lastsave().map(x => x)

  def save(): Future[JBool] = redis.save().map(x => x)

  def shutdown(): Future[JBool] = redis.shutdown().map(x => x)

  def shutdown(modifier: ShutdownModifier): Future[JBool] =
    redis.shutdown(modifier).map(x => x)

  def slaveOf(host: String, port: Int): Future[JBool] =
    redis.slaveof(host, port).map(x => x)

  def slaveOffNoOne(): Future[JBool] = redis.slaveofNoOne().map(x => x)

  def publish(channel: String, value: String): Future[JLong] =
    redis.publish(channel, value).map(x => x)

}
