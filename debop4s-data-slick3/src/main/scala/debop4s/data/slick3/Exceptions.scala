package debop4s.data.slick3

import debop4s.data.slick3.model.Identifiable

class ActiveSlickException(msg: String) extends RuntimeException(msg)

object NoRowsAffectedException extends ActiveSlickException("No rows affected")

class RowNotFoundException[@miniboxed T](notFoundRecord: T) extends ActiveSlickException(s"Row not found: $notFoundRecord")

case class StaleObjectStateException[T <: Identifiable](staleObject: T, current: T)
  extends ActiveSlickException(s"Optimistic locking error - object in stale state: $staleObject, current in DB $current")

class TooManyRowsAffectedException(affectedRowCount: Int, expectedRowCount: Int)
  extends ActiveSlickException(s"Expected $expectedRowCount rows(s) affected, get $affectedRowCount instead.")