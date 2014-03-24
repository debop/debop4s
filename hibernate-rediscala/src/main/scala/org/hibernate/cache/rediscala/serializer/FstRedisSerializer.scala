package org.hibernate.cache.rediscala.serializer

import de.ruedigermoeller.serialization.FSTConfiguration
import java.io
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success, Try}


/**
 * FstRedisSerializer
 * Created by debop on 2014. 3. 18.
 */
class FstRedisSerializer[T] extends RedisSerializer[T] {

    private lazy val log = LoggerFactory.getLogger(getClass)


    override def serialize(graph: T): Array[Byte] = {
        if (graph == null || graph == None)
            return EMPTY_BYTES

        var bos = None: Option[ByteArrayOutputStream]

        try {
            bos = Some(new io.ByteArrayOutputStream())
            Try(FstRedisSerializer.conf.getObjectOutput(bos.get)) match {

                case Success(oos) =>
                    oos.writeObject(graph, Seq[Class[_]](): _*)
                    oos.flush()
                    bos.get.toByteArray

                case Failure(e) =>
                    log.error(s"Fail to serialize graph. $graph", e)
                    EMPTY_BYTES
            }
        } finally {
            if (bos.isDefined) bos.get.close()
        }
    }

    override def deserialize(bytes: Array[Byte]): T = {
        if (bytes == null || bytes.length == 0)
            return null.asInstanceOf[T]

        var bis = None: Option[ByteArrayInputStream]

        try {
            bis = Some(new ByteArrayInputStream(bytes))
            Try(FstRedisSerializer.conf.getObjectInput(bis.get)) match {

                case Success(ois) =>
                    val instance = ois.readObject.asInstanceOf[T]
                    instance

                case Failure(e) =>
                    log.error(s"Fail to deserialize data.", e)
                    null.asInstanceOf[T]
            }
        } finally {
            if (bis.isDefined) bis.get.close()
        }
    }
}

object FstRedisSerializer {
    lazy val conf = FSTConfiguration.createDefaultConfiguration()
}
