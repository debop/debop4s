package debop4s.data.mybatis.mapping


sealed class ParamModeEnum(val v: String) {
  override def toString: String = v
}

case object ModeIN extends ParamModeEnum("IN")
case object ModeOUT extends ParamModeEnum("OUT")
case object ModeINOUT extends ParamModeEnum("INOUT")

/**
 * Provides compile time checked inline parameter bindings.
 * == Syntax ==
 * General notation
 * {{{
 * {?[T](propertyName, jdbcType=DBT, typeHandler=T[TH], mode=MD, numericScale=NS, resultMap=RM)}
 * }}}
 * Where
 * {{{
 *  - T                : JavaType      : Type of the parameter property, Optional
 *  - propertyName     : String        : Name of the parameter property, Required
 *  - DBT              : JdbcType      : Any value of org.mybatis.scala.mapping.JdbcType, Optional
 *  - TH               : TypeHandler   : A TypeHandler implementation class, Optional
 *  - MD               : ParamModeEnum : Any of (ModeIN, ModeOUT, ModeINOUT), Optional
 *  - NS               : Int           : Numeric Scale, Optional
 *  - RM               : ResultMap     : A ResultMap Object, Optional
 * }}}
 *
 * Simplified Notation
 * If you need to specify only the property name, you ca use this simplified notation:
 * {{{
 * {propertyName?}
 * }}}
 *
 * == Code Examples ==
 * {{{
 * <xsql>
 * SELECT * FROM person
 * WHERE name = {"name"?}
 * </xsql>
 * }}}
 *
 * {{{
 * <xsql>
 * SELECT * FROM task
 * WHERE due_date = {?("date", typeHandler=T[JodaTimeTypeHandler])}
 * </xsql>
 * }}}
 *
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
                            mode: ParamModeEnum = ModeIN,
                            typeHandler: T[_ <: TypeHandler[_]] = null,
                            resultMap: ResultMap[_] = null): String = {

    val sb = new StringBuilder()
    sb.append(property).append("\n")
    if (jdbcType != null) sb.append(s"jdbcType=${jdbcType.toString}").append("\n")
    if (jdbcTypeName != null) sb.append(s"jdbcTypename=$jdbcTypeName").append("\n")
    if (numericScale != 0) sb.append(s"numericScale=$numericScale").append("\n")
    if (mode != ModeIN) sb.append(s"mode=${mode.toString}").append("\n")
    if (typeHandler != null) sb.append(s"typeHandler=${typeHandler.unwrap.getName}").append("\n")
    if (resultMap != null) sb.append(s"resultMap=${resultMap.fqi.id}").append("\n")

    val jt = manifest[JavaType].runtimeClass
    if (jt != classOf[Nothing]) sb.append(s"javaType=${translate(jt)}").append("\n")

    sb.lines.mkString("#{", ",", "}")
    sb.toString()
  }

  /**
   * Utility class for simplified syntax support
   */
  case class Param(property: String) {
    def ? = Binding ? ( property )
  }

  /**
   * Implicit conversions for simplified syntax support
   */
  implicit def StringToParam(s: String): Param = new Param(s)

}
