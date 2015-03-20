package debop4s.data.mybatis.mapping

import debop4s.data.mybatis.session.Session

/**
 * A mapped SQL UPDATE statement.
 * Basically this defines a function: (Param => Int)
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
abstract class Update[P: Manifest] extends Statement with SQLFunction1[P, Int] {

  override def parameterTypeClass = manifest[P].runtimeClass

  def apply(param: P)(implicit s: Session): Int = execute {
    s.update(fqi.id, param)
  }

}
