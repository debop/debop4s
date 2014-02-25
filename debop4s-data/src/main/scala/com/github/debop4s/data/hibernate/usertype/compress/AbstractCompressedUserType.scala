package com.github.debop4s.data.hibernate.usertype.compress

import com.github.debop4s.core.compress.Compressor
import com.github.debop4s.core.utils.{Arrays, Strings}
import java.io.Serializable
import java.sql.{ResultSet, PreparedStatement}
import org.hibernate.`type`.BinaryType
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

/**
 * 속성 정보를 압축하여 저장할 수 있도록 하는 UserType 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 5:58
 */
abstract class AbstractCompressedUserType extends UserType {

    def compressor: Compressor

    override def sqlTypes(): Array[Int] = Array(BinaryType.INSTANCE.sqlType())

    override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

    override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

    override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]

    override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]

    override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)

    override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)

    override def isMutable: Boolean = true
}

/**
 * 정보를 압축하여 바이트 배열로 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 5:28
 */
abstract class AbstractCompressedBinaryUserType extends AbstractCompressedUserType {

    protected def compress(plainBytes: Array[Byte]): Array[Byte] =
        compressor.compress(plainBytes)

    protected def decompress(compressedBytes: Array[Byte]): Array[Byte] =
        compressor.decompress(compressedBytes)

    override def returnedClass(): Class[_] = classOf[Array[Byte]]

    override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
        val compressedBytes = BinaryType.INSTANCE.nullSafeGet(rs, names(0), session)
        decompress(compressedBytes)
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        if (value == null) {
            BinaryType.INSTANCE.nullSafeSet(st, null, index, session)
        } else {
            val compressedBytes = compress(value.asInstanceOf[Array[Byte]])
            BinaryType.INSTANCE.nullSafeSet(st, compressedBytes, index, session)
        }
    }
}

/**
 * 문자열을 압축하여, Byte 배열로 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 5:28
 */
abstract class AbstractCompressedStringUserType extends AbstractCompressedUserType {

    protected def compress(str: String): Array[Byte] = {
        if (Strings.isEmpty(str)) null
        else compressor.compress(Strings.getUtf8Bytes(str))
    }

    protected def decompress(compressedBytes: Array[Byte]): String = {
        if (Arrays.isEmpty(compressedBytes)) null
        else Strings.getUtf8String(compressor.decompress(compressedBytes))
    }

    override def returnedClass(): Class[_] = classOf[String]

    override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
        val compressedBytes = BinaryType.INSTANCE.nullSafeGet(rs, names(0), session)
        decompress(compressedBytes)
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        if (value == null) {
            BinaryType.INSTANCE.nullSafeSet(st, null, index, session)
        } else {
            val compressedBytes = compress(value.asInstanceOf[String])
            BinaryType.INSTANCE.nullSafeSet(st, compressedBytes, index, session)
        }
    }
}

