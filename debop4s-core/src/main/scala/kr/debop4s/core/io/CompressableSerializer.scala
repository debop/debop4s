package kr.debop4s.core.io

import kr.debop4s.core.compress.{GZipCompressor, Compressor}
import kr.debop4s.core.logging.Logger

/**
 * 객체를 직렬화하면서 압축하고, 압축된 정보를 역직렬화 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 5:48
 */
class CompressableSerializer(serializer: Serializer, val compressor: Compressor)
    extends SerializerDecorator(serializer) {

    val log = Logger[CompressableSerializer]

    def this(serializer: Serializer) {
        this(serializer, new GZipCompressor())
    }

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    override def serialize[T <: AnyRef](graph: T): Array[Byte] =
        compressor.compress(super.serialize(graph))

    /**
    * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
    * @param bytes 직렬화된 바이트 배열
    * @return 역직렬화된 객체 정보
    */
    override def deserialize[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]): T =
        super.deserialize(compressor.decompress(bytes), clazz)
}
