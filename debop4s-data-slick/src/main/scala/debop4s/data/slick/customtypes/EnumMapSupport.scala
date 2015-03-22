package debop4s.data.slick.customtypes

import debop4s.data.slick.SlickContext.driver.simple._

/**
 * EnumMapStringSupport
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait EnumMapStringSupport {
  this: Enumeration =>

  implicit val enum2StringMapper =
    MappedColumnType.base[Value, String](
                                          x => x.toString,
                                          s => if (s != null) this.withName(s) else null
                                        )
}

trait EnumMapIntSupport {
  this: Enumeration =>

  implicit val enum2IntMapper = MappedColumnType.base[Value, Int](_.id, this.apply)

}
