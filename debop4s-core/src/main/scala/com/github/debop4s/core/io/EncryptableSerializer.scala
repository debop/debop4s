package com.github.debop4s.core.io

import com.github.debop4s.core.cryptography.{RC2Encryptor, SymmetricEncryptor}
import org.slf4j.LoggerFactory

/**
 * 암호화를 통해 직력화를 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  0.1
 */
class EncryptableSerializer(serializer: Serializer, val encryptor: SymmetricEncryptor = new RC2Encryptor())
    extends SerializerDecorator(serializer) {

    private lazy val log = LoggerFactory.getLogger(classOf[EncryptableSerializer])

    /**
     * 객체를 직렬화 합니다.
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 가진 바이트 배열
     */
    override def serialize[T](graph: T) =
        encryptor.encrypt(super.serialize(graph))

    /**
     * 직렬화된 바이트 배열을 역직렬화하여 객체로 변환합니다.
     * @param bytes 직렬화된 바이트 배열
     * @return 역직렬화된 객체 정보
     */
    override def deserialize[T](bytes: Array[Byte], clazz: Class[T]) =
        super.deserialize(encryptor.decrypt(bytes), clazz)
}
