package debop4s.redis.base

import debop4s.core.parallels.Asyncs
import java.util.concurrent.TimeUnit
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

/**
 * Redis 에 응용할 수 있는 응용 기능을 제공합니다.
 * @author Sunghyouk Bae
 */
class RedisHelper(val redis: RedisClient) {

    /**
     * 특정 키 값을 1 증가 시키고, 그 값을 반환한다.
     * Id 값 관리에 유용하다.
     */
    def increseAndGet(key: String, increment: Long = 1): Future[Long] = {
        redis.incrby(key, increment)
        .andThen {
            case Success(x) => redis.get(key).map(x => x.getOrElse(0))
        }
    }

    /**
     * 특정 키 값을 1 감소 시키고, 그 값을 반환한다.
     * Id 값 관리에 유용하다.
     */
    def decreaseAndGet(key: String, decrement: Long = 1) = {
        redis.decrby(key, decrement)
        .andThen {
            case Success(x) => redis.get(key).map(x => x.getOrElse(0))
        }
    }

    /**
     * lock 을 설정합니다.
     */
    def lock(lockName: String): Future[Boolean] = {
        require(lockName != null && lockName.length > 0)
        redis.set(lockName, lockName)
    }

    /**
     * lock 을 설정하는데, expiration을 설정할 수 있습니다. expire 되면 lock 은 자동으로 사라집니다.
     */
    def lockEx(lockName: String, expireInSecond: Long): Future[Boolean] = {
        require(lockName != null && lockName.length > 0)
        redis.setex(lockName, expireInSecond, lockName)
    }

    /**
     * 지정된 Lock 을 unlock 시킵니다.
     */
    def unlock(lockName: String): Future[Boolean] = {
        require(lockName != null && lockName.length > 0)
        redis.del(lockName).map(x => x > 0)
    }

    /**
     * lock 이 풀릴 때까지 대기합니다.
     * @param lockName Lock name
     * @param timeout 대기 최대 시간 (단위 milliseconds)
     */
    def waitForUnlock(lockName: String, timeout: Long): Boolean = {
        var retry = false
        var foundLock = false

        val timeoutMillis = System.currentTimeMillis() + timeout

        do {
            retry = false
            if (Asyncs.result(redis.exists(lockName))) {
                foundLock = true
                try {
                    Thread.sleep(5)
                } catch {
                    case ignored: InterruptedException =>
                    case e: Throwable => throw new RuntimeException(s"Fail to wait for unlock. lockName=$lockName", e)
                }
                retry = true
            }
        } while (retry && timeoutMillis < System.currentTimeMillis())
        foundLock
    }

    /**
     * lock 이 풀릴 때까지 대기합니다.
     * @param lockName Lock name
     * @param timeout 대기 최대 시간 (단위 milliseconds)
     */
    def waitForUnlock(lockName: String, timeout: Long, unit: TimeUnit): Boolean = {
        val timeoutInMillis = unit.toMillis(timeout)
        waitForUnlock(lockName, timeoutInMillis)
    }
}

object RedisHelper {

    implicit val actorSystem = akka.actor.ActorSystem()

    def apply(): RedisHelper = new RedisHelper(RedisClient())
}
