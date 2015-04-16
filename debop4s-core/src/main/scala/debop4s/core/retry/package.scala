package debop4s.core

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * 재시도를 수행할 수 있도록 하는 package 입니다.
 * @author sunghyouk.bae@gmail.com
 */
package object retry {

  val DEFAULT_DELAY: FiniteDuration = Duration(500, TimeUnit.MILLISECONDS)

}
