package debop4s.core.utils

/**
 * Scala Option 수형에 대한 Object class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:50
 */
@deprecated("use scala.Option object", since = "0.5.0")
object Options {

  /**
   * 인스턴스가 null이면 None을 반환하고, 값이 있으면 Some(v)를 반환합니다.
   */
  def toOption[T](v: T): Option[T] = v match {
    case null => None
    case None => None
    case x: Option[Any] => x.asInstanceOf[Option[T]]
    case _ => Some(v)
  }
}
