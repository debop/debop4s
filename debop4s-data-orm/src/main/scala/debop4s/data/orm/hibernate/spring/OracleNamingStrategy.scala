package debop4s.data.orm.hibernate.spring

import org.hibernate.cfg.ImprovedNamingStrategy

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
