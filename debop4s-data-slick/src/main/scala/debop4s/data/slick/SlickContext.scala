package debop4s.data.slick

import org.slf4j.LoggerFactory

/**
 * Slick 사용 시 환경설정 정보를 이용하여 Database와 Driver를 사용할 수 있도록 합니다.
 * {{{
 *
 * }}}
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
object SlickContext {

  lazy val LOG = LoggerFactory.getLogger(getClass)
}
