package debop4s.data.slick.northwind.schema

import debop4s.data.slick.schema.SlickComponent

/**
 * Northwind Database Object
 * @author sunghyouk.bae@gmail.com
 */
object NorthwindDatabase extends SlickComponent with NorthwindTables with NorthwindViews
