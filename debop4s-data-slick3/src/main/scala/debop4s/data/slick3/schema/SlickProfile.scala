package debop4s.data.slick3.schema

import debop4s.data.slick3.SlickContext

/**
 * SlickProfile
 * @author sunghyouk.bae@gmail.com
 */
trait SlickProfile {

  lazy val driver = SlickContext.driver

}
