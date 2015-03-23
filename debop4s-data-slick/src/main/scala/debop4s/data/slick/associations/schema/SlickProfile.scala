package debop4s.data.slick.associations.schema

import debop4s.data.slick.SlickContext

/**
 * SlickProfile
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickProfile {
  lazy val driver = SlickContext.driver
}


