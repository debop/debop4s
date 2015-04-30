package debop4s.core.io

import scala.pickling._
import scala.pickling.binary._

/**
 * PicklingSerializer
 * @author sunghyouk.bae@gmail.com
 */
class PicklingSerializer extends Serializer {
  /**
   * 객체를 직렬화 합니다.
   * @param graph 직렬화할 객체
   * @return 직렬화된 정보를 가진 바이트 배열
   */
  override def serialize[@miniboxed T](graph: T): Array[Byte] = {
    if (graph == null) Array.emptyByteArray
    else graph.asInstanceOf[Any].pickle.value
  }
  /**
   * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
   * @param bytes 직렬화된 바이트 배열
   * @return 역직렬화된 객체 정보
   */
  override def deserialize[@miniboxed T](bytes: Array[Byte], clazz: Class[T]): T = {
    if (bytes == null || bytes.length == 0) null.asInstanceOf[T]
    else toBinaryPickle(bytes).unpickle[Any].asInstanceOf[T]
  }
}
