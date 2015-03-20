package debop4s.data.mybatis.mapping

import org.apache.ibatis.`type`.{JdbcType => MBJdbcType}

/**
 * JdbcType
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
sealed trait JdbcType {

  val unwrap: MBJdbcType
  override def toString = unwrap.name()

}

object JdbcType {
  val ARRAY = new JdbcType {val unwrap = MBJdbcType.ARRAY}
  val BIT = new JdbcType {val unwrap = MBJdbcType.BIT}
  val TINYINT = new JdbcType {val unwrap = MBJdbcType.TINYINT}
  val SMALLINT = new JdbcType {val unwrap = MBJdbcType.SMALLINT}
  val INTEGER = new JdbcType {val unwrap = MBJdbcType.INTEGER}
  val BIGINT = new JdbcType {val unwrap = MBJdbcType.BIGINT}
  val FLOAT = new JdbcType {val unwrap = MBJdbcType.FLOAT}
  val REAL = new JdbcType {val unwrap = MBJdbcType.REAL}
  val DOUBLE = new JdbcType {val unwrap = MBJdbcType.DOUBLE}
  val NUMERIC = new JdbcType {val unwrap = MBJdbcType.NUMERIC}
  val DECIMAL = new JdbcType {val unwrap = MBJdbcType.DECIMAL}
  val CHAR = new JdbcType {val unwrap = MBJdbcType.CHAR}
  val VARCHAR = new JdbcType {val unwrap = MBJdbcType.VARCHAR}
  val LONGVARCHAR = new JdbcType {val unwrap = MBJdbcType.LONGVARCHAR}
  val DATE  = new JdbcType {val unwrap = MBJdbcType.DATE}
  val TIME = new JdbcType {val unwrap = MBJdbcType.TIME}
  val TIMESTAMP = new JdbcType {val unwrap = MBJdbcType.TIMESTAMP}
  val BINARY = new JdbcType {val unwrap = MBJdbcType.BINARY}
  val VARBINARY = new JdbcType {val unwrap = MBJdbcType.VARBINARY}
  val LONGVARBINARY = new JdbcType {val unwrap = MBJdbcType.LONGVARBINARY}
  val NULL = new JdbcType {val unwrap = MBJdbcType.NULL}
  val OTHER = new JdbcType {val unwrap = MBJdbcType.OTHER}
  val BLOB = new JdbcType {val unwrap = MBJdbcType.BLOB}
  val CLOB = new JdbcType {val unwrap = MBJdbcType.CLOB}
  val BOOLEAN = new JdbcType {val unwrap = MBJdbcType.BOOLEAN}
  val CURSOR = new JdbcType {val unwrap = MBJdbcType.CURSOR}
  val UNDEFINED = new JdbcType {val unwrap = MBJdbcType.UNDEFINED}
  val NVARCHAR = new JdbcType {val unwrap = MBJdbcType.NVARCHAR}
  val NCHAR = new JdbcType {val unwrap = MBJdbcType.NCHAR}
  val NCLOB = new JdbcType {val unwrap = MBJdbcType.NCLOB}
  val STRUCT = new JdbcType {val unwrap = MBJdbcType.STRUCT}
}
