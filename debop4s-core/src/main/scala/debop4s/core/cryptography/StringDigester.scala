package debop4s.core.cryptography

import org.jasypt.digest.StandardStringDigester
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * debop4s.core.cryptography.StringDigester
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:08
 */
trait StringDigester {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  val standardStringDigester = new StandardStringDigester()

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String

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
  def matches(message: String, digest: String): Boolean = {
    standardStringDigester.matches(message, digest)
  }
}

abstract class AbstractStringDigester(val iterations: Option[Int] = None) extends StringDigester {

  standardStringDigester.setAlgorithm(algorithm)
  standardStringDigester.setIterations(iterations.getOrElse(5))
}

/** MD5 String Digester */
@Component
class MD5StringDigester(private val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  def this() {
    this(Some(5))
  }

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "MD5"
}

/** SHA1 String Digester */
@Component
class SHA1StringDigester(private val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  def this() {
    this(Some(5))
  }

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-1"
}

@Component
class SHA256StringDigester(private val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  def this() {
    this(Some(5))
  }

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-256"

}

@Component
class SHA384StringDigester(private val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  def this() {
    this(Some(5))
  }

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-384"

}

@Component
class SHA512StringDigester(private val _iterations: Option[Int] = None)
  extends AbstractStringDigester(_iterations) {

  def this() {
    this(Some(5))
  }

  /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
  def algorithm: String = "SHA-512"

}
