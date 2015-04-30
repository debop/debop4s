package debop4s.core.cryptography

import org.jasypt.digest.StandardStringDigester
import org.jasypt.salt.ZeroSaltGenerator

/**
 * 문자열을 Hash 알고리즘으로 암호화를 수행을 지원하는 Trait 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:08
 */
trait StringDigesterSupport {

  protected def iterations: Option[Int] = None

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String

  lazy val standardStringDigester: StandardStringDigester = {
    val digester = new StandardStringDigester()

    digester.setAlgorithm(algorithm)
    digester.setIterations(iterations.getOrElse(5))
    digester.setSaltGenerator(new ZeroSaltGenerator)
    digester
  }

  /** Digester 가 초기화 되었는지 여부, 초기화 된 상태에서는 속성을 변경 못합니다. */
  def isInitialized: Boolean = standardStringDigester.isInitialized

  /** 메시지를 암호화 합니다. */
  def digest(message: String): String = standardStringDigester.digest(message)

  /**
   * 지장한 메시지가 암호화된 내용과 일치하는지 확인합니다.
   *
   * @param message 일반 메시지
   * @param digest  암호화된 메시지
   * @return 메시지 일치 여부
   */
  def matches(message: String, digest: String): Boolean =
    standardStringDigester.matches(message, digest)
}

abstract class AbstractStringDigester(private[this] val _iterations: Option[Int] = Some(5))
  extends StringDigesterSupport {

  override protected def iterations = _iterations
}

/** MD5 String Digester */
class MD5StringDigester(private[this] val _iterations: Option[Int] = Some(5))
  extends AbstractStringDigester(_iterations) {

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "MD5"
}

/** SHA1 String Digester */
class SHA1StringDigester(private[this] val _iterations: Option[Int] = Some(5))
  extends AbstractStringDigester(_iterations) {

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-1"
}

/** SHA256 String Digester */
class SHA256StringDigester(private[this] val _iterations: Option[Int] = Some(5))
  extends AbstractStringDigester(_iterations) {

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-256"
}

/** SHA384 String Digester */
class SHA384StringDigester(private[this] val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-384"
}

/** SHA512 String Digester */
class SHA512StringDigester(private[this] val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-512"

}
