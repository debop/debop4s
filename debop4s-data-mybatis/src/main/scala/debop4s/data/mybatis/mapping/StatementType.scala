package debop4s.data.mybatis.mapping

/**
 * Wrapper of `org.apache.ibatis.mapping.StatementType`
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
sealed trait StatementType {
  val unwrap: org.apache.ibatis.mapping.StatementType
}

object StatementType {
  val STATEMENT = new StatementType {
    override val unwrap = org.apache.ibatis.mapping.StatementType.STATEMENT
  }

  val PREPARED = new StatementType {
    override val unwrap = org.apache.ibatis.mapping.StatementType.PREPARED
  }

  val CALLABLE = new StatementType {
    override val unwrap = org.apache.ibatis.mapping.StatementType.CALLABLE
  }
}
