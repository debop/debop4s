package debop4s.core.logback

import java.util

import debop4s.core.{ToStringHelper, ValueObjectBase}
import org.joda.time.DateTime
import scala.beans.BeanProperty
import scala.collection.mutable

/**
 * logback 로그 정보를 표현하는 클래스입니다.
 * Created by debop on 2014. 2. 22.
 */
@SerialVersionUID(1431014486199195378L)
class LogDocument extends ValueObjectBase {

  @BeanProperty var serverName: String = null
  @BeanProperty var applicationName: String = null
  @BeanProperty var logger: String = null
  @BeanProperty var levelInt: Int = 0
  @BeanProperty var levelStr: String = null
  @BeanProperty var threadName: String = null
  @BeanProperty var message: String = null
  @BeanProperty var timestamp: DateTime = null
  @BeanProperty var marker: String = null
  @BeanProperty var exception: String = null
  @BeanProperty var stacktrace: util.List[String] = new util.ArrayList[String]()

  override def toString: String = {
    ToStringHelper(this)
    .add("serverName", serverName)
    .add("applicationName", applicationName)
    .add("logger", logger)
    .add("level", levelStr)
    .add("threadName", threadName)
    .add("message", message)
    .add("timestamp", timestamp)
    .add("marker", marker)
    .add("exception", exception)
    .add("stacktrace", stacktrace)
    .toString
  }
}
