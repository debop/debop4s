package com.github.debop4s.core.io

import com.github.debop4s.core.cryptography.{RC2Encryptor, SymmetricEncryptor}
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.io.EncryptableSerializer
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 10:45
 */
class EncryptableSerializer(serializer: Serializer, val encryptor: SymmetricEncryptor = new RC2Encryptor())
    extends SerializerDecorator(serializer) {

    lazy val log = LoggerFactory.getLogger(classOf[EncryptableSerializer])

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    override def serialize[T <: AnyRef](graph: T) =
        encryptor.encrypt(super.serialize(graph))

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    override def deserialize[T <: AnyRef](bytes: Array[Byte], clazz: Class[T]) =
        super.deserialize(encryptor.decrypt(bytes), clazz)
}
