package com.github.debop4s.core.io

import de.ruedigermoeller.serialization.FSTConfiguration
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success, Try}

/**
 * Fast-Serialization 라이브러리를 이용한 Serializer 입니다.
 * Created by debop on 2014. 3. 18.
 */
@deprecated(message = "Tomcat 등에서 Float 수형에 대한 변환이 제대로 안되는 버그가 있다.", since = "0.3.0")
class FstSerializer extends Serializer {

    private lazy val log = LoggerFactory.getLogger(getClass)

    private val conf = FSTConfiguration.createDefaultConfiguration()

    /**
    * 객체를 직렬화 합니다.
    * @param graph 직렬화할 객체
    * @return 직렬화된 정보를 가진 바이트 배열
    */
    @inline
    def serialize[T](graph: T): Array[Byte] = {
        if (graph == null || graph == None)
            return Array.emptyByteArray

        var bos = None: Option[ByteArrayOutputStream]

        try {
            bos = Some(new ByteArrayOutputStream())
            Try(conf.getObjectOutput(bos.get)) match {

                case Success(oos) =>
                    oos.writeObject(graph, Seq[Class[_]](): _*)
                    oos.flush()
                    bos.get.toByteArray

                case Failure(e) =>
                    log.error(s"Fail to serialize graph. $graph", e)
                    Array.emptyByteArray
            }
        } finally {
            if (bos.isDefined) bos.get.close()
        }
    }

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    @inline
    def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        var bis = None: Option[ByteArrayInputStream]

        try {
            bis = Some(new ByteArrayInputStream(bytes))
            Try(conf.getObjectInput(bis.get)) match {

                case Success(ois) =>
                    ois.readObject.asInstanceOf[T]

                case Failure(e) =>
                    log.error(s"Fail to deserialize data.", e)
                    null.asInstanceOf[T]
            }
        } finally {
            if (bis.isDefined) bis.get.close()
        }
    }
}
