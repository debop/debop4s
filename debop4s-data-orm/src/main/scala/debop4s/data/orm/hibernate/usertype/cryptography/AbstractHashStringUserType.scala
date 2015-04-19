package debop4s.data.orm.hibernate.usertype.cryptography

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Objects

import debop4s.core.cryptography._
import org.hibernate.`type`.{StandardBasicTypes, StringType}
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.slf4j.LoggerFactory

/**
 * Hash Algorithm 으로 암호화해서 저장합니다.
 * NOTE: 한번 암호화된 데이터는 복호화가 불가능하므로, 엔티티에 @DynamicUpdate 를 꼭 추가해 주어야 합니다.
 * @author sunghyouk.bae@gmail.com
 */
abstract class AbstractHashStringUserType extends UserType {

  private lazy val log = LoggerFactory.getLogger(getClass)

  protected def digester: StringDigesterSupport

  def digest(plainText: String): String = digester.digest(plainText)

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.STRING.sqlType())
  override def returnedClass(): Class[_] = classOf[String]
  override def equals(x: Any, y: Any): Boolean = Objects.equals(x, y)
  override def hashCode(x: Any): Int = Objects.hashCode(x)

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    StringType.INSTANCE.nullSafeGet(rs, names(0), session).asInstanceOf[String]
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    val digestedText = if (value == null) null else digest(value.toString)
    log.debug(s"save digest string. value=$value, digestedText=$digestedText")

    StringType.INSTANCE.nullSafeSet(st, digestedText, index, session)
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]
  override def isMutable: Boolean = true
  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)
  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)
  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]

}

class MD5StringUserType extends AbstractHashStringUserType {
  override protected val digester: StringDigesterSupport = new MD5StringDigester()
}

class SHA1StringUserType extends AbstractHashStringUserType {
  override protected val digester: StringDigesterSupport = new SHA1StringDigester()
}

class SHA256StringUserType extends AbstractHashStringUserType {
  override protected val digester: StringDigesterSupport = new SHA256StringDigester()
}

class SHA384StringUserType extends AbstractHashStringUserType {
  override protected val digester: StringDigesterSupport = new SHA384StringDigester()
}

class SHA512StringUserType extends AbstractHashStringUserType {
  override protected val digester: StringDigesterSupport = new SHA512StringDigester()
}
