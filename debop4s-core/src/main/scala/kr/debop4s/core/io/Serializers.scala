package kr.debop4s.core.io

import java.io.{InputStream, ByteArrayOutputStream, OutputStream}
import java.util.Objects
import kr.debop4s.core.BinaryStringFormat
import kr.debop4s.core.logging.Logger
import kr.debop4s.core.parallels.Asyncs
import kr.debop4s.core.utils.{Arrays, Streams, Strings}
import scala.concurrent.Future

/**
 *
 * [[Serializer]] 를 위한 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 28. 오후 4:59
 */
object Serializers {

    lazy val log = Logger(this.getClass)
    lazy val serializer = new BinarySerializer()

    def serializeAsString[T <: AnyRef](serializer: Serializer, graph: T): String = {
        if (graph == null)
            Strings.EMPTY_STR
        else
            Strings.getStringFromBytes(serializer.serialize(graph), BinaryStringFormat.HexDecimal)
    }

    def deserializeAsString[T <: AnyRef](serializer: Serializer, str: String, clazz: Class[T]): T = {
        serializer.deserialize(Strings.getBytesFromHexString(str), clazz)
    }

    def serializeAsStream[T <: AnyRef](serializer: Serializer, graph: T): OutputStream = {
        if (Objects.equals(graph, null)) new ByteArrayOutputStream()
        else Streams.toOutputStream(serializer.serialize(graph))
    }

    def deserializeFromStream[T <: AnyRef](serializer: Serializer, clazz: Class[T], inputStream: InputStream): T = {
        if (inputStream == null) null.asInstanceOf[T]
        else serializer.deserialize[T](Streams.toByteArray(inputStream), clazz)
    }

    def serializeObject[T <: AnyRef](graph: T): Array[Byte] = serializer.serialize(graph)

    def deserializeObject[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]): T =
        serializer.deserialize[T](bytes, clazz)

    def copyObject[T <: AnyRef](graph: T): T = {
        if (Objects.equals(graph, null))
            null.asInstanceOf[T]
        else
            deserializeObject[T](serializeObject(graph), graph.getClass.asInstanceOf[Class[T]])
    }

    def serializeObjectAsync[T <: AnyRef](graph: T): Future[Array[Byte]] = {
        if (Objects.equals(graph, null))
            Asyncs.getTaskHasResult(Array.emptyByteArray)
        else Asyncs.startNew {
                                 serializer.serialize(graph)
                             }
    }

    def deserializeObjectAsync[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]): Future[T] = {
        if (Arrays.isEmpty(bytes))
            Asyncs.getTaskHasResult(null.asInstanceOf[T])
        else Asyncs.startNew {
                                 serializer.deserialize(bytes, clazz)
                             }
    }

    def copyObjectAsync[T <: AnyRef](graph: T): Future[T] = {
        if (Objects.equals(graph, null))
            Asyncs.getTaskHasResult(null.asInstanceOf[T])
        else Asyncs.startNew {
                                 val bytes = serializer.serialize(graph)
                                 serializer.deserialize(bytes, graph.getClass)
                             }
    }
}
