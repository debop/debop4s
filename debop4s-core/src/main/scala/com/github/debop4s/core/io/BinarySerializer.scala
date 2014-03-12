package com.github.debop4s.core.io

import com.github.debop4s.core.utils.Arrays
import java.io._

/**
 * Binary Serializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 5:37
 */
class BinarySerializer extends Serializer {

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    @inline
    def serialize[T](graph: T): Array[Byte] = {
        if (graph == null)
            return Array.emptyByteArray

        val bos = new ByteArrayOutputStream()
        val oos = new ObjectOutputStream(bos)
        try {
            oos.writeObject(graph)
            oos.flush()

            bos.toByteArray
        } finally {
            oos.close()
            bos.close()
        }
    }

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    @inline
    def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
        if (Arrays.isEmpty(bytes))
            return null.asInstanceOf[T]

        val bis = new ByteArrayInputStream(bytes)
        try {
            val ois = new ObjectInputStream(bis)

            ois.readObject().asInstanceOf[T]
        } finally {
            bis.close()
        }
    }
}
