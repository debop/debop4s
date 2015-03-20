package debop4s.data.mybatis.mapping

import java.util

import org.apache.ibatis.`type`.{JdbcType => MBJdbcType}
import org.apache.ibatis.mapping.{ResultFlag => MBResultFlag}

import scala.collection.JavaConverters._
/**
 * ResultMapping
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
private[mybatis] class ResultMapping(resultType: T[_],
                                     property_ : String,
                                     column_ : String,
                                     javaType: T[_],
                                     jdbcType: JdbcType,
                                     nestedSelect_ : Select,
                                    nestedResultMap_ : ResultMap[_],
                                      notNullColumn_ : String,
                                      columnPrefix_ : String,
                                      typeHandler: T[_ <: TypeHandler[_]],
                                      flags_ : Seq[ResultFlag] = Seq()) {

  def resultTypeClass: Class[_] = resultType.unwrap
  def property: String = property_
  def column: String = column_
  def javaTypeClass: Class[_] = if(javaType == null || javaType.isVoid) null else javaType.unwrap
  def jdbcTypeEnum : MBJdbcType = if(jdbcType == null || jdbcType == JdbcType.UNDEFINED) null else jdbcType.unwrap
  def nestedSelect: Select = nestedSelect_
  def nestedResultMap: ResultMap[_] = nestedResultMap_
  def notNullColumn : String = notNullColumn_
  def typeHandlerClass: Class[_ <: TypeHandler[_]] = if (typeHandler == null) null else typeHandler.unwrap
  def flags: util.List[MBResultFlag] = flags_.map(_.unwrap).asJava
  def columnPrefix: String = columnPrefix_


}
