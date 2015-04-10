package debop4s

import java.lang.{Boolean => JBoolean, Double => JDouble, Float => JFloat, Integer => JInt, Long => JLong}
import java.util.concurrent.TimeUnit
import java.util.{List => JList, Properties}

import com.typesafe.config.ConfigValue
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

/**
 * 환경설정에 사용할 상수와 extension methods 를 제공합니다.
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
package object config {

  private lazy val LOG = LoggerFactory.getLogger(getClass)

  private lazy val processCount = sys.runtime.availableProcessors()

  /** Database Connection Pool 의 최대 크기 */
  lazy val MAX_POOL_SIZE: Int = processCount * 16

  /** Database Connection Pool 의 최소 크기 */
  lazy val MIN_POOL_SIZE: Int = processCount * 2

  /** Database Connection Pool 의 Idle 크기 */
  lazy val MIN_IDLE_SIZE: Int = 2

  implicit class ConfigExtensions(val cfg: com.typesafe.config.Config) {

    def tryGetString(path: String, defaultValue: String = ""): String =
      tryGet(path, defaultValue) {
        cfg.getString(path)
      }

    def tryGetStringList(path: String, defaultValue: JList[String] = Seq.empty.asJava): JList[String] =
      tryGet(path, defaultValue) {
        cfg.getStringList(path)
      }

    def tryGetBoolean(path: String, defaultValue: Boolean = false): Boolean =
      tryGet(path, defaultValue) {
        cfg.getBoolean(path)
      }

    def tryGetBooleanList(path: String, defaultValue: JList[JBoolean] = Seq.empty.asJava): JList[JBoolean] =
      tryGet(path, defaultValue) {
        cfg.getBooleanList(path)
      }

    def tryGetInt(path: String, defaultValue: Int = 0): Int =
      tryGet(path, defaultValue) {
        cfg.getInt(path)
      }

    def tryGetIntList(path: String, defaultValue: JList[JInt] = Seq.empty.asJava): JList[JInt] =
      tryGet(path, defaultValue) {
        cfg.getIntList(path)
      }


    def tryGetLong(path: String, defaultValue: Long = 0L): Long =
      tryGet(path, defaultValue) {
        cfg.getLong(path)
      }

    def tryGetLongList(path: String, defaultValue: JList[JLong] = Seq.empty.asJava): JList[JLong] =
      tryGet(path, defaultValue) {
        cfg.getLongList(path)
      }

    def tryGetDouble(path: String, defaultValue: Double = 0.0): Double =
      tryGet(path, defaultValue) {
        cfg.getDouble(path)
      }

    def tryGetDoubleList(path: String, defaultValue: JList[JDouble] = Seq.empty.asJava): JList[JDouble] =
      tryGet(path, defaultValue) {
        cfg.getDoubleList(path)
      }

    def tryGetNumber(path: String, defaultValue: Number = 0): Number =
      tryGet(path, defaultValue) {
        cfg.getNumber(path)
      }

    def tryGetNumberList(path: String, defaultValue: JList[Number] = Seq.empty[Number].asJava): JList[Number] =
      tryGet(path, defaultValue) {
        cfg.getNumberList(path)
      }

    def tryGetAnyRef(path: String, defaultValue: AnyRef = null): AnyRef =
      tryGet(path, defaultValue) {
        cfg.getAnyRef(path)
      }

    def tryGetAnyRefList(path: String, defaultValue: JList[AnyRef] = null): JList[AnyRef] =
      tryGet(path, defaultValue) {
        cfg.getAnyRefList(path).asInstanceOf[JList[AnyRef]]
      }

    def tryGetBytes(path: String, defaultValue: JLong = 0L): JLong =
      tryGet(path, defaultValue) {
        cfg.getBytes(path)
      }

    def tryGetBytesList(path: String, defaultValue: JList[JLong] = Seq.empty[JLong].asJava): JList[JLong] =
      tryGet(path, defaultValue) {
        cfg.getBytesList(path)
      }

    def tryGetDuration(path: String, timeunit: TimeUnit = TimeUnit.SECONDS, defaultValue: Long = 0L): Long =
      tryGet(path, defaultValue) {
        cfg.getDuration(path, timeunit)
      }

    def tryGetDurationList(path: String, timeunit: TimeUnit = TimeUnit.SECONDS, defaultValue: JList[JLong] = Seq.empty.asJava): JList[JLong] =
      tryGet(path, defaultValue) {
        cfg.getDurationList(path, timeunit)
      }

    def tryGetValue(path: String, defaultValue: ConfigValue): ConfigValue =
      tryGet(path, defaultValue) {
        cfg.getValue(path)
      }


    def tryGet[T](path: String, defaultValue: T)(block: => T): T = {
      Try { block } match {
        case Success(v) => v
        case Failure(e) =>
          LOG.warn(s"환경설정에 정의되지 않아 기본값을 반환합니다. resourceBasename=$path, defaultValue=$defaultValue")
          defaultValue
      }
    }

    def asProperties(): Properties = {
      val props = new Properties()
      cfg.entrySet().asScala.foreach { entry =>
        props.put(entry.getKey, entry.getValue.unwrapped().toString)
      }
      props
    }
  }

}
