package com.github.debop4s.data.hibernate.usertype

import com.github.debop4s.core.json.{JsonTextObject, JsonSerializer}
import java.io.Serializable
import java.sql.{PreparedStatement, ResultSet}
import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import org.slf4j.LoggerFactory

/**
 * 객체 정보를 class name 과 json text 로 두 컬럼에 저장 / 로드해주는 UserType 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 4:51
 */
abstract class AbstractJsonUserType extends UserType {

    protected lazy val log = LoggerFactory.getLogger(getClass)

    def jsonSerializer: JsonSerializer

    private def serialize[T <: AnyRef](value: T): JsonTextObject = {
        if (value == null) JsonTextObject.Empty
        else JsonTextObject(value.getClass.getName, jsonSerializer.serializeToText(value))
    }

    private def deserialize(jto: JsonTextObject): AnyRef = {
        if (jto == null || jto == JsonTextObject.Empty) null
        else {
            try {
                val clazz = Class.forName(jto.className)
                jsonSerializer.deserializeFromText(jto.jsonText, clazz).asInstanceOf[AnyRef]
            } catch {
                case e: Throwable =>
                    log.error("JsonTextObject의 json text 를 deserialize 하는데 실패했습니다.", e)
                    null
            }
        }
    }

    override def sqlTypes(): Array[Int] = Array(StandardBasicTypes.STRING.sqlType(), StandardBasicTypes.STRING.sqlType())

    override def returnedClass(): Class[_] = classOf[JsonTextObject]

    override def hashCode(x: Any): Int = if (x != null) x.hashCode() else 0

    override def equals(x: Any, y: Any): Boolean =
        (x == y) || (x != null && (x == y))

    override def nullSafeGet(rs: ResultSet, names: Array[String], session: SessionImplementor, owner: Any): AnyRef = {
        val className = StandardBasicTypes.STRING.nullSafeGet(rs, names(0), session)
        val jsonText = StandardBasicTypes.STRING.nullSafeGet(rs, names(1), session)

        if (className != null && jsonText != null) {
            val jto = new JsonTextObject(className, jsonText)
            if (jto != null) deserialize(jto) else null
        } else {
            null
        }
    }

    override def nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SessionImplementor) {
        val jto = serialize(value.asInstanceOf[AnyRef])

        if (jto == null) {
            StandardBasicTypes.STRING.nullSafeSet(st, null, index, session)
            StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session)
        } else {
            StandardBasicTypes.STRING.nullSafeSet(st, jto.className, index, session)
            StandardBasicTypes.STRING.nullSafeSet(st, jto.jsonText, index + 1, session)
        }
    }

    override def deepCopy(value: Any): AnyRef =
        value.asInstanceOf[AnyRef]

    override def replace(original: Any, target: Any, owner: Any): AnyRef =
        deepCopy(original)

    override def assemble(cached: Serializable, owner: Any): AnyRef =
        deepCopy(cached)

    override def disassemble(value: Any): Serializable =
        deepCopy(value).asInstanceOf[Serializable]

    override def isMutable: Boolean = true
}
