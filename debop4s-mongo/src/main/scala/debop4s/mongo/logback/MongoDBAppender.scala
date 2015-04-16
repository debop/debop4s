package debop4s.mongo.logback

import ch.qos.logback.classic.spi.{ILoggingEvent, LoggingEvent, ThrowableProxyUtil}
import ch.qos.logback.core.{CoreConstants, UnsynchronizedAppenderBase}
import com.mongodb.{MongoClient, ServerAddress}
import debop4s.core.utils.Strings
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.data.authentication.UserCredentials
import org.springframework.data.mongodb.core.MongoTemplate

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
 * log를 client 에 쓰는 kr.hconnect.redis.logback 용 appender 입니다.
 * @author sunghyouk.bae@gmail.com
 */
class MongoDBAppender extends UnsynchronizedAppenderBase[LoggingEvent] {
  private lazy val log = LoggerFactory.getLogger(getClass)

  final val DB_NAME: String = "logDB"
  final val COLLECTION_NAME: String = "logs"

  private[logback] var mongo: MongoTemplate = _
  private[logback] var client: MongoClient = _

  @BeanProperty var serverName: String = null
  @BeanProperty var applicationName: String = null
  @BeanProperty var host: String = ServerAddress.defaultHost()
  @BeanProperty var port: Int = ServerAddress.defaultPort()
  @BeanProperty var dbName: String = DB_NAME
  @BeanProperty var collectionName: String = COLLECTION_NAME
  @BeanProperty var username: String = null
  @BeanProperty var password: String = null

  override def start() {
    try {
      connect()
      super.start()
    } catch {
      case NonFatal(e) => addError(s"MongoDB에 연결하지 못했습니다. host=$host", e)
    }
  }

  private def connect() {

    log.debug(s"connecting mongodb. host=$host, port=$port")

    client = new MongoClient(host, port)

    val userCredentials =
      if (username != null && password != null) new UserCredentials(username, password)
      else UserCredentials.NO_CREDENTIALS

    if (Strings.isEmpty(dbName))
      dbName = DB_NAME

    mongo = new MongoTemplate(client, dbName, userCredentials)
    if (Strings.isEmpty(collectionName))
      collectionName = COLLECTION_NAME

    if (!mongo.collectionExists(collectionName))
      mongo.createCollection(collectionName)
  }

  override def append(eventObject: LoggingEvent) {
    if (eventObject == null)
      return

    Future {
      val doc = createLogDocument(eventObject)
      mongo.save(doc, collectionName)
    }
  }

  private def createLogDocument(event: ILoggingEvent): MongoLogDocument = {
    val doc = new MongoLogDocument()

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
      val stacktrace: Array[String] = tpStr.replace("\t", "").split(CoreConstants.LINE_SEPARATOR)

      if (stacktrace.length > 0) {
        doc.setException(stacktrace.head)
      }
      if (stacktrace.length > 1) {
        doc.setStacktrace(stacktrace.tail.toList.asJava)
      }
    }
    doc
  }
}
