package debop4s.data.orm.hibernate.usertype.cryptography

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}

import debop4s.core.cryptography.{DESEncryptor, RC2Encryptor, SymmetricEncryptorSupport, TripleDESEncryptor}
import org.hibernate.`type`.StringType
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.slf4j.LoggerFactory

abstract class AbstractEncryptedStringUserType extends UserType {

  private lazy val log = LoggerFactory.getLogger(getClass)

  protected def encryptor: SymmetricEncryptorSupport

  def encrypt(plainText: String): String = encryptor.encrypt(plainText)

  def decrypt(cipherText: String): String = encryptor.decrypt(cipherText)

  override def sqlTypes(): Array[Int] = Array(StringType.INSTANCE.sqlType())

  override def returnedClass(): Class[_] = classOf[String]

  override def equals(x: Any, y: Any): Boolean = (x == y) || (x != null && (x == y))

  override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

  override def nullSafeGet(rs: ResultSet,
                           names: Array[String],
                           session: SessionImplementor,
                           owner: Any): AnyRef = {
    val cipherText = StringType.INSTANCE.nullSafeGet(rs, names(0), session)
    decrypt(cipherText)
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
    if (value == null) {
      StringType.INSTANCE.nullSafeSet(st, null, index, session)
    } else {
      StringType.INSTANCE.nullSafeSet(st, encrypt(value.toString), index, session)
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
class DESEncryptorUserType extends AbstractEncryptedStringUserType {
  override protected val encryptor = new DESEncryptor()
}

/**
 * RC2 알고리즘을 이용하여 문자열을 암호화 합니다.
 */
class RC2EncryptorUserType extends AbstractEncryptedStringUserType {
  override protected val encryptor = new RC2Encryptor()
}

/**
 * TripleDES 알고리즘을 이용하여 문자열을 암호화합니다.
 */
class TripleDESEncryptorUserType extends AbstractEncryptedStringUserType {
  override protected val encryptor = new TripleDESEncryptor()
}