package debop4s.data.mybatis.session

import org.apache.ibatis.session.{TransactionIsolationLevel => TIL}

trait TransactionIsolationLevel {
  val unwrap: TIL
}
/**
 * TransactionIsolationLevel
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
object TransactionIsolationLevel {

  val NONE = new TransactionIsolationLevel {override val unwrap = TIL.NONE}
  val READ_COMMITTED = new TransactionIsolationLevel {override val unwrap: TIL = TIL.READ_COMMITTED}
  val READ_UNCOMMITTED = new TransactionIsolationLevel {override val unwrap: TIL = TIL.READ_UNCOMMITTED}
  val REPEATABLE_READ = new TransactionIsolationLevel {override val unwrap: TIL = TIL.REPEATABLE_READ}
  val SERIALIZABLE = new TransactionIsolationLevel {override val unwrap: TIL = TIL.SERIALIZABLE}
  val UNDEFINED = new TransactionIsolationLevel {override val unwrap: TIL = null}

}
