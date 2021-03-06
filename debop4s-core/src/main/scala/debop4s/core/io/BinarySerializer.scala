package debop4s.core.io

import java.io._

import debop4s.core.utils.Closer._
import debop4s.core.utils._

object BinarySerializer {
  def apply(): BinarySerializer = new BinarySerializer()
}

/**
  * Binary Serializer
  *
  * @author 배성혁 sunghyouk.bae@gmail.com
  * @since 2013. 12. 10. 오후 5:37
  */
class BinarySerializer extends Serializer {

  /**
    * 객체를 직렬화 합니다.
    *
    * @param graph 직렬화할 객체
    * @return 직렬화된 정보를 가진 바이트 배열
    */
  def serialize[@miniboxed T](graph: T): Array[Byte] = {
    if (graph == null)
      return Array.emptyByteArray

    //    val bos = new ByteArrayOutputStream()

    using(new ByteArrayOutputStream()) { bos =>
      using(new ObjectOutputStream(bos)) { oos =>
        oos.writeObject(graph)
        oos.flush()
        return bos.toByteArray
      }
    }
  }

  /**
    * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
    *
    * @param bytes 직렬화된 바이트 배열
    * @return 역직렬화된 객체 정보
    */
  def deserialize[@miniboxed T](bytes: Array[Byte], clazz: Class[T]): T = {
    if (Arrays.isEmpty(bytes))
      return null.asInstanceOf[T]

    using(new ByteArrayInputStream(bytes)) { bis =>
      val ois = new ObjectInputStream(bis)
      using(ois) { input =>
        return input.readObject().asInstanceOf[T]
      }
    }
  }
}
