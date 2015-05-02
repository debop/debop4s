package org.hibernate.cache.rediscala.serializer

import akka.util.ByteString
import redis.ByteStringFormatter

/**
 * 캐시 정보를 Redis 서버에 저장할 때 사용할 Formatter 입니다.
 *
 * @param serializer [[org.hibernate.cache.spi.entry.CacheEntry]] 정보를 직렬화/역직렬화 합니다.
 * @tparam T  Redis Value 의 수형
 */
abstract class CacheEntryFormatter[@miniboxed T](val serializer: RedisSerializer[T]) extends ByteStringFormatter[T] {

  override def serialize(data: T): ByteString = {
    data match {
      case null => ByteString.empty
      case _ => ByteString(serializer.serialize(data))
    }

  }

  override def deserialize(bs: ByteString): T = {
    bs match {
      case null => null.asInstanceOf[T]
      case _ => serializer.deserialize(bs.toArray)
    }
  }
}

class BinaryCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](BinaryRedisSerializer[T]()) {}

class FstCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](FstRedisSerializer[T]()) {}

class SnappyBinaryCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](SnappyRedisSerializer[T](BinaryRedisSerializer[T]())) {}

class SnappyFstCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](SnappyRedisSerializer[T](FstRedisSerializer[T]())) {}

class LZ4BinaryCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](LZ4RedisSerializer[T](BinaryRedisSerializer[T]()))

class LZ4FstCacheEntryFormatter[@miniboxed T]
  extends CacheEntryFormatter[T](LZ4RedisSerializer[T](FstRedisSerializer[T]()))