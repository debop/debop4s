package debop4s.rediscala.logback

import ch.qos.logback.classic.spi.{LoggingEvent, ThrowableProxyUtil}
import ch.qos.logback.core.{CoreConstants, UnsynchronizedAppenderBase}
import debop4s.core._
import debop4s.core.json.JacksonSerializer
import debop4s.core.logback.LogDocument
import debop4s.rediscala.{RedisConsts, _}
import org.joda.time.DateTime
import redis.RedisClient

import scala.collection.JavaConverters._

/**
 * log를 client 에 쓰는 debop4s.rediscala.logback 용 appender 입니다.
 * TODO: JsonEventLayout 을 사용하지 말고, LogDocument Class 를 만들고, 이 것을 Jackson으로 Serialize 하도록 변경한다.
 * Created by debop on 2014. 2. 22.
 */
class RedisAppender extends UnsynchronizedAppenderBase[LoggingEvent] {

  private lazy val serializer = JacksonSerializer()
  @volatile protected var redis: RedisClient = null

  var host = "localhost"
  var port = RedisConsts.DEFAULT_PORT
  var timeout = RedisConsts.DEFAULT_TIMEOUT
  var password: String = null
  var database = RedisConsts.DEFAULT_DATABASE

  var key: String = RedisAppender.DEFAULT_KEY
  var serverName: String = ""
  var applicationName: String = ""

  def setHost(host: String = "localhost") {
    this.host = host
  }

  def setPort(port: Int = 6379) {
    this.port = port
  }

  def setPassword(password: String) {
    this.password = password
  }

  def setDatabase(database: Int = 0) {
    this.database = database
  }

  def setKey(key: String = RedisAppender.DEFAULT_KEY) {
    this.key = key
  }

  def setServerName(serverName: String) {
    this.serverName = serverName
  }

  def setApplicationName(applicationName: String) {
    this.applicationName = applicationName
  }

  override def start() {
    print(s"start Redis Logging Appender")
    synchronized {
      if (redis == null) {
        println(s"host=$host, port=$port, password=$password, database=$database ")
        redis = RedisClient(host, port, Option(password), Some(database))
      }
      super.start()
    }
  }

  override def stop() {
    super.stop()
    redis = null
  }

  override def append(eventObject: LoggingEvent) {
    if (eventObject == null)
      return

    val doc = createLogDocument(eventObject)
    val jsonDoc = toJsonText(doc)
    redis.lpush(key, jsonDoc).await
  }

  /**
   * 로그 정보를 [[debop4s.core.logback.LogDocument]] 빌드합니다.
   */
  protected def createLogDocument(event: LoggingEvent): LogDocument = {
    val doc = new LogDocument() {
      serverName = this.serverName
      applicationName = this.applicationName

      logger = event.getLoggerName
      levelInt = event.getLevel.levelInt
      levelStr = event.getLevel.levelStr
      threadName = event.getThreadName
      timestamp = new DateTime(event.getTimeStamp)
      message = event.getFormattedMessage
    }
    if (event.getMarker != null)
      doc.marker = event.getMarker.getName

    val tp = event.getThrowableProxy
    if (tp != null) {
      val tpStr = ThrowableProxyUtil.asString(tp)
      val stacktrace = tpStr.replace("\t", "").split(CoreConstants.LINE_SEPARATOR)

      if (stacktrace.size > 0) {
        doc.setException(stacktrace.head)
      }
      if (stacktrace.size > 1) {
        doc.setStacktrace(stacktrace.tail.toList.asJava)
      }
    }
    doc
  }

  protected def toJsonText(document: LogDocument): String = {
    serializer.serializeToText(document)
  }
}

object RedisAppender {

  val DEFAULT_KEY = "debop4s.rediscala.logback:logs"

  def apply(): RedisAppender = new RedisAppender()
}
