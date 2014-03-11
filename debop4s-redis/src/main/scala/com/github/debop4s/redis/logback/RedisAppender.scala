package com.github.debop4s.redis.logback

import ch.qos.logback.classic.spi.{ThrowableProxyUtil, LoggingEvent}
import ch.qos.logback.core.{CoreConstants, UnsynchronizedAppenderBase}
import com.github.debop4s.core.json.ScalaJacksonSerializer
import com.github.debop4s.core.logback.LogDocument
import com.github.debop4s.core.utils.Options
import com.github.debop4s.redis.RedisConsts
import org.joda.time.DateTime
import redis.RedisClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * log를 client 에 쓰는 logback 용 appender 입니다.
 * TODO: JsonEventLayout 을 사용하지 말고, LogDocument Class 를 만들고, 이 것을 Jackson으로 Serialize 하도록 변경한다.
 * Created by debop on 2014. 2. 22.
 */
class RedisAppender extends UnsynchronizedAppenderBase[LoggingEvent] {

  implicit val akkaSystem = akka.actor.ActorSystem()

  lazy val serializer = ScalaJacksonSerializer()
  protected var redis: RedisClient = null

  var host = "localhost"
  var port = RedisConsts.DEFAULT_PORT
  var timeout = RedisConsts.DEFAULT_TIMEOUT
  var password: String = ""
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
        redis = RedisClient(host, port, Options.toOption(password), Some(database))
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

    future {
      try {
        val doc = createLogDocument(eventObject)
        val jsonDoc = toJsonText(doc)
        redis.lpush(key, jsonDoc)
      }
    }
  }

  /**
   * 로그 정보를 [[LogDocument]] 빌드합니다.
   */
  protected def createLogDocument(event: LoggingEvent): LogDocument = {
    val doc = new LogDocument()

    doc.serverName = this.serverName
    doc.applicationName = this.applicationName

    doc.logger = event.getLoggerName
    doc.levelInt = event.getLevel.levelInt
    doc.levelStr = event.getLevel.levelStr
    doc.threadName = event.getThreadName
    doc.timestamp = new DateTime(event.getTimeStamp)
    doc.message = event.getFormattedMessage

    if (event.getMarker != null)
      doc.marker = event.getMarker.getName

    val tp = event.getThrowableProxy
    if (tp != null) {
      val tpStr = ThrowableProxyUtil.asString(tp)
      val stacktrace = tpStr.replace("\t", "").split(CoreConstants.LINE_SEPARATOR)
      if (stacktrace != null && stacktrace.length > 0)
        doc.exception = stacktrace(0)
      if (stacktrace.length > 1)
        doc.stacktrace ++= stacktrace.drop(1)
    }
    doc
  }

  protected def toJsonText(document: LogDocument): String = {
    serializer.serializeToText(document)
  }
}

object RedisAppender {

  val DEFAULT_KEY = "logback:logs"

  def apply(): RedisAppender = new RedisAppender()
}
