package com.github.debop4s.redis.base

import akka.util.ByteString
import com.github.debop4s.redis.serializer.BinaryRedisSerializer
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * 크기가 제한된 컬력센입니다.
 * @author Sunghyouk Bae
 */
class RedisCappedCollection[T](val redis: RedisClient,
                               val collectionName: String,
                               val collectionSize: Long = Long.MaxValue) {

    lazy val serializer = new BinaryRedisSerializer[T]()

    /**
    * 리스트에 새로운 객체를 추가합니다.
    */
    def lpush(value: T): Future[Long] = {
        val bs = ByteString(serializer.serialize(value))
        val future = redis.lpush(collectionName, bs)

        // 리스트에 데이터
        future onSuccess { case x => trim()}
        future
    }

    /**
     * 리스트에서 객체를 조회합니다.
     */
    def get(index: Long): Future[T] = {
        assert(index >= 0 && index < collectionSize, s"index 의 범위가 벗어났습니다. [0, $collectionSize) 사이어야 합니다. index=$index")
        redis.lindex(collectionName, index).map { v => serializer.deserialize(v.get.toArray)}
    }

    def getRange(start: Long, end: Long): Future[Iterable[T]] = {
        redis.lrange(collectionName, start, end).map { list =>
            list.map(x => serializer.deserialize(x.toArray))
        }
    }

    private def trim(): Future[Boolean] = {
        redis.ltrim(collectionName, 0, collectionSize)
    }

}
