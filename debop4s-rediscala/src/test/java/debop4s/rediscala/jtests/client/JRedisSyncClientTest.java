//package debop4s.rediscala.jtests.client;
//
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import kr.hconnect.core.concurrent.JavaAsyncs;
//import kr.hconnect.core.tools.ArrayTool;
//import kr.hconnect.redis.MemberScore;
//import debop4s.rediscala.client.JRedisClient;
//import debop4s.rediscala.client.JRedisSyncClient;
//import debop4s.rediscala.config.RedisConfiguration;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import redis.api.*;
//import scala.Tuple2;
//
//import java.util.*;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Future;
//
//import static com.google.common.collect.Lists.newArrayList;
//import static com.google.common.collect.Sets.newHashSet;
//import static java.lang.Double.NEGATIVE_INFINITY;
//import static java.lang.Double.POSITIVE_INFINITY;
//import static java.lang.System.currentTimeMillis;
//import static org.fest.assertions.Assertions.assertThat;
//
///**
// * JSyncRedisClientTest
// *
// * @author debop
// *         created at 2014. 4. 28.
// */
//@Slf4j
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { RedisConfiguration.class })
//public class JRedisSyncClientTest {
//
//    @Autowired JRedisClient jredis;
//    @Autowired JRedisSyncClient jsyncRedis;
//    @Autowired JRedisSyncClient jsyncRedis2;
//
//    @Test
//    public void connectionTest() {
//        assertThat(jredis).isNotNull();
//        assertThat(jsyncRedis).isNotNull();
//
//        assertThat(jsyncRedis.ping()).isEqualToIgnoringCase("PONG");
//    }
//
//    @Test
//    public void echo() throws Exception {
//        final String hello = "Hello World! 안녕하세요.";
//        assertThat(jsyncRedis.echo(hello)).isEqualTo(hello);
//    }
//
//    @Test
//    public void ping() throws Exception {
//        assertThat(jsyncRedis.ping()).isEqualToIgnoringCase("PONG");
//    }
//
//    @Test
//    public void select() throws Exception {
//        assertThat(jsyncRedis.select(1)).isTrue();
//        assertThat(jsyncRedis.select(0)).isTrue();
//    }
//
//    @Test(expected = Exception.class)
//    public void selectInvalidDB() {
//        jsyncRedis.select(-1);
//    }
//
//    @Test(expected = Exception.class)
//    public void selectInvalidDB2() {
//        jsyncRedis.select(10000);
//    }
//
//    @Test
//    public void del() throws Exception {
//        jsyncRedis.set("delKey", "value");
//        assertThat(jsyncRedis.del("delKey")).isEqualTo(1);
//        assertThat(jsyncRedis.del("delKeyNotExisting")).isEqualTo(0);
//    }
//
//    @Test
//    public void delTx() throws Exception {
//        jsyncRedis.set("delTxKey1", "value1");
//        jsyncRedis.set("delTxKey2", "value2");
//        assertThat(jsyncRedis.del("delTxKey1", "delTxKey2")).isEqualTo(2);
//        assertThat(jsyncRedis.del("delTxKeyNotExisting")).isEqualTo(0);
//
//        jsyncRedis.set("delTxKey1", "value1");
//        jsyncRedis.set("delTxKey2", "value2");
//        assertThat(jsyncRedis.del(Sets.newHashSet("delTxKey1", "delTxKey2"))).isEqualTo(2);
//        assertThat(jsyncRedis.del("delTxKeyNotExisting")).isEqualTo(0);
//    }
//
//    @Test
//    public void dump() throws Exception {
//        jsyncRedis.set("dumpKey", "value");
//        assertThat(jsyncRedis.dump("dumpKey"))
//                .isEqualTo(new byte[] { 0, 5, 118, 97, 108, 117, 101, 6, 0, 23, 27, -87, -72, 52, -1, -89, -3 });
//        jsyncRedis.del("dumpKey");
//    }
//
//    @Test
//    public void exists() throws Exception {
//        jsyncRedis.set("existsKey", "value");
//        assertThat(jsyncRedis.exists("existsKey")).isTrue();
//        assertThat(jsyncRedis.exists("existKeyNotExisting")).isFalse();
//        jsyncRedis.del("existsKey");
//    }
//
//    @Test
//    public void expire() throws Exception {
//        jsyncRedis.set("expireKey", "value");
//        jsyncRedis.expire("expireKey", 1);       // 1 seconds
//        jsyncRedis.expire("expireKeyNotExists", 1);
//        assertThat(jsyncRedis.exists("expireKey")).isTrue();
//        Thread.sleep(1300);
//        assertThat(jsyncRedis.exists("expireKey")).isFalse();
//        jsyncRedis.del("expireKey");
//    }
//
//    @Test
//    public void expireAt() throws Exception {
//        jsyncRedis.set("expireatKey", "value");
//        jsyncRedis.expireAt("expireatKey", System.currentTimeMillis() / 1000 + 1);
//        assertThat(jsyncRedis.exists("expireatKey")).isTrue();
//
//        Thread.sleep(1100);
//        assertThat(jsyncRedis.exists("expireatKey")).isFalse();
//        jsyncRedis.del("expireatKey");
//    }
//
//    @Test
//    public void keys() throws Exception {
//        jsyncRedis.set("keysKey", "value");
//        jsyncRedis.set("keysKey2", "value2");
//        assertThat(jsyncRedis.keys("keysKey*")).isEqualTo(Sets.newHashSet("keysKey", "keysKey2"));
//        assertThat(jsyncRedis.keys("keysKey?")).isEqualTo(Sets.newHashSet("keysKey2"));
//        assertThat(jsyncRedis.keys("keysKeyNoMatch")).isEqualTo(new HashSet<>());
//
//        jsyncRedis.del("keysKey", "keysKey2");
//    }
//
////    @Test
////    @Ignore("migrate 는 다른 server 간에 수행해야 합니다.")
////    public void migrate() throws Exception {
////        JSyncRedisClient redisMigrate = JSyncRedisClient.apply();
////        redisMigrate.select(1);
////
////        final String key = "migrateKey-" + currentTimeMillis();
////        redisMigrate.del(key);
////        jsyncRedis.set(key, "value");
////        jsyncRedis.migrate("localhost", 6379, key, 1, 1000);
////        assertThat(redisMigrate.get(key)).isEqualTo("value");
////    }
//
//    @Test
//    public void move() throws Exception {
//        JRedisSyncClient redisMove = JRedisSyncClient.apply();
//        redisMove.select(1);
//
//        final String key = "migrateKey-" + currentTimeMillis();
//        jsyncRedis.set(key, "value");
//        assertThat(jsyncRedis.move(key, 1)).isTrue();
//        assertThat(redisMove.get(key)).isEqualTo("value");
//
//        redisMove.del(key);
//    }
//
//    @Test
//    public void object_refcount() throws Exception {
//        jsyncRedis.set("objectRefCount", "objectRefCountValue");
//        assertThat(jsyncRedis.objectRefcount("objectRefCount")).isEqualTo(1);
//        assertThat(jsyncRedis.objectRefcount("objectRefCountNotFound")).isNull();
//        jsyncRedis.del("objectRefCount");
//    }
//
//    @Test
//    public void object_idletime() throws Exception {
//        jsyncRedis.set("objectIdletime", "value");
//        assertThat(jsyncRedis.objectIdletime("objectIdletime")).isGreaterThanOrEqualTo(0);
//        assertThat(jsyncRedis.objectIdletime("objectIdletimeNotExists")).isNull();
//        jsyncRedis.del("objectIdletime");
//    }
//
//    @Test
//    public void object_encoding() throws Exception {
//        jsyncRedis.set("objectEncoding", "objectEncodingValue");
//        assertThat(jsyncRedis.objectEncoding("objectEncoding")).isEqualTo("raw");
//        assertThat(jsyncRedis.objectEncoding("objectEncodingNotExists")).isNullOrEmpty();
//
//        jsyncRedis.del("objectEncoding", "objectEncodingValue");
//    }
//
//    @Test
//    public void persist() throws Exception {
//        jsyncRedis.set("persistKey", "value");
//        jsyncRedis.expire("persistKey", 10);
//
//        Thread.sleep(50);
//        assertThat(jsyncRedis.ttl("persistKey")).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(10);
//        assertThat(jsyncRedis.persist("persistKey")).isTrue();
//        assertThat(jsyncRedis.ttl("persistKey")).isEqualTo(-1);
//
//        jsyncRedis.del("persistKey");
//    }
//
//    @Test
//    public void pexpire() throws Exception {
//        assertThat(jsyncRedis.set("pexpireKey", "value")).isTrue();
//        assertThat(jsyncRedis.pexpire("pexpireKey", 1100)).isTrue();
//        assertThat(jsyncRedis.pexpire("pexpireKeyExisting", 1100)).isFalse();
//
//        assertThat(jsyncRedis.get("pexpireKey")).isEqualTo("value");
//
//        Thread.sleep(1300);
//        assertThat(jsyncRedis.get("pexpireKey")).isNullOrEmpty();
//
//        jsyncRedis.del("pexpireKey");
//    }
//
//    @Test
//    public void pexpireAt() throws Exception {
//        assertThat(jsyncRedis.set("pexpireatKey", "value")).isTrue();
//        assertThat(jsyncRedis.pexpireAt("pexpireatKey", currentTimeMillis() + 10)).isTrue();
//        assertThat(jsyncRedis.pexpireAt("pexpireatKeyExisting", currentTimeMillis() + 10)).isFalse();
//
//        assertThat(jsyncRedis.get("pexpireatKey")).isEqualTo("value");
//
//        Thread.sleep(300);
//        assertThat(jsyncRedis.get("pexpireatKey")).isNullOrEmpty();
//
//        jsyncRedis.del("pexpireatKey");
//    }
//
//    @Test
//    public void pttl() throws Exception {
//        assertThat(jsyncRedis.set("pttlKey", "value")).isTrue();
//        assertThat(jsyncRedis.expire("pttlKey", 1)).isTrue();
//        assertThat(jsyncRedis.pttl("pttlKey")).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(1000);
//        jsyncRedis.del("pttlKey");
//    }
//
//    @Test
//    public void randomKey() throws Exception {
//        assertThat(jsyncRedis.set("randomKey", "value")).isTrue();
//        assertThat(jsyncRedis.randomKey()).isNotEmpty();
//        jsyncRedis.del("randomKey");
//    }
//
//
//    @Test
//    public void rename() throws Exception {
//        jsyncRedis.del("renameNewKey");
//        jsyncRedis.set("renameKey", "value");
//        assertThat(jsyncRedis.rename("renameKey", "renameNewKey")).isTrue();
//        assertThat(jsyncRedis.get("renameNewKey")).isEqualTo("value");
//
//        jsyncRedis.del("renameNewKey");
//    }
//
//
//    @Test
//    public void renameNX() throws Exception {
//        jsyncRedis.del("renamenxNewKey");
//        jsyncRedis.set("renamenxKey", "value");
//        jsyncRedis.set("renamenxNewKey", "value");
//
//        assertThat(jsyncRedis.renameNX("renamenxKey", "renamenxNewKey")).isFalse();
//        assertThat(jsyncRedis.del("renamenxNewKey")).isEqualTo(1);
//        assertThat(jsyncRedis.renameNX("renamenxKey", "renamenxNewKey")).isTrue();
//        assertThat(jsyncRedis.get("renamenxNewKey")).isEqualTo("value");
//
//        jsyncRedis.del("renamenxNewKey");
//    }
//
//    @Test
//    public void restore() throws Exception {
//        assertThat(jsyncRedis.set("restoreKey", "value")).isTrue();
//        final byte[] rawValue = jsyncRedis.dump("restoreKey");
//        assertThat(rawValue).isEqualTo(new byte[] { 0, 5, 118, 97, 108, 117, 101, 6, 0, 23, 27, -87, -72, 52, -1, -89, -3 });
//
//        assertThat(jsyncRedis.del("restoreKey")).isEqualTo(1);
//        assertThat(jsyncRedis.restore("restoreKey", rawValue)).isTrue();
//
//        jsyncRedis.del("restoreKey");
//    }
//
//    @Test
//    @Ignore("Rediscala 의 MasterSlaves 환경에서 sort 는 master에 호출해야 하지만, 현재 slave 를 호출하는 버그가 있습니다.")
//    public void sort() throws Exception {
//        jsyncRedis.hset("bonds|1", "bid_price", "96.01");
//        jsyncRedis.hset("bonds|1", "ask_price", "97.53");
//        jsyncRedis.hset("bonds|2", "bid_price", "95.50");
//        jsyncRedis.hset("bonds|2", "ask_price", "98.25");
//        jsyncRedis.del("bond_ids");
//        jsyncRedis.sadd("bond_ids", "1");
//        jsyncRedis.sadd("bond_ids", "2");
//        jsyncRedis.del("sortAlpha");
//        jsyncRedis.rpush("sortAlpha", "abc", "xyz");
//
//        log.debug("jsyncRedis class name={}", jsyncRedis.getClass().getName());
//
//        assertThat(jsyncRedis.sort("bond_ids")).isEqualTo(newArrayList("1", "2"));
//        assertThat(jsyncRedis.sort("bond_ids", DESC$.MODULE$)).isEqualTo(newArrayList("2", "1"));
//        assertThat(jsyncRedis.sort("sortAlpha", ASC$.MODULE$, true)).isEqualTo(newArrayList("abc", "xyz"));
//        assertThat(jsyncRedis.sort("bond_ids", new LimitOffsetCount(0, 1))).isEqualTo(newArrayList("1"));
//
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price")))
////                .isEqualTo(Lists.newArrayList("2", "1"));
////
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price").get("bonds|*->bid_price")))
////                .isEqualTo(Lists.newArrayList("95.50", "96.01"));
////
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price").get("bonds|*->bid_price", "#")))
////                .isEqualTo(Lists.newArrayList("95.50", "2", "96.01", "1"));
////
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price").limit(0, 1)))
////                .isEqualTo(Lists.newArrayList("2"));
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price").desc()))
////                .isEqualTo(Lists.newArrayList("1", "2"));
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->bid_price")))
////                .isEqualTo(Lists.newArrayList("2", "1"));
////
////        assertThat(jsyncRedis.sort("bond_ids", new SortingParams().by("bonds|*->ask_price"), "bond_ids_sorted_by_ask_price"))
////                .isEqualTo(2);
//
//        jsyncRedis.del("bonds|1", "bonds|2", "bond_ids");
//    }
//
//    @Test
//    public void ttl() throws Exception {
//        jsyncRedis.set("ttlKey", "value");
//        assertThat(jsyncRedis.expire("ttlKey", 10)).isTrue();
//        assertThat(jsyncRedis.ttl("ttlKey")).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(10);
//        jsyncRedis.del("ttlKey");
//    }
//
//    @Test
//    public void testType() {
//        jsyncRedis.set("typeKey", "value");
//        assertThat(jsyncRedis.type("typeKey")).isEqualToIgnoringCase("string");
//        assertThat(jsyncRedis.type("typeKeyNotExisting")).isEqualToIgnoringCase("none");
//        jsyncRedis.del("typeKey");
//    }
//
//    @Test
//    public void incr() throws Exception {
//        jsyncRedis.del("incrKey", "incrKeyNotExists");
//        jsyncRedis.set("incrKey", "0");
//        assertThat(jsyncRedis.incr("incrKey")).isEqualTo(1L);
//        assertThat(jsyncRedis.incr("incrKeyNotExists")).isEqualTo(1L);
//
//        jsyncRedis.del("incrKey", "incrKeyNotExists");
//    }
//
//    @Test
//    public void setex() throws Exception {
//        jsyncRedis.setEx("setexKey", 1, "expireTest");
//        assertThat(jsyncRedis.exists("setexKey")).isTrue();
//        Thread.sleep(1300L);
//        assertThat(jsyncRedis.exists("setexKey")).isFalse();
//
//        jsyncRedis.del("setexKey");
//    }
//
//    @Test
//    public void hdel() throws Exception {
//        jsyncRedis.hset("hdelKey", "field", "value");
//        assertThat(jsyncRedis.hdel("hdelKey", "field", "fieldNotExisting")).isEqualTo(1);
//    }
//
//    @Test
//    public void hexists() throws Exception {
//        jsyncRedis.del("hexistsKey");
//        jsyncRedis.hset("hexistsKey", "field", "value");
//        assertThat(jsyncRedis.hexists("hexistsKey", "field")).isTrue();
//        assertThat(jsyncRedis.hexists("hexistsKey", "notExistsFields")).isFalse();
//
//        jsyncRedis.del("hexistsKey");
//    }
//
//    @Test
//    public void hget() throws Exception {
//        jsyncRedis.del("hgetKey");
//        jsyncRedis.hset("hgetKey", "field", "value");
//        assertThat(jsyncRedis.hget("hgetKey", "field")).isEqualTo("value");
//        assertThat(jsyncRedis.hget("hgetKey", "fieldNotExisting")).isNullOrEmpty();
//
//        jsyncRedis.del("hgetKey");
//    }
//
//    @Test
//    public void hincrBy() throws Exception {
//        jsyncRedis.del("hincrby");
//
//        Integer v = 10;
//        jsyncRedis.hset("hincrby", "field", v.toString());
//        assertThat(jsyncRedis.hincrBy("hincrby", "field", 1)).isEqualTo(v + 1);
//        assertThat(jsyncRedis.hincrBy("hincrby", "field", -1)).isEqualTo(v);
//
//        jsyncRedis.del("hincrby");
//    }
//
//    @Test
//    public void hincrByFloat() throws Exception {
//        jsyncRedis.del("hincrbyFloat");
//
//        Double v = 10.0;
//        jsyncRedis.hset("hincrbyFloat", "field", v.toString());
//        assertThat(jsyncRedis.hincrByFloat("hincrbyFloat", "field", 0.1)).isEqualTo(v + 0.1);
//        assertThat(jsyncRedis.hincrByFloat("hincrbyFloat", "field", -1.1)).isEqualTo(v - 1.0);
//
//        jsyncRedis.del("hincrbyFloat");
//    }
//
//    @Test
//    public void hkeys() throws Exception {
//        jsyncRedis.hset("hkeysKey", "field", "value");
//        jsyncRedis.hset("hkeysKey", "field2", "value2");
//
//        List<String> values = jsyncRedis.hkeys("hkeysKey");
//        assertThat(values).isEqualTo(newArrayList("field", "field2"));
//
//        jsyncRedis.del("hkeysKey");
//    }
//
//    @Test
//    public void hlen() throws Exception {
//        jsyncRedis.hset("hlenKey", "field", "value");
//        assertThat(jsyncRedis.hlen("hlenKey")).isEqualTo(1);
//
//        jsyncRedis.del("hlen");
//    }
//
//    @Test
//    public void hmget() throws Exception {
//        jsyncRedis.del("hgetallKey");
//        jsyncRedis.hset("hgetallKey", "field", "value");
//
//        List<String> values = jsyncRedis.hmget("hgetallKey", "field", "fieldNotExisting");
//        assertThat(values.size()).isEqualTo(2);
//        assertThat(values.get(0)).isEqualTo("value");
//        assertThat(values.get(1)).isNullOrEmpty();
//
//        List<String> notExisting = jsyncRedis.hmget("hgetallKey", "fieldNotExisting");
//        assertThat(notExisting.size()).isEqualTo(1);
//        assertThat(notExisting.get(0)).isNullOrEmpty();
//
//        jsyncRedis.del("hgetallKey");
//    }
//
//    @Test
//    public void hmset() throws Exception {
//        jsyncRedis.del("hmsetKey");
//
//        Map<String, String> map = new HashMap<>();
//        map.put("field", "value");
//        map.put("field2", "value2");
//
//        jsyncRedis.hmset("hmsetKey", map);
//
//        assertThat(jsyncRedis.hget("hmsetKey", "field")).isEqualTo("value");
//        assertThat(jsyncRedis.hget("hmsetKey", "field2")).isEqualTo("value2");
//
//        jsyncRedis.del("hmsetKey");
//    }
//
//
//    @Test
//    public void hset() throws Exception {
//        jsyncRedis.del("setKey");
//        jsyncRedis.hset("setKey", "field", String.valueOf(1));
//        assertThat(Integer.valueOf(jsyncRedis.hget("setKey", "field"))).isEqualTo(1);
//
//        // FIELD 값이 없으면 0으로 가정합니다.
//        jsyncRedis.hincrBy("setKey", "field2", 1);
//        assertThat(Integer.decode(jsyncRedis.hget("setKey", "field2"))).isEqualTo(1);
//        jsyncRedis.hincrBy("setKey", "field2", 2);
//        assertThat(Integer.decode(jsyncRedis.hget("setKey", "field2"))).isEqualTo(3);
//
//        jsyncRedis.del("setKey");
//    }
//
//    @Test
//    public void hsetnx() throws Exception {
//        jsyncRedis.hdel("hsetnxKey", "field");
//        assertThat(jsyncRedis.hsetnx("hsetnxKey", "field", "value")).isTrue();
//        assertThat(jsyncRedis.hsetnx("hsetnxKey", "field", "value2")).isFalse();
//        assertThat(jsyncRedis.hget("hsetnxKey", "field")).isEqualTo("value");
//
//        jsyncRedis.del("hsetnxKey");
//    }
//
//    @Test
//    public void hvals() throws Exception {
//        jsyncRedis.hdel("hvalsKey", "field");
//        assertThat(jsyncRedis.hvals("hvalsKey")).isNullOrEmpty();
//        jsyncRedis.hset("hvalsKey", "field", "value");
//        assertThat(jsyncRedis.hvals("hvalsKey")).isEqualTo(newArrayList("value"));
//
//        jsyncRedis.del("hvalsKey");
//    }
//
//    @Test
//    public void lindex() throws Exception {
//        jsyncRedis.del("lindexKey");
//        jsyncRedis.lpush("lindexKey", "World", "Hello");
//        assertThat(jsyncRedis.lindex("lindexKey", 0)).isEqualTo("Hello");
//        assertThat(jsyncRedis.lindex("lindexKey", 1)).isEqualTo("World");
//        assertThat(jsyncRedis.lindex("lindexKey", 2)).isNullOrEmpty();
//
//        jsyncRedis.del("lindexKey");
//    }
//
//    @Test
//    public void linsert() throws Exception {
//        jsyncRedis.del("linsertKey");
//        jsyncRedis.lpush("linsertKey", "World", "Hello");
//        long length = jsyncRedis.linsertBefore("linsertKey", "World", "There");
//        List<String> list = jsyncRedis.lrange("linsertKey", 0, -1);
//        long length4 = jsyncRedis.linsertAfter("linsertKey", "World", "!!!");
//        List<String> list4 = jsyncRedis.lrange("linsertKey", 0, -1);
//
//        assertThat(length).isEqualTo(3);
//        assertThat(list).isEqualTo(newArrayList("Hello", "There", "World"));
//        assertThat(length4).isEqualTo(4);
//        assertThat(list4).isEqualTo(newArrayList("Hello", "There", "World", "!!!"));
//
//        jsyncRedis.del("linsertKey");
//    }
//
//    @Test
//    public void llen() throws Exception {
//        jsyncRedis.del("llenKey");
//        jsyncRedis.lpush("llenKey", "World", "Hello");
//        assertThat(jsyncRedis.llen("llenKey")).isEqualTo(2);
//        jsyncRedis.del("llenKey");
//    }
//
//    @Test
//    public void lpop() throws Exception {
//        jsyncRedis.del("lpopKey");
//        jsyncRedis.rpush("lpopKey", "one", "two", "three");
//        assertThat(jsyncRedis.lpop("lpopKey")).isEqualTo("one");
//        jsyncRedis.del("lpopKey");
//    }
//
//    @Test
//    public void lpush() throws Exception {
//        jsyncRedis.del("lpushKey");
//        jsyncRedis.lpush("lpushKey", "1", "2", "3", "4");
//        assertThat(jsyncRedis.llen("lpushKey")).isEqualTo(4);
//        jsyncRedis.del("lpushKey");
//    }
//
//    @Test
//    public void lpushx() throws Exception {
//        jsyncRedis.del("lpushxKey", "lpushxKeyOther");
//
//        assertThat(jsyncRedis.rpush("lpushxKey", "World")).isEqualTo(1);
//        assertThat(jsyncRedis.lpushx("lpushxKey", "Hello")).isEqualTo(2);
//        assertThat(jsyncRedis.lpushx("lpushxKeyOther", "Hello")).isEqualTo(0);
//        assertThat(jsyncRedis.lrange("lpushxKey", 0, -1)).isEqualTo(newArrayList("Hello", "World"));
//        assertThat(jsyncRedis.lrange("lpushxKeyOther", 0, -1)).isNullOrEmpty();
//
//        jsyncRedis.del("lpushxKey", "lpushxKeyOther");
//    }
//
//    @Test
//    public void lrange() throws Exception {
//        jsyncRedis.del("lrangeKey");
//
//        jsyncRedis.rpush("lrangeKey", "one", "two", "three");
//        assertThat(jsyncRedis.lrange("lrangeKey", 0, 0)).isEqualTo(newArrayList("one"));
//        assertThat(jsyncRedis.lrange("lrangeKey", -3, 2)).isEqualTo(newArrayList("one", "two", "three"));
//        assertThat(jsyncRedis.lrange("lrangeKey", 5, 10)).isNullOrEmpty();
//        assertThat(jsyncRedis.lrange("lrangeKeyNonExisting", 0, -1)).isNullOrEmpty();
//
//        jsyncRedis.del("lrangeKey");
//    }
//
//    @Test
//    public void lrem() throws Exception {
//        jsyncRedis.del("lremKey");
//
//        jsyncRedis.rpush("lremKey", "hello", "hello", "foo", "hello");
//        // count < 0 이면 뒤에서 앞으로, >0 이면 앞에서 뒤로, =0 이면 일치하는 모든 것
//        assertThat(jsyncRedis.lrem("lremKey", -2, "hello")).isEqualTo(2);
//        assertThat(jsyncRedis.lrange("lremKey", 0, -1)).isEqualTo(newArrayList("hello", "foo"));
//
//        jsyncRedis.del("lremKey");
//    }
//
//    @Test
//    public void lset() throws Exception {
//        jsyncRedis.del("lsetKey");
//
//        jsyncRedis.rpush("lsetKey", "one", "two", "three");
//        assertThat(jsyncRedis.lset("lsetKey", 0, "four")).isTrue();
//        assertThat(jsyncRedis.lset("lsetKey", -2, "five")).isTrue();
//        assertThat(jsyncRedis.lrange("lsetKey", 0, -1)).isEqualTo(newArrayList("four", "five", "three"));
//
//        jsyncRedis.del("lsetKey");
//    }
//
//    @Test
//    public void ltrim() throws Exception {
//        jsyncRedis.del("ltrimKey");
//
//        jsyncRedis.rpush("ltrimKey", "one", "two", "three");
//        assertThat(jsyncRedis.ltrim("ltrimKey", 1, -1)).isTrue();
//        assertThat(jsyncRedis.lrange("ltrimKey", 0, -1)).isEqualTo(newArrayList("two", "three"));
//
//        jsyncRedis.del("ltrimKey");
//    }
//
//    @Test
//    public void rpop() throws Exception {
//        jsyncRedis.del("rpopKey");
//
//        jsyncRedis.rpush("rpopKey", "one", "two", "three");
//        assertThat(jsyncRedis.rpop("rpopKey")).isEqualTo("three");
//        assertThat(jsyncRedis.lrange("rpopKey", 0, -1)).isEqualTo(newArrayList("one", "two"));
//
//        jsyncRedis.del("rpopKey");
//    }
//
//    @Test
//    public void rpoplpush() throws Exception {
//        jsyncRedis.del("rpoplpushKey", "rpoplpushKeyOther");
//
//        jsyncRedis.rpush("rpoplpushKey", "one", "two", "three");
//        jsyncRedis.rpoplpush("rpoplpushKey", "rpoplpushKeyOther");
//        assertThat(jsyncRedis.lrange("rpoplpushKey", 0, -1)).isEqualTo(newArrayList("one", "two"));
//        assertThat(jsyncRedis.lrange("rpoplpushKeyOther", 0, -1)).isEqualTo(newArrayList("three"));
//
//        jsyncRedis.del("rpoplpushKey", "rpoplpushKeyOther");
//    }
//
//    @Test
//    public void rpushx() throws Exception {
//        jsyncRedis.del("rpushxKey", "rpushxKeyOther");
//
//        assertThat(jsyncRedis.rpush("rpushxKey", "hello")).isEqualTo(1);
//        assertThat(jsyncRedis.rpushx("rpushxKey", "world")).isEqualTo(2);
//        assertThat(jsyncRedis.rpushx("rpushxKeyOther", "world")).isEqualTo(0);
//        assertThat(jsyncRedis.lrange("rpushxKey", 0, -1)).isEqualTo(newArrayList("hello", "world"));
//        assertThat(jsyncRedis.lrange("rpushxKeyOther", 0, -1)).isNullOrEmpty();
//
//        jsyncRedis.del("rpushxKey", "rpushxKeyOther");
//    }
//
//    @Test
//    public void blpop_already_containing_elements() throws Exception {
//        jsyncRedis.del("blpop1", "blpop2");
//        jsyncRedis.rpush("blpop1", "a", "b", "c");
//
//        final Tuple2<String, String> results = jsyncRedis2.blpop(60, "blpop1", "blpop2");
//
//        assertThat(results).isNotNull();
//        assertThat(results._1()).isEqualTo("blpop1");
//        assertThat(results._2()).isEqualTo("a");
//
//        jsyncRedis.del("blpop1", "blpop2");
//    }
//
//    @Test
//    public void blpop_blocking() throws Exception {
//        jsyncRedis2.del("blpopBlock");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//
//        final Future<Tuple2<String, String>> future = JavaAsyncs.startNew(new Callable<Tuple2<String, String>>() {
//            @Override
//            public Tuple2<String, String> call() throws Exception {
//                return syncClient.blpop(60, "blpopBlock", "blpopBlock2");
//            }
//        });
//
//        Thread.sleep(1000);
//
//        this.jsyncRedis.rpush("blpopBlock", "a", "b", "c");
//
//        Tuple2<String, String> results = future.get();
//        assertThat(results).isNotNull();
//        assertThat(results._1()).isEqualTo("blpopBlock");
//        assertThat(results._2()).isEqualTo("a");
//
//        jsyncRedis2.del("blpopBlock");
//    }
//
//    @Test
//    public void blpop_blocking_timeout() throws Exception {
//        jsyncRedis2.del("blpopBlockTimeout");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//        final Future<Tuple2<String, String>> future = JavaAsyncs.startNew(new Callable<Tuple2<String, String>>() {
//            @Override
//            public Tuple2<String, String> call() throws Exception {
//                return syncClient.blpop(1, "blpopBlockTimeout");
//            }
//        });
//
//        // Timeout 이 되면 null 을 반환합니다.
//        assertThat(future.get()).isNull();
//
//        jsyncRedis2.del("blpopBlockTimeout");
//    }
//
//    @Test
//    public void brpop_already_containing_elements() throws Exception {
//        jsyncRedis.del("brpop1", "brpop2");
//        jsyncRedis.rpush("brpop1", "a", "b", "c");
//
//        final Tuple2<String, String> results = jsyncRedis2.brpop(60, "brpop1", "brpop2");
//
//        assertThat(results).isNotNull();
//        assertThat(results._1()).isEqualTo("brpop1");
//        assertThat(results._2()).isEqualTo("c");
//
//        jsyncRedis.del("brpop1", "brpop2");
//    }
//
//    @Test
//    public void brpop_blocking() throws Exception {
//        jsyncRedis2.del("brpopBlock");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//        final Future<Tuple2<String, String>> future = JavaAsyncs.startNew(new Callable<Tuple2<String, String>>() {
//            @Override
//            public Tuple2<String, String> call() throws Exception {
//                return syncClient.brpop(60, "brpopBlock", "brpopBlock2");
//            }
//        });
//
//
//        Thread.sleep(1000);
//
//        this.jsyncRedis.rpush("brpopBlock", "a", "b", "c");
//
//        Tuple2<String, String> results = future.get();
//        assertThat(results).isNotNull();
//        assertThat(results._1()).isEqualTo("brpopBlock");
//        assertThat(results._2()).isEqualTo("c");
//
//        jsyncRedis2.del("brpopBlock");
//    }
//
//    @Test
//    public void brpop_blocking_timeout() throws Exception {
//        jsyncRedis2.del("brpopBlockTimeout");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//        final Future<Tuple2<String, String>> future = JavaAsyncs.startNew(new Callable<Tuple2<String, String>>() {
//            @Override
//            public Tuple2<String, String> call() throws Exception {
//                return syncClient.brpop(1, "brpopBlockTimeout");
//            }
//        });
//
//        // Timeout 이 되면 null 을 반환합니다.
//        assertThat(future.get()).isNull();
//
//        jsyncRedis2.del("brpopBlockTimeout");
//    }
//
//    @Test
//    public void brpoplpush_already_containing_elements() throws Exception {
//        jsyncRedis.del("brpoplpush1", "brpoplpush2");
//        jsyncRedis.rpush("brpoplpush1", "a", "b", "c");
//        final String value = jsyncRedis.brpoplpush("brpoplpush1", "brpoplpush2", 60);
//        assertThat(value).isEqualTo("c");
//
//        jsyncRedis.del("brpoplpush1", "brpoplpush2");
//    }
//
//    @Test
//    public void brpoplpush_blocking() throws Exception {
//        jsyncRedis.del("brpoplpushBlock1", "brpoplpushBlock2");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//        final Future<String> future = JavaAsyncs.startNew(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return syncClient.brpoplpush("brpoplpushBlock1", "brpoplpushBlock2", 60);
//            }
//        });
//
//
//        Thread.sleep(1000);
//        this.jsyncRedis.rpush("brpoplpushBlock1", "a", "b", "c");
//        assertThat(future.get()).isEqualTo("c");
//
//        jsyncRedis.del("brpoplpushBlock1", "brpoplpushBlock2");
//    }
//
//    @Test
//    public void brpoplpush_blocking_timeout() throws Exception {
//        jsyncRedis.del("brpoplpushTimeout1", "brpoplpushTimeout2");
//
//        final JRedisSyncClient syncClient = jsyncRedis2;
//        final Future<String> future = JavaAsyncs.startNew(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return syncClient.brpoplpush("brpoplpushTimeout1", "brpoplpushTimeout2", 1);
//            }
//        });
//
//        // Timeout 이 되면 null 을 반환합니다.
//        assertThat(future.get()).isNullOrEmpty();
//
//        jsyncRedis.del("brpoplpushTimeout1", "brpoplpushTimeout2");
//    }
//
//    @Test
//    public void sadd() throws Exception {
//        jsyncRedis.del("saddKey");
//
//        assertThat(jsyncRedis.sadd("saddKey", "Hello", "World")).isEqualTo(2);
//        assertThat(jsyncRedis.sadd("saddKey", "Hello")).isEqualTo(0); // already exists
//
//        assertThat(jsyncRedis.smembers("saddKey")).isEqualTo(newHashSet("Hello", "World"));
//
//        jsyncRedis.del("saddKey");
//    }
//
//    @Test
//    public void scard() throws Exception {
//        jsyncRedis.del("scardKey");
//
//        assertThat(jsyncRedis.scard("scardKey")).isEqualTo(0);
//        assertThat(jsyncRedis.sadd("scardKey", "Hello", "World")).isEqualTo(2);
//        assertThat(jsyncRedis.scard("scardKey")).isEqualTo(2);
//
//        jsyncRedis.del("scardKey");
//    }
//
//    @Test
//    public void sdiff() throws Exception {
//        jsyncRedis.del("sdiffKey1", "sdiffKey2");
//
//        jsyncRedis.sadd("sdiffKey1", "a", "b", "c");
//        jsyncRedis.sadd("sdiffKey2", "c", "d", "e");
//        assertThat(jsyncRedis.sdiff("sdiffKey1", "sdiffKey2")).isEqualTo(newHashSet("a", "b"));
//
//        jsyncRedis.del("sdiffKey1", "sdiffKey2");
//    }
//
//    @Test
//    public void sdiffStore() throws Exception {
//        jsyncRedis.del("sdiffstoreKey1", "sdiffstoreKey2", "sdiffstoreKey");
//
//        jsyncRedis.sadd("sdiffstoreKey1", "a", "b", "c");
//        jsyncRedis.sadd("sdiffstoreKey2", "c", "d", "e");
//
//        assertThat(jsyncRedis.sdiffStore("sdiffstoreKey", "sdiffstoreKey1", "sdiffstoreKey2")).isEqualTo(2);
//        assertThat(jsyncRedis.smembers("sdiffstoreKey")).isEqualTo(Sets.newHashSet("a", "b"));
//
//        jsyncRedis.del("sdiffstoreKey1", "sdiffstoreKey2", "sdiffstoreKey");
//    }
//
//    @Test
//    public void sinter() throws Exception {
//        jsyncRedis.del("sinterKey1", "sinterKey2");
//
//        jsyncRedis.sadd("sinterKey1", "a", "b", "c");
//        jsyncRedis.sadd("sinterKey2", "c", "d", "e");
//        assertThat(jsyncRedis.sinter("sinterKey1", "sinterKey2")).isEqualTo(newHashSet("c"));
//
//        jsyncRedis.del("sinterKey1", "sinterKey2");
//    }
//
//    @Test
//    public void sinterStore() throws Exception {
//        jsyncRedis.del("sinterstoreKey1", "sinterstoreKey2", "sinterstoreKey");
//
//        jsyncRedis.sadd("sinterstoreKey1", "a", "b", "c");
//        jsyncRedis.sadd("sinterstoreKey2", "c", "d", "e");
//
//        assertThat(jsyncRedis.sinterStore("sinterstoreKey", "sinterstoreKey1", "sinterstoreKey2")).isEqualTo(1);
//        assertThat(jsyncRedis.smembers("sinterstoreKey")).isEqualTo(newHashSet("c"));
//
//        jsyncRedis.del("sinterstoreKey1", "sinterstoreKey2", "sinterstoreKey");
//    }
//
//    @Test
//    public void sismember() throws Exception {
//        jsyncRedis.del("sismemberKey");
//
//        jsyncRedis.sadd("sismemberKey", "hello", "world");
//        assertThat(jsyncRedis.sisMember("sismemberKey", "hello")).isTrue();
//        assertThat(jsyncRedis.sisMember("sismemberKey", "world")).isTrue();
//        assertThat(jsyncRedis.sisMember("sismemberKey", "notMember")).isFalse();
//
//        jsyncRedis.del("sismemberKey");
//    }
//
//    @Test
//    public void smove() throws Exception {
//        jsyncRedis.del("smoveKey1", "smoveKey2");
//
//        jsyncRedis.sadd("smoveKey1", "one", "two");
//        jsyncRedis.sadd("smoveKey2", "three");
//        assertThat(jsyncRedis.smove("smoveKey1", "smoveKey2", "two")).isTrue();
//        assertThat(jsyncRedis.smove("smoveKey1", "smoveKey2", "notExisting")).isFalse();
//        assertThat(jsyncRedis.smembers("smoveKey1")).isEqualTo(newHashSet("one"));
//        assertThat(jsyncRedis.smembers("smoveKey2")).isEqualTo(newHashSet("two", "three"));
//
//        jsyncRedis.del("smoveKey1", "smoveKey2");
//    }
//
//    @Test
//    public void spop() throws Exception {
//        jsyncRedis.del("spopKey");
//
//        Set<String> values = newHashSet("one", "two", "three");
//        jsyncRedis.sadd("spopKey", values);
//        assertThat(jsyncRedis.spop("spopKey")).isIn(values);
//        assertThat(jsyncRedis.spop("spopKeyNonExisting")).isNullOrEmpty();
//
//        jsyncRedis.del("spopKey");
//    }
//
//    @Test
//    public void srandmember() throws Exception {
//        jsyncRedis.del("srandmemberKey");
//
//        Set<String> values = newHashSet("one", "two", "three");
//        jsyncRedis.sadd("srandmemberKey", values);
//        assertThat(jsyncRedis.srandmember("srandmemberKey")).isIn(values);
//        assertThat(jsyncRedis.srandmember("srandmemberKeyNotExisting")).isNullOrEmpty();
//
//        jsyncRedis.del("srandmemberKey");
//    }
//
//    @Test
//    public void srem() throws Exception {
//        jsyncRedis.del("sremKey");
//
//        Set<String> values = newHashSet("one", "two", "three", "four");
//        jsyncRedis.sadd("sremKey", ArrayTool.asArray(values, String.class));
//        assertThat(jsyncRedis.srem("sremKey", "one", "four")).isEqualTo(2);
//        assertThat(jsyncRedis.srem("sremKey", "five")).isEqualTo(0);
//
//        assertThat(jsyncRedis.smembers("sremKey")).isEqualTo(newHashSet("two", "three"));
//
//        jsyncRedis.del("sremKey");
//    }
//
//    @Test
//    public void sunion() throws Exception {
//        jsyncRedis.del("sunionKey1", "sunionKey2");
//
//        jsyncRedis.sadd("sunionKey1", "a", "b", "c");
//        jsyncRedis.sadd("sunionKey2", "c", "d", "e");
//        assertThat(jsyncRedis.sunion("sunionKey1", "sunionKey2")).isEqualTo(newHashSet("a", "b", "c", "d", "e"));
//
//        jsyncRedis.del("sunionKey1", "sunionKey2");
//    }
//
//    @Test
//    public void sunionStore() throws Exception {
//        jsyncRedis.del("sunionstoreKey1", "sunionstoreKey2", "sunionstoreKey");
//
//        jsyncRedis.sadd("sunionstoreKey1", "a", "b", "c");
//        jsyncRedis.sadd("sunionstoreKey2", "c", "d", "e");
//
//        assertThat(jsyncRedis.sunionStore("sunionstoreKey", "sunionstoreKey1", "sunionstoreKey2")).isEqualTo(5);
//        assertThat(jsyncRedis.smembers("sunionstoreKey")).isEqualTo(newHashSet("a", "b", "c", "d", "e"));
//
//        jsyncRedis.del("sunionstoreKey1", "sunionstoreKey2", "sunionstoreKey");
//    }
//
//    @Test
//    public void zadd() throws Exception {
//        jsyncRedis.del("zaddKey");
//
//        assertThat(jsyncRedis.zadd("zaddKey", 1.0, "one")).isEqualTo(1);
//        assertThat(jsyncRedis.zadd("zaddKey", 1, "uno")).isEqualTo(1);
//        assertThat(jsyncRedis.zadd("zaddKey", 2, "two")).isEqualTo(1);
//        assertThat(jsyncRedis.zrangeWithScores("zaddKey", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("one", 1.0),
//                                        new MemberScore("uno", 1.0),
//                                        new MemberScore("two", 2.0)));
//
//        jsyncRedis.del("zaddKey");
//    }
//
//    @Test
//    public void zcard() throws Exception {
//        jsyncRedis.del("zcardKey");
//
//        assertThat(jsyncRedis.zcard("zcardKey")).isEqualTo(0);
//        jsyncRedis.zaddAll("zcardKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0));
//        assertThat(jsyncRedis.zcard("zcardKey")).isEqualTo(2);
//        assertThat(jsyncRedis.zcard("zcardKeyNonExisting")).isEqualTo(0);
//
//        jsyncRedis.del("zcardKey");
//    }
//
//    @Test
//    public void zcount() throws Exception {
//        jsyncRedis.del("zcountKey");
//
//        assertThat(jsyncRedis.zcount("zcountKey")).isEqualTo(0);
//        jsyncRedis.zaddAll("zcountKey",
//                           new MemberScore("one", 1.0),
//                           new MemberScore("two", 2.0),
//                           new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zcount("zcountKey")).isEqualTo(3);
//        assertThat(jsyncRedis.zcount("zcountKey", 1.1, 3)).isEqualTo(2);
//
//        jsyncRedis.del("zcountKey");
//    }
//
//    @Test
//    public void zincrby() throws Exception {
//        jsyncRedis.del("zincrbyKey");
//
//        jsyncRedis.zaddAll("zincrbyKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0));
//        assertThat(jsyncRedis.zincrBy("zincrbyKey", 2.1, "one")).isEqualTo(3.1);
//        assertThat(jsyncRedis.zincrBy("zincrbyKey", 2.1, "notExisting")).isEqualTo(2.1);
//
//        assertThat(jsyncRedis.zrangeWithScores("zincrbyKey", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("two", 2.0),
//                                        new MemberScore("notExisting", 2.1),
//                                        new MemberScore("one", 3.1)));
//
//        jsyncRedis.del("zincrbyKey");
//    }
//
//    @Test
//    public void zinterstore() throws Exception {
//        jsyncRedis.del("zinterstoreKey", "zinterstoreKeyOutWeighted", "zinterstoreKey1", "zinterstoreKey2");
//
//        jsyncRedis.zaddAll("zinterstoreKey1", new MemberScore("one", 1.0), new MemberScore("two", 2.0));
//        jsyncRedis.zaddAll("zinterstoreKey2", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zinterStore("zinterstoreKey", "zinterstoreKey1", "zinterstoreKey2")).isEqualTo(2);
//
//        HashMap<String, Double> weights = Maps.newHashMap();
//        weights.put("zinterstoreKey1", 2D);
//        weights.put("zinterstoreKey2", 3D);
//        Long ziw = jsyncRedis.zinterStoreWeighted("zinterstoreKeyOutWeighted", weights);
//
//        assertThat(ziw).isEqualTo(2);
//
//        assertThat(jsyncRedis.zrangeWithScores("zinterstoreKey", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("one", 2.0), new MemberScore("two", 4.0)));
//
//        assertThat(jsyncRedis.zrangeWithScores("zinterstoreKeyOutWeighted", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("one", 5.0), new MemberScore("two", 10.0)));
//
//        jsyncRedis.del("zinterstoreKey", "zinterstoreKeyOutWeighted", "zinterstoreKey1", "zinterstoreKey2");
//    }
//
//    @Test
//    public void zrange() throws Exception {
//        jsyncRedis.del("zrangeKey");
//
//        jsyncRedis.zaddAll("zrangeKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zrange("zrangeKey", 0, -1)).isEqualTo(newArrayList("one", "two", "three"));
//        assertThat(jsyncRedis.zrange("zrangeKey", 2, 3)).isEqualTo(newArrayList("three"));
//        assertThat(jsyncRedis.zrange("zrangeKey", -2, -1)).isEqualTo(newArrayList("two", "three"));
//
//        jsyncRedis.del("zrangeKey");
//    }
//
//    @Test
//    public void zrangeByScore() throws Exception {
//        jsyncRedis.del("zrangebyscoreKey");
//
//        jsyncRedis.zaddAll("zrangebyscoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//
//        assertThat(jsyncRedis.zrangeByScore("zrangebyscoreKey", NEGATIVE_INFINITY, POSITIVE_INFINITY))
//                .isEqualTo(newArrayList("one", "two", "three"));
//
//        assertThat(jsyncRedis.zrangeByScore("zrangebyscoreKey", NEGATIVE_INFINITY, POSITIVE_INFINITY, 1L, 2L))
//                .isEqualTo(newArrayList("two", "three"));
//
//        assertThat(jsyncRedis.zrangeByScore("zrangebyscoreKey", 1.0, 2.0))
//                .isEqualTo(newArrayList("one", "two"));
//
//        assertThat(jsyncRedis.zrangeByScoreWithScores("zrangebyscoreKey", 1.0, 2.0))
//                .isEqualTo(newArrayList(new MemberScore("one", 1.0), new MemberScore("two", 2.0)));
//
//        // not include
//        assertThat(jsyncRedis.zrangeByScore("zrangebyscoreKey", new Limit(1.0, false), new Limit(2.0, true)))
//                .isEqualTo(newArrayList("two"));
//
//        // not include
//        assertThat(jsyncRedis.zrangeByScore("zrangebyscoreKey", new Limit(1.0, false), new Limit(2.0, false)))
//                .isEqualTo(newArrayList());
//
//        jsyncRedis.del("zrangebyscoreKey");
//    }
//
//    @Test
//    public void zrank() throws Exception {
//        jsyncRedis.del("zrankKey");
//
//        jsyncRedis.zaddAll("zrankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        // rank 는 0부터
//        assertThat(jsyncRedis.zrank("zrankKey", "three")).isEqualTo(2);
//        assertThat(jsyncRedis.zrank("zrankKey", "two")).isEqualTo(1);
//        assertThat(jsyncRedis.zrank("zrankKey", "one")).isEqualTo(0);
//        assertThat(jsyncRedis.zrank("zrankKey", "notExisting")).isNull();
//
//        jsyncRedis.del("zrankKey");
//    }
//
//    @Test
//    public void zrem() throws Exception {
//        jsyncRedis.del("zremKey");
//
//        jsyncRedis.zaddAll("zremKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zrem("zremKey", "two", "nonexisting")).isEqualTo(1);
//        assertThat(jsyncRedis.zrange("zremKey", 0, -1)).isEqualTo(newArrayList("one", "three"));
//
//        jsyncRedis.del("zremKey");
//    }
//
//
//    @Test
//    public void zremRangeByRank() throws Exception {
//        jsyncRedis.del("zremRangeByRankKey");
//
//        jsyncRedis.zaddAll("zremRangeByRankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zremRangeByRank("zremRangeByRankKey", 0, 1)).isEqualTo(2);
//        assertThat(jsyncRedis.zrange("zremRangeByRankKey", 0, -1)).isEqualTo(newArrayList("three"));
//
//        jsyncRedis.del("zremRangeByRankKey");
//    }
//
//    @Test
//    public void zremRangeByScore() throws Exception {
//        jsyncRedis.del("zremRangeByScoreKey");
//
//        jsyncRedis.zaddAll("zremRangeByScoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zremRangeByScore("zremRangeByScoreKey", 1.0, 2.0)).isEqualTo(2);
//        assertThat(jsyncRedis.zrange("zremRangeByScoreKey", 0, -1)).isEqualTo(newArrayList("three"));
//
//        jsyncRedis.del("zremRangeByScoreKey");
//    }
//
//    @Test
//    public void zrevrange() throws Exception {
//        jsyncRedis.del("zrevrangeKey");
//
//        jsyncRedis.zaddAll("zrevrangeKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zrevrange("zrevrangeKey", 0, -1)).isEqualTo(newArrayList("three", "two", "one"));
//        assertThat(jsyncRedis.zrevrange("zrevrangeKey", 2, 3)).isEqualTo(newArrayList("one"));
//        assertThat(jsyncRedis.zrevrange("zrevrangeKey", -2, -1)).isEqualTo(newArrayList("two", "one"));
//
//        jsyncRedis.del("zrevrangeKey");
//    }
//
//    @Test
//    public void zrevrangeByScore() throws Exception {
//        jsyncRedis.del("zrevrangeByScoreKey");
//
//        jsyncRedis.zaddAll("zrevrangeByScoreKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//
//        assertThat(jsyncRedis.zrevrangeByScore("zrevrangeByScoreKey", POSITIVE_INFINITY, NEGATIVE_INFINITY))
//                .isEqualTo(newArrayList("three", "two", "one"));
//
//        assertThat(jsyncRedis.zrevrangeByScore("zrevrangeByScoreKey", 2.0, 1.0))
//                .isEqualTo(newArrayList("two", "one"));
//
//        assertThat(jsyncRedis.zrevrangeByScoreWithScores("zrevrangeByScoreKey", 2.0, 1.0))
//                .isEqualTo(newArrayList(new MemberScore("two", 2.0), new MemberScore("one", 1.0)));
//
//        // not include
//
//        assertThat(jsyncRedis.zrevrangeByScore("zrevrangeByScoreKey", new Limit(2.0, true), new Limit(1.0, false)))
//                .isEqualTo(newArrayList("two"));
//
//        // not include
//        assertThat(jsyncRedis.zrevrangeByScore("zrevrangeByScoreKey", new Limit(2.0, false), new Limit(1.0, false)))
//                .isEqualTo(newArrayList());
//
//        jsyncRedis.del("zrevrangeByScoreKey");
//    }
//
//    @Test
//    public void zrevrank() throws Exception {
//        jsyncRedis.del("zrevrankKey");
//
//        jsyncRedis.zaddAll("zrevrankKey", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        // rank 는 0부터
//        assertThat(jsyncRedis.zrevrank("zrevrankKey", "three")).isEqualTo(0);
//        assertThat(jsyncRedis.zrevrank("zrevrankKey", "two")).isEqualTo(1);
//        assertThat(jsyncRedis.zrevrank("zrevrankKey", "one")).isEqualTo(2);
//        assertThat(jsyncRedis.zrevrank("zrevrankKey", "notExisting")).isNull();
//
//        jsyncRedis.del("zrevrankKey");
//    }
//
//    @Test
//    public void zscore() {
//        jsyncRedis.del("zscoreKey");
//
//        jsyncRedis.zaddAll("zscoreKey", new MemberScore("one", 1.1), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zscore("zscoreKey", "one")).isEqualTo(1.1);
//        assertThat(jsyncRedis.zscore("zscoreKey", "nonExisting")).isNull();
//
//        jsyncRedis.del("zscoreKey");
//    }
//
//    @Test
//    public void zunionstore() throws Exception {
//        jsyncRedis.del("zunionstoreKey", "zunionstoreKeyOutWeighted", "zunionstoreKey1", "zunionstoreKey2");
//
//        jsyncRedis.zaddAll("zunionstoreKey1", new MemberScore("one", 1.0), new MemberScore("two", 2.0));
//        jsyncRedis.zaddAll("zunionstoreKey2", new MemberScore("one", 1.0), new MemberScore("two", 2.0), new MemberScore("three", 3.0));
//        assertThat(jsyncRedis.zunionStore("zunionstoreKey", "zunionstoreKey1", "zunionstoreKey2")).isEqualTo(3);
//
//        HashMap<String, Double> weights = Maps.newHashMap();
//        weights.put("zunionstoreKey1", 2D);
//        weights.put("zunionstoreKey2", 3D);
//        Long ziw = jsyncRedis.zunionStoreWeighted("zunionstoreKeyOutWeighted", weights, SUM$.MODULE$);
//
//        assertThat(ziw).isEqualTo(3);
//
//        assertThat(jsyncRedis.zrangeWithScores("zunionstoreKey", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("one", 2.0), new MemberScore("three", 3.0), new MemberScore("two", 4.0)));
//        assertThat(jsyncRedis.zrangeWithScores("zunionstoreKeyOutWeighted", 0, -1))
//                .isEqualTo(newArrayList(new MemberScore("one", 5.0), new MemberScore("three", 9.0), new MemberScore("two", 10.0)));
//
//        jsyncRedis.del("zunionstoreKey", "zunionstoreKeyOutWeighted", "zunionstoreKey1", "zunionstoreKey2");
//    }
//}
