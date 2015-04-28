package debop4s.data.slick3.northwind

import debop4s.data.slick3.northwind.schema._
import debop4s.data.slick3.schema.SlickComponent

/**
 * NorthwindDatabase
 * @author sunghyouk.bae@gmail.com
 */
object NorthwindDatabase
  extends SlickComponent
  with NorthwindTables
  with NorthwindViews
