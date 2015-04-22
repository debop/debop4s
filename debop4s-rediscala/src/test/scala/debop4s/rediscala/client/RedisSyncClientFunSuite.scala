package debop4s.rediscala.client

import java.util

import akka.util.ByteString
import com.google.common.collect.Sets
import debop4s.core._
import debop4s.core.concurrent._
import debop4s.rediscala.MemberScore
import debop4s.rediscala.config.RedisConfiguration
import debop4s.rediscala.serializer.BinaryValueFormatter
import org.scalatest.{FunSuite, Matchers, OptionValues}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import redis.api.{DESC, Limit, LimitOffsetCount}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * RedisSyncClientTest
 * @author debop created at 2014. 4. 30.
 */
@ContextConfiguration(classes = Array(classOf[RedisConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class RedisSyncClientFunSuite extends FunSuite with Matchers with OptionValues {

  @Autowired val syncRedis: RedisSyncClient = null
  @Autowired val syncRedis2: RedisSyncClient = null

  // NOTE: Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  // NOTE: 기존 버전과 호환성을 위해 Binary Serializer 를 사용해야 합니다.
  // implicit val valueFormatter = new BinaryValueFormatter[Any]()

  test("connection") {
    syncRedis should not be null
  }

  test("ping") {
    syncRedis.ping shouldEqual "PONG"
  }

  test("select") {
    syncRedis.select(1) shouldEqual true
    syncRedis.select(0) shouldEqual true
  }

  test("select invalid db") {
    intercept[Exception] { syncRedis.select(-1) }
    intercept[Exception] { syncRedis.select(1000) }
  }

  test("delete key") {
    syncRedis.set("delKey", "value")
    syncRedis.del("delKey") shouldEqual 1
    syncRedis.del("delKeyNotExisting") shouldEqual 0
  }

  test("delete in transaction") {
    syncRedis.set("delTxKey1", "value1")
    syncRedis.set("delTxKey2", "value2")
    syncRedis.del("delTxKey1", "delTxKey2") shouldEqual 2
    syncRedis.del("delTxKeyNotExisting") shouldEqual 0

    syncRedis.set("delTxKey1", "value1")
    syncRedis.set("delTxKey2", "value2")
    syncRedis.del("delTxKey1", "delTxKey2") shouldEqual 2
    syncRedis.del("delTxKeyNotExisting") shouldEqual 0
  }

  test("dump") {
    syncRedis.set("dumpKey", "value")
    syncRedis.dump("dumpKey").get shouldEqual ByteString(0, 5, 118, 97, 108, 117, 101, 6, 0, 23, 27, -87, -72, 52, -1, -89, -3)
    syncRedis.del("dumpKey")
  }

  test("exists") {
    syncRedis.set("existsKey", "value")
    syncRedis.exists("existsKey") shouldEqual true
    syncRedis.exists("existsKeyNotExisting") shouldEqual false
    syncRedis.del("existsKey")
  }

  test("expire") {
    syncRedis.set("expireKey", "value")
    syncRedis.expire("expireKey", 1) // 1 seconds
    syncRedis.expire("expireKeyNotExists", 1)
    syncRedis.exists("expireKey") shouldEqual true

    Thread.sleep(1300)
    syncRedis.exists("expireKey") shouldEqual false
    syncRedis.del("expireKey")
  }

  test("expire at") {
    syncRedis.set("expireatKey", "value")
    syncRedis.expireAt("expireatKey", System.currentTimeMillis() / 1000 + 1)
    syncRedis.exists("expireatKey") shouldEqual true

    Thread.sleep(1300)
    syncRedis.exists("expireatKey") shouldEqual false
    syncRedis.del("expireatKey")
  }

  test("keys") {
    syncRedis.set("keysKey", "value")
    syncRedis.set("keysKey2", "value2")

    syncRedis.keys("keysKey*") should contain allOf("keysKey", "keysKey2")
    syncRedis.keys("keysKey?") should contain only "keysKey2"
    syncRedis.keys("keysKeyNoMatch") shouldEqual Seq()

    syncRedis.del("keysKey", "keysKey2")
  }

  test("move") {
    val redisMove: RedisSyncClient = RedisSyncClient()

    redisMove.select(1)

    val key: String = "migrateKey-" + System.currentTimeMillis
    syncRedis.set(key, "value")
    syncRedis.move(key, 1) shouldEqual true
    redisMove.get[String](key) shouldEqual Some("value")

    redisMove.del(key)
  }

  test("object refcount") {
    syncRedis.set("objectRefCount", "objectRefCountValue")
    syncRedis.objectRefcount("objectRefCount") shouldEqual Some(1)
    syncRedis.objectRefcount("objectRefCountNotFound") shouldEqual None
    syncRedis.del("objectRefCount")
  }

  test("object idletime") {
    syncRedis.set("objectIdletime", "value")
    syncRedis.objectIdletime("objectIdletime").get should be >= 0L
    syncRedis.objectIdletime("objectIdletimeNotExists") shouldEqual None
    syncRedis.del("objectIdletime")
  }

  test("object encoding") {
    syncRedis.set("objectEncoding", "objectEncodingValue")
    syncRedis.objectEncoding("objectEncoding") shouldEqual Some("embstr")
    syncRedis.objectEncoding("objectEncodingNotExists") shouldEqual None
    syncRedis.del("objectEncoding", "objectEncodingValue")
  }

  test("persist") {
    syncRedis.set("persistKey", "value")
    syncRedis.expire("persistKey", 10)

    Thread.sleep(50)
    syncRedis.ttl("persistKey") should (be >= 1L and be <= 10L)
    syncRedis.persist("persistKey") shouldEqual true
    syncRedis.ttl("persistKey") shouldEqual -1

    syncRedis.del("persistKey")
  }

  test("pexpire") {
    syncRedis.set("pexpireKey", "value") shouldEqual true
    syncRedis.pexpire("pexpireKey", 1100) shouldEqual true
    syncRedis.pexpire("pexpireKeyExisting", 1100) shouldEqual false

    syncRedis.get[String]("pexpireKey") shouldEqual Some("value")

    Thread.sleep(1500)
    syncRedis.get[String]("pexpireKey") should be('empty)
    syncRedis.del("pexpireKey")
  }

  test("pexpire at") {
    syncRedis.set("pexpireatKey", "value") shouldEqual true
    syncRedis.pexpireAt("pexpireatKey", System.currentTimeMillis() + 10) shouldEqual true
    syncRedis.pexpireAt("pexpireatKeyExisting", System.currentTimeMillis() + 10) shouldEqual false

    syncRedis.get[String]("pexpireatKey") shouldEqual Some("value")

    Thread.sleep(300)
    syncRedis.get[String]("pexpireatKey") should be('empty)
    syncRedis.del("pexpireatKey")
  }

  test("pttl") {
    syncRedis.set("pttlKey", "value") shouldEqual true
    syncRedis.expire("pttlKey", 1) shouldEqual true
    syncRedis.pttl("pttlKey") should (be >= 1L and be <= 1000L)
    syncRedis.del("pttlKey")
  }

  test("random key") {
    syncRedis.set("randomKey", "value") shouldEqual true
    syncRedis.randomKey should not be 'empty
    syncRedis.del("randomKey")
  }

  test("rename") {
    syncRedis.del("renameNewKey")
    syncRedis.set("renameKey", "value")
    syncRedis.rename("renameKey", "renameNewKey") shouldEqual true
    syncRedis.get[String]("renameNewKey") shouldEqual Some("value")

    syncRedis.del("renameNewKey")
  }

  test("rename not exists") {
    syncRedis.del("renamenxNewKey")
    syncRedis.set("renamenxKey", "value")
    syncRedis.set("renamenxNewKey", "value")
    syncRedis.renameNX("renamenxKey", "renamenxNewKey") shouldEqual false
    syncRedis.del("renamenxNewKey") shouldEqual 1
    syncRedis.renameNX("renamenxKey", "renamenxNewKey") shouldEqual true
    syncRedis.get[String]("renamenxNewKey") shouldEqual Some("value")
    syncRedis.del("renamenxNewKey")
  }

  test("restore") {
    syncRedis.set("restoreKey", "value") shouldEqual true
    val dump = syncRedis.dump("restoreKey")
    dump.get shouldEqual ByteString(0, 5, 118, 97, 108, 117, 101, 6, 0, 23, 27, -87, -72, 52, -1, -89, -3)
    syncRedis.del("restoreKey") shouldEqual 1
    syncRedis.restore("restoreKey", dump.get) shouldEqual true
    syncRedis.del("restoreKey")
  }

  test("sort") {
    syncRedis.hset("bonds|1", "bid_price", "96.01")
    syncRedis.hset("bonds|1", "ask_price", "97.53")
    syncRedis.hset("bonds|2", "bid_price", "95.50")
    syncRedis.hset("bonds|2", "ask_price", "98.25")
    syncRedis.del("bond_ids")
    syncRedis.sadd("bond_ids", "1")
    syncRedis.sadd("bond_ids", "2")
    syncRedis.del("sortAlpha")
    syncRedis.rpush("sortAlpha", "abc", "xyz")

    syncRedis.sort[String]("bond_ids") shouldEqual Seq("1", "2")
    syncRedis.sort[String]("bond_ids", order = Some(DESC)) shouldEqual Seq("2", "1")
    syncRedis.sort[String]("sortAlpha", alpha = true) shouldEqual Seq("abc", "xyz")
    syncRedis.sort[String]("bond_ids", limit = Some(LimitOffsetCount(0, 1))) shouldEqual Seq("1")

    syncRedis.sort[String]("bond_ids", byPattern = Some("bonds|*->bid_price")) shouldEqual Seq("2", "1")
    syncRedis.sort[String]("bond_ids",
      byPattern = Some("bonds|*->bid_price"),
      getPatterns = Seq("bonds|*->bid_price")) shouldEqual Seq("95.50", "96.01")
    syncRedis.sort[String]("bond_ids",
      byPattern = Some("bonds|*->bid_price"),
      getPatterns = Seq("bonds|*->bid_price", "#")) shouldEqual Seq("95.50", "2", "96.01", "1")

    syncRedis.sort[String]("bond_ids",
      byPattern = Some("bonds|*->bid_price"),
      limit = Some(LimitOffsetCount(0, 1))) shouldEqual Seq("2")
    syncRedis.sort[String]("bond_ids",
      byPattern = Some("bonds|*->bid_price"),
      order = Some(DESC)) shouldEqual Seq("1", "2")

    syncRedis.sort[String]("bond_ids",
      byPattern = Some("bonds|*->bid_price")) shouldEqual Seq("2", "1")

    syncRedis.sortStore("bond_ids",
      byPattern = Some("bonds|*->ask_price"),
      store = "bond_ids_sorted_by_ask_price") shouldEqual 2
    //                .isEqualTo(2);

    syncRedis.del("bonds|1", "bonds|2", "bond_ids", "bond_ids_sorted_by_ask_price")
  }

  test("ttl") {
    syncRedis.set("ttlKey", "value")
    syncRedis.expire("ttlKey", 10) shouldEqual true
    syncRedis.ttl("ttlKey") should (be >= 1L and be <= 10L)
    syncRedis.del("ttlKey")
  }

  test("kind") {
    syncRedis.set("typeKey", "value")
    syncRedis.`type`("typeKey") shouldEqual "string"
    syncRedis.`type`("typeKeyNotExisting") shouldEqual "none"

    syncRedis.del("typeKey")
  }

  test("incr") {
    syncRedis.del("incrKey", "incrKeyNotExists")
    syncRedis.set("incrKey", "0")
    syncRedis.incr("incrKey") shouldEqual 1L
    syncRedis.incr("incrKeyNotExists") shouldEqual 1L
    syncRedis.del("incrKey", "incrKeyNotExists")
  }

  test("setex") {
    syncRedis.setEx("setexKey", 1, "expireTest")
    syncRedis.exists("setexKey") shouldEqual true

    Thread.sleep(1300L)
    syncRedis.exists("setexKey") shouldEqual false
    syncRedis.del("setexKey")
  }

  test("hdel") {
    syncRedis.hset("hdelKey", "field", "value")
    syncRedis.hdel("hdelKey", "field", "fieldNotExisting") shouldEqual 1L
  }

  test("hexists") {
    syncRedis.del("hexistsKey")
    syncRedis.hset("hexistsKey", "field", "value")
    syncRedis.hexists("hexistsKey", "field") shouldEqual true
    syncRedis.hexists("hexistsKey", "notExistsFields") shouldEqual false
    syncRedis.del("hexistsKey")
  }

  test("hget") {
    syncRedis.del("hgetKey")
    syncRedis.hset("hgetKey", "field", "value")
    syncRedis.hget[String]("hgetKey", "field") shouldEqual Some("value")
    syncRedis.hget[String]("hgetKey", "fieldNotExisting") shouldEqual None
    syncRedis.del("hgetKey")
  }

  test("hincr by") {
    syncRedis.del("hincrby")

    val v = 10
    syncRedis.hset("hincrby", "field", v.toString)
    syncRedis.hincrBy("hincrby", "field", 1) shouldEqual (v + 1)
    syncRedis.hincrBy("hincrby", "field", -1) shouldEqual v
    syncRedis.del("hincrby")
  }

  test("hincr by float") {
    syncRedis.del("hincrByFloat")

    val v = 10.0D
    syncRedis.hset("hincrByFloat", "field", v.toString)
    syncRedis.hincrByFloat("hincrByFloat", "field", 0.1D) shouldEqual (v + 0.1)
    syncRedis.hincrByFloat("hincrByFloat", "field", -1.1D) shouldEqual (v - 1.0)
    syncRedis.del("hincrByFloat")
  }

  test("hkeys") {
    syncRedis.hset("hkeysKey", "field", "value")
    syncRedis.hset("hkeysKey", "field2", "value2")

    val values: Seq[String] = syncRedis.hkeys("hkeysKey")
    values shouldEqual Seq("field", "field2")

    syncRedis.del("hkeysKey")
  }

  test("hlen") {
    syncRedis.hset("hlenKey", "field", "value")
    syncRedis.hlen("hlenKey") shouldEqual 1L
    syncRedis.del("hlen")
  }

  test("hmget") {
    syncRedis.del("hgetallKey")
    syncRedis.hset("hgetallKey", "field", "value")

    val values = syncRedis.hmget[String]("hgetallKey", "field", "fieldNotExisting")
    values.size shouldEqual 2
    values(0) shouldEqual Some("value")
    values(1) shouldEqual None

    val notExisting = syncRedis.hmget[String]("hgetallKey", "fieldNotExisting")
    notExisting.size shouldEqual 1L
    notExisting(0) should be('empty)

    syncRedis.del("hgetallKey")
  }

  test("hmset") {
    syncRedis.del("hmsetKey")
    val map = mutable.HashMap[String, String]("field" -> "value", "field2" -> "value2")
    syncRedis.hmset("hmsetKey", map.toMap)
    syncRedis.hget[String]("hmsetKey", "field") shouldEqual Some("value")
    syncRedis.hget[String]("hmsetKey", "field2") shouldEqual Some("value2")
    syncRedis.del("hmsetKey")
  }

  test("hset") {
    syncRedis.del("setKey")
    syncRedis.hset("setKey", "field", "1")
    syncRedis.hget[String]("setKey", "field") shouldEqual Some("1")

    // FIELD 값이 없으면 0으로 가정합니다.
    syncRedis.hincrBy("setKey", "field2", 1)
    syncRedis.hget[String]("setKey", "field2") shouldEqual Some("1")
    syncRedis.hincrBy("setKey", "field2", 2)
    syncRedis.hget[String]("setKey", "field2") shouldEqual Some("3")

    syncRedis.del("setKey")
  }

  test("hset binary data by custom serializer") {

    implicit val valueFormatter = new BinaryValueFormatter[util.HashSet[String]]()
    // implicit val valueFormatter = new BinaryValueFormatter[Any]()

    val key = "setBinaryKey"
    syncRedis.del(key)

    val data: util.HashSet[String] = Sets.newHashSet("a", "b", "c")
    syncRedis.hset(key, "field", data)

    val loaded = syncRedis.hget[util.HashSet[String]](key, "field").get

    loaded shouldEqual data

    syncRedis.del(key)
  }

  test("hsetnx") {
    syncRedis.hdel("hsetnxKey", "field")
    syncRedis.hsetnx("hsetnxKey", "field", "value") shouldEqual true
    syncRedis.hsetnx("hsetnxKey", "field", "value2") shouldEqual false
    syncRedis.hget[String]("hsetnxKey", "field") shouldEqual Some("value")
    syncRedis.hdel("hsetnxKey", "field")
  }

  test("hvals") {
    syncRedis.hdel("hvalsKey", "field")
    syncRedis.hvals[String]("hvalsKey") should be('empty)
    syncRedis.hset("hvalsKey", "field", "value")
    syncRedis.hvals[String]("hvalsKey") shouldEqual Seq("value")
    syncRedis.del("hvalsKey")
  }

  test("lindex") {
    syncRedis.del("lindexKey")
    syncRedis.lpush("lindexKey", "World", "Hello")

    syncRedis.lindex[String]("lindexKey", 0) shouldEqual Some("Hello")
    syncRedis.lindex[String]("lindexKey", 1) shouldEqual Some("World")
    syncRedis.lindex[String]("lindexKey", 2) should be('empty)

    syncRedis.del("lindexKey")
  }

  test("linsert") {
    syncRedis.del("linsertKey")
    syncRedis.lpush("linsertKey", "World", "Hello")

    val length = syncRedis.linsertBefore("linsertKey", "World", "There")
    val list = syncRedis.lrange[String]("linsertKey", 0, -1)
    val length4 = syncRedis.linsertAfter("linsertKey", "World", "!!!")
    val list4 = syncRedis.lrange[String]("linsertKey", 0, -1)

    length shouldEqual 3
    list shouldEqual Seq("Hello", "There", "World")
    length4 shouldEqual 4
    list4 shouldEqual Seq("Hello", "There", "World", "!!!")

    syncRedis.del("linsertKey")
  }

  test("llen") {
    syncRedis.del("llenKey")
    syncRedis.lpush("llenKey", "World", "Hello")
    syncRedis.llen("llenKey") shouldEqual 2
    syncRedis.del("llenKey")
  }

  test("lpop") {
    syncRedis.del("lpopKey")
    syncRedis.rpush("lpopKey", "one", "two", "three")
    syncRedis.lpop[String]("lpopKey") shouldEqual Some("one")
    syncRedis.lpop[String]("lpopKey") shouldEqual Some("two")
    syncRedis.lpop[String]("lpopKey") shouldEqual Some("three")
    syncRedis.lpop[String]("lpopKey") shouldEqual None
    syncRedis.del("lpopKey")
  }

  test("lpush") {
    syncRedis.del("lpushKey")
    syncRedis.lpush("lpushKey", "1", "2", "3", "4")
    syncRedis.llen("lpushKey") shouldEqual 4
    syncRedis.lpop[String]("lpushKey") shouldEqual Some("4")
    syncRedis.llen("lpushKey") shouldEqual 3
    syncRedis.del("lpushKey")
  }

  test("lpushx") {
    syncRedis.del("lpushxKey", "lpushxKeyOther")

    syncRedis.rpush("lpushxKey", "World") shouldEqual 1
    syncRedis.lpushx("lpushxKey", "Hello") shouldEqual 2
    syncRedis.lpushx("lpushxKeyOther", "Hello") shouldEqual 0
    syncRedis.lrange[String]("lpushxKey", 0, -1) shouldEqual Seq("Hello", "World")
    syncRedis.lrange[String]("lpushxKeyOther", 0, -1) should be('empty)

    syncRedis.del("lpushxKey", "lpushxKeyOther")
  }

  test("lrange") {
    syncRedis.del("lrangeKey")
    syncRedis.rpush("lrangeKey", "one", "two", "three")
    syncRedis.lrange[String]("lrangeKey", 0, 0) shouldEqual Seq("one")
    syncRedis.lrange[String]("lrangeKey", -3, 2) shouldEqual Seq("one", "two", "three")
    syncRedis.lrange[String]("lrangeKey", 5, 10) shouldEqual Seq()
    syncRedis.lrange[String]("lrangeKeyNonExisting", 0, -1) shouldEqual Seq()
    syncRedis.del("lrangeKey")
  }

  test("lrem") {
    syncRedis.del("lremKey")
    syncRedis.rpush("lremKey", "hello", "hello", "foo", "hello")

    // count < 0 이면 뒤에서 앞으로, >0 이면 앞에서 뒤로, =0 이면 일치하는 모든 것
    syncRedis.lrem("lremKey", -2, "hello") shouldEqual 2
    syncRedis.lrange[String]("lremKey", 0, -1) shouldEqual Seq("hello", "foo")

    syncRedis.del("lremKey")
  }

  test("lset") {
    syncRedis.del("lsetKey")
    syncRedis.rpush("lsetKey", "one", "two", "three")
    syncRedis.lset("lsetKey", 0, "four") shouldEqual true
    syncRedis.lset("lsetKey", -2, "five") shouldEqual true
    syncRedis.lrange[String]("lsetKey", 0, -1) shouldEqual Seq("four", "five", "three")
    syncRedis.del("lsetKey")
  }

  test("ltrim") {
    syncRedis.del("ltrimKey")
    syncRedis.rpush("ltrimKey", "one", "two", "three")
    syncRedis.ltrim("ltrimKey", 1, -1) shouldEqual true
    syncRedis.lrange[String]("ltrimKey", 0, -1) shouldEqual Seq("two", "three")
    syncRedis.del("ltrimKey")
  }

  test("rpop") {
    syncRedis.del("rpopKey")
    syncRedis.rpush("rpopKey", "one", "two", "three")
    syncRedis.rpop[String]("rpopKey") shouldEqual Some("three")
    syncRedis.lrange[String]("rpopKey", 0, -1) shouldEqual Seq("one", "two")
    syncRedis.del("rpopKey")
  }

  test("rpoplpush") {
    syncRedis.del("rpoplpushKey", "rpoplpushKeyOther")

    syncRedis.rpush("rpoplpushKey", "one", "two", "three")
    syncRedis.rpoplpush("rpoplpushKey", "rpoplpushKeyOther")

    syncRedis.lrange[String]("rpoplpushKey", 0, -1) shouldEqual Seq("one", "two")
    syncRedis.lrange[String]("rpoplpushKeyOther", 0, -1) shouldEqual Seq("three")

    syncRedis.del("rpoplpushKey", "rpoplpushKeyOther")
  }

  test("rpushx") {
    syncRedis.del("rpushxKey", "rpushxKeyOther")

    syncRedis.rpush("rpushxKey", "hello") shouldEqual 1
    syncRedis.rpushx("rpushxKey", "world") shouldEqual 2
    syncRedis.rpushx("rpushxKeyOther", "world") shouldEqual 0
    syncRedis.lrange[String]("rpushxKey", 0, -1) shouldEqual Seq("hello", "world")
    syncRedis.lrange[String]("rpushxKeyOther", 0, -1).isEmpty shouldEqual true

    syncRedis.del("rpushxKey", "rpushxKeyOther")
  }

  test("blpop already containing elements") {
    syncRedis.del("blpop1", "blpop2")
    syncRedis.rpush("blpop1", "a", "b", "c")

    val (key, msg) = syncRedis2.blpop[String](Seq("blpop1", "blpop2"), 60).get
    key should not be null
    key shouldEqual "blpop1"
    msg shouldEqual "a"

    syncRedis.del("blpop1", "blpop2")
  }

  test("blpop blocking") {
    syncRedis2.del("blpopBlock")
    val future = Future {
      syncRedis2.blpop[String](Seq("blpopBlock", "blpopBlock2"), 60)
    }

    Thread.sleep(1000)
    syncRedis.rpush("blpopBlock", "a", "b", "c")

    val (key, msg) = future.await.get
    future.isCompleted shouldEqual true
    key shouldEqual "blpopBlock"
    msg shouldEqual "a"

    syncRedis2.del("blpopBlock")
  }

  test("blpop blocking timeout") {
    syncRedis2.del("blpopBlockTimeout")
    val future = Future {
      syncRedis2.blpop[String](Seq("blpopBlockTimeout"), 1)
    }
    // Timeout 이 되면 null 을 반환합니다.
    future.await shouldEqual None
    syncRedis2.del("blpopBlockTimeout")
  }

  test("brpop already containing elements") {
    syncRedis.del("brpop1", "brpop2")
    syncRedis.rpush("brpop1", "a", "b", "c")

    val results = syncRedis2.brpop[String](Seq("brpop1", "brpop2"), 60).get
    results should not be null
    results._1 shouldEqual "brpop1"
    results._2 shouldEqual "c"
    syncRedis.del("brpop1", "brpop2")
  }

  test("brpop blocking") {
    syncRedis2.del("brpopBlock")

    val future: Future[(String, String)] = Future {
      syncRedis.brpop[String](Seq("brpopBlock", "brpopBlock2"), 60).get
    }

    Thread.sleep(1000)
    syncRedis.rpush("brpopBlock", "a", "b", "c")

    val results: (String, String) = future.await
    results should not be null
    results._1 shouldEqual "brpopBlock"
    results._2 shouldEqual "c"
    syncRedis2.del("brpopBlock")
  }

  test("brpop blocking timeout") {
    syncRedis2.del("brpopBlockTimeout")
    val future = Future {
      syncRedis.brpop[String](Seq("brpopBlockTimeout"), 1)
    }
    // Timeout 이 되면 null 을 반환합니다.
    future.await shouldEqual None

    syncRedis2.del("brpopBlockTimeout")
  }

  test("brpoplpush already containing elements") {
    syncRedis.del("brpoplpush1", "brpoplpush2")
    syncRedis.rpush("brpoplpush1", "a", "b", "c")
    val value = syncRedis.brpoplpush[String]("brpoplpush1", "brpoplpush2", 60)
    value shouldEqual Some("c")

    syncRedis.del("brpoplpush1", "brpoplpush2")
  }

  test("brpoplpush blocking") {
    syncRedis.del("brpoplpushBlock1", "brpoplpushBlock2")

    val future: Future[String] = Future {
      syncRedis2.brpoplpush[String]("brpoplpushBlock1", "brpoplpushBlock2", 60).get
    }
    Thread.sleep(1000)
    syncRedis.rpush("brpoplpushBlock1", "a", "b", "c")

    future.await shouldEqual "c"
    syncRedis.del("brpoplpushBlock1", "brpoplpushBlock2")
  }

  test("brpoplpush blocking timeout") {
    syncRedis.del("brpoplpushTimeout1", "brpoplpushTimeout2")

    val future = Future {
      syncRedis2.brpoplpush[String]("brpoplpushTimeout1", "brpoplpushTimeout2", 1)
    }

    // Timeout 이 되면 None 을 반환합니다.
    future.await should not be defined
    syncRedis.del("brpoplpushTimeout1", "brpoplpushTimeout2")
  }

  test("sadd") {
    syncRedis.del("saddKey")
    syncRedis.sadd("saddKey", "Hello", "World") shouldEqual 2
    syncRedis.sadd("saddKey", "Hello") shouldEqual 0
    syncRedis.smembers[String]("saddKey") should contain allOf("Hello", "World")
    syncRedis.del("saddKey")
  }

  test("scard") {
    syncRedis.del("scardKey")
    syncRedis.scard("scardKey") shouldEqual 0
    syncRedis.sadd("scardKey", "Hello", "World") shouldEqual 2
    syncRedis.scard("scardKey") shouldEqual 2
    syncRedis.del("scardKey")
  }

  test("sdiff") {
    syncRedis.del("sdiffKey1", "sdiffKey2")
    syncRedis.sadd("sdiffKey1", "a", "b", "c")
    syncRedis.sadd("sdiffKey2", "c", "d", "e")
    syncRedis.sdiff[String]("sdiffKey1", "sdiffKey2") should contain allOf("a", "b")
    syncRedis.del("sdiffKey1", "sdiffKey2")
  }

  test("sdiffStore") {
    syncRedis.del("sdiffstoreKey1", "sdiffstoreKey2", "sdiffstoreKey")
    syncRedis.sadd("sdiffstoreKey1", "a", "b", "c")
    syncRedis.sadd("sdiffstoreKey2", "c", "d", "e")
    syncRedis.sdiffStore("sdiffstoreKey", "sdiffstoreKey1", "sdiffstoreKey2") shouldEqual 2
    syncRedis.smembers[String]("sdiffstoreKey") should contain allOf("a", "b")
    syncRedis.del("sdiffstoreKey1", "sdiffstoreKey2", "sdiffstoreKey")
  }

  test("sinter") {
    syncRedis.del("sinterKey1", "sinterKey2")
    syncRedis.sadd("sinterKey1", "a", "b", "c")
    syncRedis.sadd("sinterKey2", "c", "d", "e")
    syncRedis.sinter[String]("sinterKey1", "sinterKey2") shouldEqual Seq("c")
    syncRedis.del("sinterKey1", "sinterKey2")
  }

  test("sinterStore") {
    syncRedis.del("sinterstoreKey1", "sinterstoreKey2", "sinterstoreKey")
    syncRedis.sadd("sinterstoreKey1", "a", "b", "c")
    syncRedis.sadd("sinterstoreKey2", "c", "d", "e")
    syncRedis.sinterStore("sinterstoreKey", "sinterstoreKey1", "sinterstoreKey2") shouldEqual 1
    syncRedis.smembers[String]("sinterstoreKey") shouldEqual Seq("c")
    syncRedis.del("sinterstoreKey1", "sinterstoreKey2", "sinterstoreKey")
  }

  test("sismember") {
    syncRedis.del("sismemberKey")
    syncRedis.sadd("sismemberKey", "hello", "world")
    syncRedis.sisMember("sismemberKey", "hello") shouldEqual true
    syncRedis.sisMember("sismemberKey", "world") shouldEqual true
    syncRedis.sisMember("sismemberKey", "notMember") shouldEqual false
    syncRedis.del("sismemberKey")
  }

  test("smove") {
    syncRedis.del("smoveKey1", "smoveKey2")
    syncRedis.sadd("smoveKey1", "one", "two")
    syncRedis.sadd("smoveKey2", "three")
    syncRedis.smove("smoveKey1", "smoveKey2", "two") shouldEqual true
    syncRedis.smove("smoveKey1", "smoveKey2", "notExisting") shouldEqual false
    syncRedis.smembers[String]("smoveKey1") shouldEqual Seq("one")
    syncRedis.smembers[String]("smoveKey2") should contain allOf("two", "three")
    syncRedis.del("smoveKey1", "smoveKey2")
  }

  test("spop") {
    syncRedis.del("spopKey")
    val values = Seq("one", "two", "three")
    syncRedis.sadd("spopKey", values: _*)
    values contains syncRedis.spop[String]("spopKey").get shouldEqual true
    syncRedis.spop[String]("spopKeyNonExisting").isEmpty shouldEqual true
    syncRedis.del("spopKey")
  }

  test("srandmember") {
    syncRedis.del("srandmemberKey")
    val values = Seq("one", "two", "three")
    syncRedis.sadd("srandmemberKey", values: _*)
    values contains syncRedis.srandmember[String]("srandmemberKey").get shouldEqual true
    syncRedis.srandmember("srandmemberKeyNotExisting").isEmpty shouldEqual true
    syncRedis.del("srandmemberKey")
  }

  test("srem") {
    syncRedis.del("sremKey")
    val values = Seq("one", "two", "three", "four")
    syncRedis.sadd("sremKey", values: _*)
    syncRedis.srem("sremKey", "one", "four") shouldEqual 2
    syncRedis.srem("sremKey", "five") shouldEqual 0
    syncRedis.smembers[String]("sremKey") should contain allOf("two", "three")
    syncRedis.del("sremKey")
  }

  test("sunion") {
    syncRedis.del("sunionKey1", "sunionKey2")
    syncRedis.sadd("sunionKey1", "a", "b", "c")
    syncRedis.sadd("sunionKey2", "c", "d", "e")
    syncRedis.sunion[String]("sunionKey1", "sunionKey2") should contain allOf("a", "b", "c", "d", "e")
    syncRedis.del("sunionKey1", "sunionKey2")
  }

  test("sunionStore") {
    syncRedis.del("sunionstoreKey1", "sunionstoreKey2", "sunionstoreKey")
    syncRedis.sadd("sunionstoreKey1", "a", "b", "c")
    syncRedis.sadd("sunionstoreKey2", "c", "d", "e")
    syncRedis.sunionStore("sunionstoreKey", "sunionstoreKey1", "sunionstoreKey2") shouldEqual 5
    syncRedis.smembers[String]("sunionstoreKey") should contain allOf("a", "b", "c", "d", "e")
    syncRedis.del("sunionstoreKey1", "sunionstoreKey2", "sunionstoreKey")
  }

  test("zadd") {
    syncRedis.del("zaddKey")
    syncRedis.zadd("zaddKey", 1.0, "one") shouldEqual 1
    syncRedis.zadd("zaddKey", 1, "uno") shouldEqual 1
    syncRedis.zadd("zaddKey", 2, "two") shouldEqual 1
    syncRedis.zrangeWithScores("zaddKey", 0, -1) should contain allOf(
      new MemberScore("one", 1.0), new MemberScore("uno", 1.0), new MemberScore("two", 2.0)
      )
    syncRedis.del("zaddKey")
  }

  test("zcard") {
    syncRedis.del("zcardKey")
    syncRedis.zcard("zcardKey") shouldEqual 0

    syncRedis.zadd("zcardKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0))
    syncRedis.zcard("zcardKey") shouldEqual 2
    syncRedis.zcard("zcardKeyNonExisting") shouldEqual 0

    syncRedis.del("zcardKey")
  }

  test("zcount") {
    syncRedis.del("zcountKey")
    syncRedis.zcount("zcountKey") shouldEqual 0

    syncRedis.zadd("zcountKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zcount("zcountKey") shouldEqual 3
    syncRedis.zcount("zcountKey", 1.1, 3) shouldEqual 2

    syncRedis.del("zcountKey")
  }

  test("zincrby") {
    syncRedis.del("zincrbyKey")
    syncRedis.zadd("zincrbyKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0))
    syncRedis.zincrBy("zincrbyKey", 2.1, "one") shouldEqual 3.1
    syncRedis.zincrBy("zincrbyKey", 2.1, "notExisting") shouldEqual 2.1
    syncRedis.zrangeWithScores("zincrbyKey", 0, -1) shouldEqual Seq(new MemberScore("two", 2.0), new MemberScore("notExisting", 2.1), new MemberScore("one", 3.1))
    syncRedis.del("zincrbyKey")
  }

  test("zinterStore") {
    syncRedis.del("zinterstoreKey", "zinterstoreKeyOutWeighted", "zinterstoreKey1", "zinterstoreKey2")

    syncRedis.zadd("zinterstoreKey1", new MemberScore("one", 1.0), new MemberScore("two", 2.0))
    syncRedis.zadd("zinterstoreKey2", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zinterStore("zinterstoreKey", "zinterstoreKey1", Seq("zinterstoreKey2")) shouldEqual 2

    val weights = mutable.HashMap("zinterstoreKey1" -> 2.0, "zinterstoreKey2" -> 3.0)
    val ziw: Long = syncRedis.zinterStoreWeighted("zinterstoreKeyOutWeighted", weights.toMap)
    ziw shouldEqual 2
    syncRedis.zrangeWithScores("zinterstoreKey", 0, -1) shouldEqual Seq(new MemberScore("one", 2.0), new MemberScore("two", 4.0))
    syncRedis.zrangeWithScores("zinterstoreKeyOutWeighted", 0, -1) shouldEqual Seq(new MemberScore("one", 5.0), new MemberScore("two", 10.0))

    syncRedis.del("zinterstoreKey", "zinterstoreKeyOutWeighted", "zinterstoreKey1", "zinterstoreKey2")
  }

  test("zrange") {
    syncRedis.del("zrangeKey")
    syncRedis.zadd("zrangeKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zrange[String]("zrangeKey", 0, -1) shouldEqual Seq("one", "two", "three")
    syncRedis.zrange[String]("zrangeKey", 2, 3) shouldEqual Seq("three")
    syncRedis.zrange[String]("zrangeKey", -2, -1) shouldEqual Seq("two", "three")
    syncRedis.del("zrangeKey")
  }

  test("zrange by score") {
    syncRedis.del("zrangebyscoreKey")
    syncRedis.zadd("zrangebyscoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))

    syncRedis.zrangeByScore[String]("zrangebyscoreKey", Double.NegativeInfinity, Double.PositiveInfinity) shouldEqual Seq("one", "two", "three")
    syncRedis.zrangeByScore[String]("zrangebyscoreKey", Double.NegativeInfinity, Double.PositiveInfinity, Some(1L, 2L)) shouldEqual Seq("two", "three")
    syncRedis.zrangeByScore[String]("zrangebyscoreKey", Limit(1.0), Limit(2.0)) shouldEqual Seq("one", "two")
    syncRedis.zrangeByScoreWithScores("zrangebyscoreKey", Limit(1.0), Limit(2.0)) shouldEqual Seq(new MemberScore("one", 1.0), new MemberScore("two", 2.0))

    // not include
    syncRedis.zrangeByScore[String]("zrangebyscoreKey", new Limit(1.0, false), new Limit(2.0, true)) shouldEqual Seq("two")
    // not include
    syncRedis.zrangeByScore[String]("zrangebyscoreKey", new Limit(1.0, false), new Limit(2.0, false)) shouldEqual Seq()

    syncRedis.del("zrangebyscoreKey")
  }

  test("zrank") {
    syncRedis.del("zrankKey")
    syncRedis.zadd("zrankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))


    // rank 는 0부터
    syncRedis.zrank("zrankKey", "three") shouldEqual Some(2)
    syncRedis.zrank("zrankKey", "two") shouldEqual Some(1)
    syncRedis.zrank("zrankKey", "one") shouldEqual Some(0)
    syncRedis.zrank("zrankKey", "notExisting") shouldEqual None

    syncRedis.del("zrankKey")
  }

  test("zrem") {
    syncRedis.del("zremKey")

    syncRedis.zadd("zremKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zrem("zremKey", "two", "nonexisting") shouldEqual 1
    syncRedis.zrange[String]("zremKey", 0, -1) shouldEqual Seq("one", "three")

    syncRedis.del("zremKey")
  }

  test("zremRange by rank") {
    syncRedis.del("zremRangeByRankKey")

    syncRedis.zadd("zremRangeByRankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zremRangeByRank("zremRangeByRankKey", 0, 1) shouldEqual 2
    syncRedis.zrange[String]("zremRangeByRankKey", 0, -1) shouldEqual Seq("three")

    syncRedis.del("zremRangeByRankKey")
  }

  test("zremRange by score") {
    syncRedis.del("zremRangeByScoreKey")

    syncRedis.zadd("zremRangeByScoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zremRangeByScore("zremRangeByScoreKey", 1.0, 2.0) shouldEqual 2
    syncRedis.zrange[String]("zremRangeByScoreKey", 0, -1) shouldEqual Seq("three")

    syncRedis.del("zremRangeByScoreKey")
  }

  test("zrevrange") {
    syncRedis.del("zrevrangeKey")

    syncRedis.zadd("zrevrangeKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zrevrange[String]("zrevrangeKey", 0, -1) shouldEqual Seq("three", "two", "one")
    syncRedis.zrevrange[String]("zrevrangeKey", 2, 3) shouldEqual Seq("one")
    syncRedis.zrevrange[String]("zrevrangeKey", -2, -1) shouldEqual Seq("two", "one")

    syncRedis.del("zrevrangeKey")
  }

  test("zrevrange by score") {
    syncRedis.del("zrevrangeByScoreKey")

    syncRedis.zadd("zrevrangeByScoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zrevrangeByScore[String]("zrevrangeByScoreKey", Double.PositiveInfinity, Double.NegativeInfinity) shouldEqual Seq("three", "two", "one")
    syncRedis.zrevrangeByScore[String]("zrevrangeByScoreKey", Limit(2.0), Limit(1.0)) shouldEqual Seq("two", "one")
    syncRedis.zrevrangeByScoreWithScores("zrevrangeByScoreKey", Limit(2.0), Limit(1.0)) shouldEqual Seq(new MemberScore("two", 2.0), new MemberScore("one", 1.0))

    // not include
    syncRedis.zrevrangeByScore[String]("zrevrangeByScoreKey", new Limit(2.0, true), new Limit(1.0, false)) shouldEqual Seq("two")

    // not include
    syncRedis.zrevrangeByScore[String]("zrevrangeByScoreKey", new Limit(2.0, false), new Limit(1.0, false)) shouldEqual Seq()

    syncRedis.del("zrevrangeByScoreKey")
  }

  test("zrevrank") {
    syncRedis.del("zrevrankKey")
    syncRedis.zadd("zrevrankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))

    // rank 는 0부터
    syncRedis.zrevrank("zrevrankKey", "three") shouldEqual Some(0)
    syncRedis.zrevrank("zrevrankKey", "two") shouldEqual Some(1)
    syncRedis.zrevrank("zrevrankKey", "one") shouldEqual Some(2)
    syncRedis.zrevrank("zrevrankKey", "notExisting") shouldEqual None

    syncRedis.del("zrevrankKey")
  }

  test("zscore") {
    syncRedis.del("zscoreKey")
    syncRedis.zadd("zscoreKey", new MemberScore("one", 1.1), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zscore("zscoreKey", "one") shouldEqual Some(1.1)
    syncRedis.zscore("zscoreKey", "nonExisting") shouldEqual None
    syncRedis.del("zscoreKey")
  }

  test("zunionstore") {
    syncRedis.del("zunionstoreKey", "zunionstoreKeyOutWeighted", "zunionstoreKey1", "zunionstoreKey2")
    syncRedis.zadd("zunionstoreKey1", new MemberScore("one", 1.0), new MemberScore("two", 2.0))
    syncRedis.zadd("zunionstoreKey2", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0))
    syncRedis.zunionStore("zunionstoreKey", "zunionstoreKey1", Seq("zunionstoreKey2")) shouldEqual 3

    val weights = mutable.HashMap("zunionstoreKey1" -> 2.0, "zunionstoreKey2" -> 3.0)
    val ziw: Long = syncRedis.zunionStoreWeighted("zunionstoreKeyOutWeighted", weights.toMap)
    ziw shouldEqual 3

    syncRedis.zrangeWithScores("zunionstoreKey", 0, -1) shouldEqual
    Seq(new MemberScore("one", 2.0), new MemberScore("three", 3.0), new MemberScore("two", 4.0))

    syncRedis.zrangeWithScores("zunionstoreKeyOutWeighted", 0, -1) shouldEqual
    Seq(new MemberScore("one", 5.0), new MemberScore("three", 9.0), new MemberScore("two", 10.0))

    syncRedis.del("zunionstoreKey", "zunionstoreKeyOutWeighted", "zunionstoreKey1", "zunionstoreKey2")
  }
}
