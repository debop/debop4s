package debop4s.data.mybatis.config

import javax.sql.DataSource

/**
 * Environment
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
case class Environment(id:String, tf:TransactionFactory, ds:DataSource) {

  val unwrap = new org.apache.ibatis.mapping.Environment(id, tf, ds)

}
