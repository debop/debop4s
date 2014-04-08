package debop4s.data.hibernate.usertype.cryptography

import debop4s.core.cryptography.{TripleDESEncryptor, DESEncryptor, RC2Encryptor, SymmetricEncryptor}
import debop4s.core.utils.Strings
import java.io.Serializable
import java.sql.{ResultSet, PreparedStatement}
import org.hibernate.`type`.StringType
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

/**
 * debop4s.data.hibernate.usertype.cryptography.AbstractEncryptedStringUserType
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:57
 */
abstract class AbstractEncryptedStringUserType extends UserType {

    protected def encryptor: SymmetricEncryptor

    private def encrypt(plainText: String): String = {
        if (Strings.isEmpty(plainText)) {
            null
        } else {
            val bytes = encryptor.encrypt(Strings.getUtf8Bytes(plainText))
            Strings.getStringFromBytes(bytes)
        }
    }

    private def decrypt(cipherText: String): String = {
        if (Strings.isEmpty(cipherText)) {
            null
        } else {
            val bytes = encryptor.decrypt(Strings.getBytesFromString(cipherText))
            Strings.getUtf8String(bytes)
        }
    }

    override def sqlTypes(): Array[Int] = Array(StringType.INSTANCE.sqlType())

    override def returnedClass(): Class[_] = classOf[String]

    override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

    override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

    override def nullSafeGet(rs: ResultSet,
                             names: Array[String],
                             session: SessionImplementor,
                             owner: Any): AnyRef = {
        val str = StringType.INSTANCE.nullSafeGet(rs, names(0), session)
        decrypt(str)
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        if (value == null) {
            StringType.INSTANCE.nullSafeSet(st, null, index, session)
        } else {
            val str = encrypt(value.toString)
            StringType.INSTANCE.nullSafeSet(st, value, index, session)
        }
    }

    override def deepCopy(value: Any): AnyRef =
        value.asInstanceOf[AnyRef]

    override def isMutable: Boolean = true

    override def disassemble(value: Any): Serializable =
        deepCopy(value).asInstanceOf[Serializable]

    override def assemble(cached: Serializable, owner: Any): AnyRef =
        deepCopy(cached)

    override def replace(original: Any, target: Any, owner: Any): AnyRef =
        deepCopy(original)
}

/**
 * DES 알고리즘을 이용하여 문자열을 암호화 합니다.
 */
class DESStringUserType extends AbstractEncryptedStringUserType {

    private lazy val _encryptor = new DESEncryptor()

    override protected def encryptor: SymmetricEncryptor = _encryptor
}

/**
 * RC2 알고리즘을 이용하여 문자열을 암호화 합니다.
 */
class RC2StringUserType extends AbstractEncryptedStringUserType {

    private lazy val _encryptor = new RC2Encryptor()

    override protected def encryptor: SymmetricEncryptor = _encryptor
}

/**
 * TripleDES 알고리즘을 이용하여 문자열을 암호화합니다.
 */
class TripleDESStringUserType extends AbstractEncryptedStringUserType {

    private lazy val _encryptor = new TripleDESEncryptor()

    override protected def encryptor: SymmetricEncryptor = _encryptor
}