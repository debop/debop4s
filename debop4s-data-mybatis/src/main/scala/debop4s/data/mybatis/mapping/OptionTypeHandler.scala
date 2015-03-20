package debop4s.data.mybatis.mapping

import java.sql.{CallableStatement, PreparedStatement, ResultSet}

import org.apache.ibatis.`type`.{JdbcType => MBJdbcType}

/**
 * Generic scala.Option[T] TypeHandler.
 * Wraps any TypeHandler[T] to support TypeHandler[Option[T]]
 * {{{
 *   class OptionIntegerTypeHandler extends OptionTypeHandler(new IntegerTypeHandler())
 * }}}
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
class OptionTypeHandler[T](delegate: TypeHandler[T]) extends TypeHandler[Option[T]] {

  override def setParameter(ps: PreparedStatement, i: Int, parameter: Option[T], jdbcType: MBJdbcType): Unit = {
    parameter match {
      case None => delegate.setParameter(ps, i, null.asInstanceOf[T], jdbcType)
      case Some(v) => delegate.setParameter(ps, i, v, jdbcType)
    }
  }
  override def getResult(rs: ResultSet, columnName: String): Option[T] = {
    delegate.getResult(rs, columnName) match {
      case null => None
      case v: T => Some(v)
    }
  }
  override def getResult(rs: ResultSet, columnIndex: Int): Option[T] = {
    delegate.getResult(rs, columnIndex) match {
      case null => None
      case v: T => Some(v)
    }
  }
  override def getResult(cs: CallableStatement, columnIndex: Int): Option[T] = {
    delegate.getResult(cs, columnIndex) match {
      case null => None
      case v: T => Some(v)
    }
  }

}

object TypeHandlers {

  import org.apache.ibatis.`type`._

  class OptionBooleanTypeHandler extends OptionTypeHandler(new BooleanTypeHandler())
  class OptionByteTypeHandler extends OptionTypeHandler(new ByteTypeHandler())
  class OptionShortTypeHandler extends OptionTypeHandler(new ShortTypeHandler())
  class OptionIntegerTypeHandler extends OptionTypeHandler(new IntegerTypeHandler())
  class OptionLongTypeHandler extends OptionTypeHandler(new LongTypeHandler())
  class OptionFloatTypeHandler extends OptionTypeHandler(new FloatTypeHandler())
  class OptionDoubleTypeHandler extends OptionTypeHandler(new DoubleTypeHandler())
  class OptionStringTypeHandler extends OptionTypeHandler(new StringTypeHandler())
  class OptionClobTypeHandler extends OptionTypeHandler(new ClobTypeHandler())
  class OptionBlobTypeHandler extends OptionTypeHandler(new BlobTypeHandler())
  class OptionNStringTypeHandler extends OptionTypeHandler(new NStringTypeHandler())
  class OptionNClobTypeHandler extends OptionTypeHandler(new NClobTypeHandler())
  class OptionBigDecimalTypeHandler extends OptionTypeHandler(new BigDecimalTypeHandler())
  class OptionDateOnlyTypeHandler extends OptionTypeHandler(new DateOnlyTypeHandler())
  class OptionTimeOnlyTypeHandler extends OptionTypeHandler(new TimeOnlyTypeHandler())
  class OptionDateTypeHandler extends OptionTypeHandler(new DateTypeHandler())

}
