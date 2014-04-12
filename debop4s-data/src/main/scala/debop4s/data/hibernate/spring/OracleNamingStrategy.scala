package debop4s.data.hibernate.spring

import org.hibernate.cfg.ImprovedNamingStrategy

/**
 * debop4s.data.hibernate.spring.OracleNamingStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:53
 */
class OracleNamingStrategy extends ImprovedNamingStrategy {

  override def classToTableName(className: String): String =
    super.classToTableName(className).toUpperCase

  override def propertyToColumnName(propertyName: String): String =
    super.propertyToColumnName(propertyName).toUpperCase

  override def tableName(tableName: String): String =
    super.tableName(tableName).toUpperCase

  override def columnName(columnName: String): String =
    super.columnName(columnName).toUpperCase
}
