package debop4s.core.cryptography

import java.security.SecureRandom

import org.slf4j.LoggerFactory

/**
 * 암호화 관련 Helper class 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:08
 */
object Cryptos {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val RANDOM_NUMBER_GENERATION = "SHA1PRNG"

  /**
   * 난수 발생기
   */
  lazy val random: SecureRandom = SecureRandom.getInstance(RANDOM_NUMBER_GENERATION)

  /**
   * 난수 데이터를 발생시킵니다.
   */
  def randomBytes(num: Int): Array[Byte] = {
    if (num <= 0)
      return Array[Byte]()

    val bytes = new Array[Byte](num)
    random.nextBytes(bytes)
    bytes
  }
}
