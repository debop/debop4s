package debop4s.data.mybatis.mapping

import debop4s.data.mybatis.session.Session

/**
 * A mapped SQL UPDATE statement.
 * Basically this defines a function: ( => Int)
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
abstract class Perform extends Statement with SQLFunction0[Int] {

  override def parameterTypeClass = classOf[Nothing]

  def apply()(implicit s: Session): Int =
    execute { s.update(fqi.id) }

}
