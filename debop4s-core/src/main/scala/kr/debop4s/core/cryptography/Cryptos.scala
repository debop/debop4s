package kr.debop4s.core.cryptography

import java.security.SecureRandom
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.cryptography.Cryptos
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:08
 */
object Cryptos {

  lazy val log = LoggerFactory.getLogger(getClass)

  val RANDOM_NUMBER_GENERATION = "SHA1PRNG"
  lazy val random: SecureRandom = SecureRandom.getInstance(RANDOM_NUMBER_GENERATION)

  def randomBytes(num: Int): Array[Byte] = {
    assert(num >= 0)
    val bytes = new Array[Byte](num)
    if (num > 0)
      random.nextBytes(bytes)
    bytes
  }
}
