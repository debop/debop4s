package debop4s.core.io

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import debop4s.core.utils.Closer._
import org.nustaq.serialization.FSTConfiguration
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

object FstSerializer {

  def apply(conf: FSTConfiguration = FSTConfiguration.createDefaultConfiguration()) =
    new FstSerializer(conf)
}

/**
 * Fast-Serialization 라이브러리를 이용한 Serializer 입니다.
 * @see [[http://ruedigermoeller.github.io/fast-serialization/]]
 */
class FstSerializer(val conf: FSTConfiguration) extends Serializer {

  def this() = this(FSTConfiguration.createDefaultConfiguration())

  private val log = LoggerFactory.getLogger(getClass)

  /**
   * 객체를 직렬화 합니다.
   * @param graph 직렬화할 객체
   * @return 직렬화된 정보를 가진 바이트 배열
   */
  def serialize[@miniboxed T](graph: T): Array[Byte] = {
    if (graph == null || graph == None)
      return Array.emptyByteArray

    using(new ByteArrayOutputStream()) { bos =>
      Try(conf.getObjectOutput(bos)) match {
        case Success(oos) =>
          oos.writeObject(graph, Seq[Class[_]](): _*)
          oos.flush()
          bos.toByteArray
        case Failure(e) =>
          log.error("Fail to serialize graph. {}", graph, e)
          Array.emptyByteArray
      }
    }
  }

  /**
   * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
   * @param bytes 직렬화된 바이트 배열
   * @return 역직렬화된 객체 정보
   */
  def deserialize[@miniboxed T](bytes: Array[Byte], clazz: Class[T] = classOf[Any]): T = {
    if (bytes == null || bytes.length == 0)
      return null.asInstanceOf[T]

    using(new ByteArrayInputStream(bytes)) { bis =>
      Try(conf.getObjectInput(bis)) match {
        case Success(ois) =>
          ois.readObject.asInstanceOf[T]
        case Failure(e) =>
          log.error("Fail to deserialize data.", e)
          null.asInstanceOf[T]
      }
    }
  }
}