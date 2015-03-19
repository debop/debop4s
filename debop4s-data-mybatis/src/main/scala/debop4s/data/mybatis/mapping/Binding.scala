package debop4s.data.mybatis.mapping

import org.apache.ibatis.`type`.JdbcType

/**
 * Binding
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
object Binding {

  /** Custom Aliased Types */
  private val valueTypes = Set("byte", "long", "short", "int", "double", "float", "boolean",
                                "byte[]", "long[]", "short[]", "int[]", "double[]", "float[]", "boolean[]")

  private def translate(cls: Class[_]): String = {
    if (valueTypes contains cls.getSimpleName) "_" + cls.getSimpleName
    else cls.getName
  }

  def ?[JavaType: Manifest](property: String,
                            jdbcType: JdbcType = null,
                            jdbcTypeName: String = null,
                            numericScale: Int = 0,
                            mode: ParamModeEnum = ModeIn,
                            typeHandler: T[_ <: TypeHandler[_]] = null,
                            resultMap: ResultMap[_] = null): String = {

  }

}
