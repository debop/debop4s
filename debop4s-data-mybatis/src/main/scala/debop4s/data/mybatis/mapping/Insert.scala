package debop4s.data.mybatis.mapping

import debop4s.data.mybatis.session.Session

/**
 * A mapped SQL INSERT statement.
 * Basically this defines a function: (Param => Int)
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
abstract class Insert[P: Manifest] extends Statement with SQLFunction1[P, Int] {

  /**
   * Key Generator used to retrieve database generated keys.
   * Default is null
   */
  var keyGenerator: KeyGenerator = null //JdbcGeneratedKey(null, "id")

  override def parameterTypeClass = manifest[P].runtimeClass

  override def apply(param: P)(implicit s: Session): Int = execute {
    s.insert(fqi.id, param)
  }
}
