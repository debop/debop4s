package debop4s.data.mybatis.session

/**
 * Wrapper of `org.apache.ibatis.session.RowBounds`
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
case class RowBounds(offset: Int, limit: Int) {
  val unwrap = new org.apache.ibatis.session.RowBounds(offset, limit)
}
