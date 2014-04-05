package com.github.debop4s.core.io

import com.github.debop4s.core._
import com.github.debop4s.core.utils.{Streams, Strings}
import java.io.{InputStream, ByteArrayOutputStream, OutputStream}
import java.util.Objects
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 *
 * [[Serializer]] 를 위한 Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 28. 오후 4:59
 */
object Serializers {

    private lazy val log = LoggerFactory.getLogger(getClass)

    lazy val serializer = new FstSerializer()

    def serializeAsString[T](serializer: Serializer, graph: T): String = {
        if (graph == null)
            return Strings.EMPTY_STR

        Strings.getStringFromBytes(serializer.serialize(graph), BinaryStringFormat.HexDecimal)
    }

    def deserializeFromString[T](serializer: Serializer, str: String, clazz: Class[T]): T = {
        if (Strings.isEmpty(str)) null.asInstanceOf[T]
        else serializer.deserialize(Strings.getBytesFromHexString(str), clazz)
    }

    def serializeAsStream[T](serializer: Serializer, graph: T): OutputStream = {
        if (Objects.equals(graph, null)) new ByteArrayOutputStream()
        else Streams.toOutputStream(serializer.serialize(graph))
    }

    def deserializeFromStream[T](serializer: Serializer, clazz: Class[T], inputStream: InputStream): T = {
        if (inputStream == null) null.asInstanceOf[T]
        else serializer.deserialize[T](Streams.toByteArray(inputStream), clazz)
    }

    def serializeObject[T](graph: T): Array[Byte] =
        serializer.serialize(graph)

    def deserializeObject[T](bytes: Array[Byte], clazz: Class[T]): T =
        serializer.deserialize[T](bytes, clazz)

    def copyObject[T](graph: T): T = {
        if (Objects.equals(graph, null))
            return null.asInstanceOf[T]

        deserializeObject[T](serializeObject(graph), graph.getClass.asInstanceOf[Class[T]])
    }

    def serializeObjectAsync[T](graph: T): Future[Array[Byte]] = future {
        serializer.serialize(graph)
    }

    def deserializeObjectAsync[T](bytes: Array[Byte], clazz: Class[T]): Future[T] = future {
        serializer.deserialize(bytes, clazz)
    }

    def copyObjectAsync[T](graph: T): Future[T] = future {
        if (Objects.equals(graph, null))
            return null

        val bytes = serializer.serialize(graph)
        serializer.deserialize(bytes, graph.getClass)
    }
}
