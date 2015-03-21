package debop4s.config.base

import debop4s.config._

/**
 * 서버 주소 (IP, Port) 의 설정 정보
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait ServerAddressElementSupport extends ConfigElementSupport {

  /** server host */
  val host: String = config.tryGetString("host", "localhost")

  /** server port */
  val port: Int = config.getInt("port")
}
