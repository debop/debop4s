package org.hibernate.cache.rediscala.client

import java.util.concurrent.TimeUnit

import org.hibernate.cache.rediscala._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
 * org.hibernate.cache.rediscala.client.CacheClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 20. 오후 3:04
 */
class HibernateRedisCacheFunSuite extends AbstractHibernateRedisFunSuite {

  val client = RedisCache()

  val REGION = RedisCache.DEFAULT_REGION_NAME

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

    client.multiDelete(REGION, keys).await

    Thread.sleep(400)

    (0 until count).map { x =>
      val key = "key-" + x
      client.get(REGION, key) map {
        v => assert(v == null)
      }
    }.awaitAll
  }

  test("keys in region") {

    client.flushDb().await

    val count = 100
    val keys = new ArrayBuffer[String](count)
    val results = new ArrayBuffer[Future[Boolean]](count)

    (0 until count).foreach { x =>
      val key = "key-" + x
      results += client.set(REGION, key, x)
      keys += key
    }

    results.awaitAll
    client.keysInRegion(REGION) map { keys => assert(keys.size == count) }
    client.deleteRegion(REGION)
    client.keysInRegion(REGION) map { keys => assert(keys.size == 0) }
  }
}
