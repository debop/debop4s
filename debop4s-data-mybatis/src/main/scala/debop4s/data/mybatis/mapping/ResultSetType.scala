package debop4s.data.mybatis.mapping

import org.apache.ibatis.mapping.{ResultSetType => MBResultSetType}

/**
 * ResultSetType
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
sealed trait ResultSetType {

  val unwrap: MBResultSetType

}

/**
 * Wrapper of org.apache.ibatis.mapping.ResultSetType values
 */
object ResultSetType {
  val FORWARD_ONLY = new ResultSetType {
    override val unwrap: MBResultSetType = MBResultSetType.FORWARD_ONLY
  }
  val SCROLL_INSENSITIVE = new ResultSetType {
    override val unwrap: MBResultSetType = MBResultSetType.SCROLL_INSENSITIVE
  }
  val SCROLL_SENSITIVE = new ResultSetType {
    override val unwrap: MBResultSetType = MBResultSetType.SCROLL_SENSITIVE
  }
}
