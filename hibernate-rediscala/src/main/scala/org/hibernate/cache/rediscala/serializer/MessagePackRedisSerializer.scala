package org.hibernate.cache.rediscala.serializer

import org.msgpack.ScalaMessagePack._
import scala.reflect._

/**
 * MessagePackRedisSerializer
 * @author Sunghyouk Bae
 */
private[rediscala] class MessagePackRedisSerializer {

    def serialize[T: ClassTag](graph: T): Array[Byte] = {
        messagePack.register(classTag[T].runtimeClass.asInstanceOf[Class[T]])
        write(graph)
    }

    def deserialize[T: ClassTag](bytes: Array[Byte]): T = {
        messagePack.read(bytes, classTag[T].runtimeClass.asInstanceOf[Class[T]])
    }

    def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
        messagePack.read(bytes, clazz)
    }
}
