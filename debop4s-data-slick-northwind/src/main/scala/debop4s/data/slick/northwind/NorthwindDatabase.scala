package debop4s.data.slick.northwind

import debop4s.data.slick.northwind.schema.{NorthwindTables, NorthwindViews}
import debop4s.data.slick.schema.SlickComponent

/**
 * Northwind Database Object
 * @author sunghyouk.bae@gmail.com
 */
object NorthwindDatabase extends SlickComponent with NorthwindTables with NorthwindViews
