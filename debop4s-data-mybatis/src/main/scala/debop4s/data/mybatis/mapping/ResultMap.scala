package debop4s.data.mybatis.mapping

import scala.collection.mutable

sealed trait AutoMappingBehavior {
  val value: java.lang.Boolean
}

object AutoMappingEnabled extends AutoMappingBehavior {override val value = java.lang.Boolean.TRUE}
object AutoMappingDisabled extends AutoMappingBehavior {override val value = java.lang.Boolean.FALSE}
object AutoMappingInherited extends AutoMappingBehavior {override val value = null}

/**
 * Defines a mapping between JDBC Results and Java/Scala Classes.
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
class ResultMap[R: Manifest](val parent: ResultMap[_] = null) {

  private[mybatis] val mappings = new mutable.ArrayBuffer[ResultMapping]
  private[mybatis] val constructor = new mutable.ArrayBuffer[ResultMapping]
  private[mybatis] var discr: (String, T[_], JdbcType, T[_ <: TypeHandler[_]], Seq[Case]) = _

  var fqi: FQI = _
  var autoMapping: AutoMappingBehavior = AutoMappingInherited

  def resultTypeClass = manifest[R].runtimeClass

  /**
   * A single result mapping between a column and a property or field.
   * This property will be used for comparisons.
   * @param property Name of the property
   * @param column name of the column
   * @param javaType Type of the property
   * @param jdbcType Type of the column
   * @param typeHandler Type of the handler
   */
  def id(property: String,
         column: String,
         javaType: T[_] = null,
         jdbcType: JdbcType = JdbcType.UNDEFINED,
         typeHandler: T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(T[R],
                                   property,
                                   column,
                                   javaType,
                                   jdbcType,
                                   null,
                                   null,
                                   null,
                                   null,
                                   typeHandler,
                                   Seq(ResultFlag.ID))
  }

  /**
   * A single result mapping between a column and a property or field.
   * @param property Name of the property
   * @param column name of the column
   * @param javaType Type of the property
   * @param jdbcType Type of the column
   * @param typeHandler Type of the handler
   */
  def result(property: String,
             column: String,
             javaType: T[_] = null,
             jdbcType: JdbcType = JdbcType.UNDEFINED,
             typeHandler: T[_ <: TypeHandler[_]] = null) = {

    mappings += new ResultMapping(T[R],
                                   property,
                                   column,
                                   javaType,
                                   jdbcType,
                                   null,
                                   null,
                                   null,
                                   null,
                                   typeHandler,
                                   Seq())
  }

  /**
   * A single constructor argument that is part of a ConstructorArgs collection.
   * This arg will be used for comparisons.
   * @param column name of the column
   * @param javaType Type of the property
   * @param jdbcType Type of the column
   * @param typeHandler Type of the handler
   */
  def idArg(column: String = null,
            javaType: T[_] = null,
            jdbcType: JdbcType = JdbcType.UNDEFINED,
            typeHandler: T[_ <: TypeHandler[_]] = null): Unit = {

    constructor += new ResultMapping(T[R],
                                      null,
                                      column,
                                      javaType,
                                      jdbcType,
                                      null,
                                      null,
                                      null,
                                      null,
                                      typeHandler,
                                      Seq(ResultFlag.CONSTRUCTOR, ResultFlag.ID))
  }

  /** A single constructor argument that is part of a ConstructorArgs collection.
    * @param column name of the column
    * @param javaType Type of the property
    * @param jdbcType Type of the column
    * @param typeHandler Type of the handler
    * @param select Reference to an external select which will be called to obtain this value.
    * @param resultMap Reference to an external resultMap which handles this value.
    */
  def arg(column: String = null,
          javaType: T[_] = null,
          jdbcType: JdbcType = JdbcType.UNDEFINED,
          select: Select = null,
          resultMap: ResultMap[_] = null,
          typeHandler: T[_ <: TypeHandler[_]] = null): Unit = {

    constructor += new ResultMapping(T[R],
                                      null,
                                      column,
                                      javaType,
                                      jdbcType,
                                      select,
                                      resultMap,
                                      null,
                                      null,
                                      typeHandler,
                                      Seq(ResultFlag.CONSTRUCTOR))
  }

  /** The association element deals with a “has-one” type relationship.
    * An association mapping works mostly like any other result.
    * You specify the target property, the column to retrieve the value from, the javaType
    * of the property (which MyBatis can figure out most of the time), the jdbcType if necessary
    * and a typeHandler if you want to override the retrieval of the result values.
    * @param property Name of the property
    * @param column name of the column
    * @param javaType Type of the property
    * @param jdbcType Type of the column
    * @param typeHandler Type of the handler
    * @param select Reference to an external select which will be called to obtain this value.
    * @param resultMap Reference to an external resultMap which handles this value.
    * @param notNullColumn Name of the column to be checked to avoid loading of empty objects.
    * @tparam Type type of the associated object
    */
  def association[Type: Manifest](property: String = null,
                                  column: String = null,
                                  jdbcType: JdbcType = JdbcType.UNDEFINED,
                                  select: Select = null,
                                  resultMap: ResultMap[_] = null,
                                  notNullColumn: String = null,
                                  columnPrefix: String = null,
                                  typeHandler: T[_ <: TypeHandler[_]] = null): Unit = {

    constructor += new ResultMapping(T[R],
                                      property,
                                      column,
                                      T[Type],
                                      jdbcType,
                                      select,
                                      resultMap,
                                      notNullColumn,
                                      columnPrefix,
                                      typeHandler,
                                      Seq(ResultFlag.CONSTRUCTOR))
  }

  /** The collection element deals with a “has-many” type relationship.
    * A collection mapping works mostly like any other result.
    * You specify the target property, the column to retrieve the value from, the javaType
    * of the property (which MyBatis can figure out most of the time), the jdbcType if necessary
    * and a typeHandler if you want to override the retrieval of the result values.
    * The collection element works almost identically to the association but for One-To_Many relationships.
    * @param column name of the column
    * @param javaType Type of the property
    * @param jdbcType Type of the column
    * @param typeHandler Type of the handler
    * @param select Reference to an external select which will be called to obtain this value.
    * @param resultMap Reference to an external resultMap which handles this value.
    * @param notNullColumn Name of the column to be checked to avoid loading of empty objects.
    * @tparam Type type of the associated objects
    */
  def collection[Type: Manifest](property: String = null,
                                  column: String = null,
                                  jdbcType: JdbcType = JdbcType.UNDEFINED,
                                  select: Select = null,
                                  resultMap: ResultMap[_] = null,
                                  notNullColumn: String = null,
                                  columnPrefix: String = null,
                                  typeHandler: T[_ <: TypeHandler[_]] = null): Unit = {

    constructor += new ResultMapping(T[R],
                                      property,
                                      column,
                                      null  /* Let's mybatis infer this */,
                                      jdbcType,
                                      select,
                                      resultMap,
                                      notNullColumn,
                                      columnPrefix,
                                      typeHandler,
                                      Seq())
  }

  /** Sometimes a single database query might return result sets of many different (but hopefully somewhat related) data types.
    * The discriminator element was designed to deal with this situation, and others, including class inheritance hierarchies.
    * The discriminator is pretty simple to understand, as it behaves much like a switch statement.
    * A discriminator definition specifies column and javaType attributes.
    * The column is where MyBatis will look for the value to compare.
    * The javaType is required to ensure the proper kind of equality test is performed (although String would probably
    * work for almost any situation).
    * @param column where MyBatis will look for the value to compare.
    * @param javaType required to ensure the proper kind of equality test is performed
    * @param jdbcType Type of the column
    * @param typeHandler Type of the handler
    * @param cases A Collection of cases to be matched in order to select an appropiate ResultMap
    */
  def discriminator(column:String = null,
                     javaType: T[_] = null,
                     jdbcType: JdbcType = JdbcType.UNDEFINED,
                     typeHandler: T[_ <: TypeHandler[_]] = null,
                     cases: Seq[Case] = Seq()): Unit = {
    discr = (column, javaType, jdbcType, typeHandler, cases)
  }
}
