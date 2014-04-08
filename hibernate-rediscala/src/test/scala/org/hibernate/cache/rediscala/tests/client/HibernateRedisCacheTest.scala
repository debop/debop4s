package org.hibernate.cache.rediscala.tests.client

import java.util.concurrent.TimeUnit
import org.hibernate.cache.rediscala.Promises
import org.hibernate.cache.rediscala.client.HibernateRedisCache
import org.hibernate.cache.rediscala.tests.AbstractHibernateRedisTest
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * org.hibernate.cache.rediscala.tests.client.CacheClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 3:04
 */
class HibernateRedisCacheTest extends AbstractHibernateRedisTest {

    val client = HibernateRedisCache()

    val REGION = HibernateRedisCache.DEFAULT_REGION_NAME

    test("connection") {
        client.ping.map { r =>
            assert(r == "pong")
        }
    }

    test("multiple connection test") {
        (0 to 1000).par.foreach { x =>
            client.ping
            Thread.sleep(1)
        }
    }

    test("set and get") {
        val r = for {
            _ <- client.set(REGION, "key", 123)
            value <- client.get(REGION, "key")
        } yield {
            value should equal(123)
        }
    }

    test("expire") {
        val r = for {
            _ <- client.set(REGION, "expireKey", "value", 100, TimeUnit.MILLISECONDS)
            value <- client.get(REGION, "expireKey")
        } yield {
            value should equal("value")
        }

        Thread.sleep(150)

        client.get(REGION, "expireKey") map {
            x => assert(x == null)
        }
    }

    test("flushDb") {
        val r = for {
            _ <- client.set(REGION, "flushDbKey", "value")
            size1 <- client.dbSize
            _ <- client.flushDb()
            size0 <- client.dbSize
        } yield {
            size1 should equal(1)
            size0 should equal(0)
        }
    }

    test("delete") {
        val r = for {
            _ <- client.set(REGION, "deleteKey", "value")
            v <- client.get(REGION, "deleteKey")
            exists <- client.get(REGION, "deleteKey")

            _ <- client.delete(REGION, "deleteKey")
            v2 <- client.get(REGION, "deleteKey")
            exists2 <- client.get(REGION, "deleteKey")
        } yield {
            v should equal("value")
            exists should equal(true)
            assert(v2 == null)
            exists2 should equal(false)
        }
    }

    test("multi get") {
        val count = 100
        val keys = ArrayBuffer[String]()

        (0 until count).foreach { x =>
            val key = "key-" + x
            client.set(REGION, key, x)
            keys += key
        }

        val future = client.multiGet(REGION, keys)
        future.map(values => values.size should equal(keys.size))
    }

    test("multi delete") {
        val count = 100
        val keys = ArrayBuffer[String]()

        (0 until count).foreach { x =>
            val key = "key-" + x
            client.set(REGION, key, x)
            keys += key
        }

        val future = client.multiGet(REGION, keys)
        future.map(values => values.size should equal(keys.size))

        client.multiDelete(REGION, keys)

        (0 until count).foreach { x =>
            val key = "key-" + x
            client.get(REGION, key) map {
                v => assert(v == null)
            }
        }
    }

    test("keys in region") {

        Promises.await(client.flushDb())

        val count = 100
        val keys = ArrayBuffer[String]()

        (0 until count).foreach { x =>
            val key = "key-" + x
            client.set(REGION, key, x)
            keys += key
        }

        // 비동기 기다리는 거 손 쉽게 하려고...
        Thread.sleep(10)

        client.keysInRegion(REGION) map { keys => assert(keys.size == count)}

        client.deleteRegion(REGION)

        client.keysInRegion(REGION) map { keys => assert(keys.size == 0)}
    }
}
