package debop4s.config.base

/**
 * 로그인 정보를 나타내는 환경 설정 요소
 * {{{
 *   username = "xxx"
 *   password = "ppp"
 * }}}
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait CredentialElementSupport extends ConfigElementSupport {

  /** Username */
  val username: String = getString("username")

  /** Password */
  val password: String = getString("password")

}
