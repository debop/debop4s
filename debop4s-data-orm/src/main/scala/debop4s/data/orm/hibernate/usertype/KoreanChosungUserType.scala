package debop4s.data.orm.hibernate.usertype

import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import java.util.Objects

import debop4s.core.korean.KoreanString
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType

import scala.collection.JavaConverters._

/**
 * 한글 문자열일 경우 문자의 초성만을 추출하여 따로 저장하도록 합니다.
 *
 * {{{
 *  `@Column`(columns= Array(new Column(name="name"), new Column(name="nameChosung")))
 *  `@org.hibernate.annotation.Type`(`type`="debop4s.data.orm.hibernate.usertype.KoreanChosungUserType")
 *  var name : String = _
 * }}}
 * @author sunghyouk.bae@gmail.com
 */
class KoreanChosungUserType extends UserType {

  override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.STRING.sqlType(), StandardBasicTypes.STRING.sqlType())
  override def returnedClass(): Class[_] = classOf[String]
  override def equals(x: Any, y: Any): Boolean = Objects.equals(x, y)
  override def hashCode(x: Any): Int = Objects.hashCode(x)

  override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
    StandardBasicTypes.STRING.nullSafeGet(rs, names(0), session, owner).asInstanceOf[String]
  }

  override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor): Unit = {
    if (value == null) {
      StandardBasicTypes.STRING.nullSafeSet(st, null, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
    } else {
      val text = value.asInstanceOf[String]
      val chosung = KoreanString.getChosung(text).asScala.mkString
      StandardBasicTypes.STRING.nullSafeSet(st, text, index, session)
      StandardBasicTypes.STRING.nullSafeSet(st, chosung, index + 1, session)
    }
  }

  override def deepCopy(value: Any): AnyRef = value.asInstanceOf[AnyRef]
  override def isMutable: Boolean = true
  override def disassemble(value: Any): Serializable = deepCopy(value).asInstanceOf[Serializable]
  override def replace(original: Any, target: Any, owner: Any): AnyRef = deepCopy(original)
  override def assemble(cached: Serializable, owner: Any): AnyRef = deepCopy(cached)
}
