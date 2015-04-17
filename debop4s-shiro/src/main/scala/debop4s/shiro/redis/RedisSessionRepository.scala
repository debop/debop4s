package debop4s.shiro.redis

import java.io.Serializable
import java.util

import debop4s.rediscala.client.RedisSyncClient
import debop4s.rediscala.serializer.FstValueFormatter
import org.apache.shiro.session.Session
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import redis.ByteStringFormatter

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
 * Session 정보를 Redis 서버에서 관리하는 Repository 입니다.
 * @author sunghyouk.bae@gmail.com
 */
@Component
class RedisSessionRepository extends AbstractSessionDAO {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val redis: RedisSyncClient = null

  @BeanProperty var keyPrefix = "shiro:session:"
  @BeanProperty var timeout: Long = 30 * 60 * 1000L

  implicit val valueFormatter: ByteStringFormatter[Session] = new FstValueFormatter[Session]()

  override def doCreate(session: Session): Serializable = {
    val sessionId = this.generateSessionId(session)
    this.assignSessionId(session, sessionId)
    this.saveSession(session)
    sessionId
  }

  override def doReadSession(sessionId: Serializable): Session = {
    if (sessionId == null) {
      log.warn(s"session id is null.")
      return null
    }
    redis.get[Session](cacheKey(sessionId)).getOrElse(null.asInstanceOf[Session])
  }

  override def getActiveSessions: util.Collection[Session] = {
    val sessionIds = redis.keys(keyPrefix + "*")
    sessionIds.map { sessionId => doReadSession(sessionId) }.asJavaCollection
  }

  override def update(session: Session) {
    saveSession(session)
  }

  override def delete(session: Session) {
    if (session == null || session.getId == null) {
      log.warn(s"session or session id is null.")
      return
    }
    redis.del(cacheKey(session.getId))
  }

  private def cacheKey(sessionId: Serializable): String = {
    keyPrefix + sessionId.toString
  }

  private def saveSession(session: Session) {
    if (session == null || session.getId == null) {
      log.warn(s"session or session id is null.")
    } else {
      session.setTimeout(timeout)
      redis.set(cacheKey(session.getId), session, exSeconds = Some(timeout))
    }
  }


}
