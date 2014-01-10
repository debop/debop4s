package kr.debop4s.core.cryptography

import org.jasypt.digest.StandardStringDigester
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.cryptography.StringDigester
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 11:08
 */
trait StringDigester {

    implicit lazy val log = LoggerFactory.getLogger(getClass)

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
        val matches = standardStringDigester.matches(message, digest)
        log.trace(s"문자열이 암호화된 문자열과 같은지 확인합니다. message=[$message], digest=[$digest], metch=[$matches]")
        matches
    }
}

abstract class AbstractStringDigester(iterations: Option[Int] = None) extends StringDigester {

    standardStringDigester.setAlgorithm(algorithm)
    standardStringDigester.setIterations(iterations.getOrElse(5))
}

class MD5StringDigester(iterations: Option[Int] = None) extends AbstractStringDigester(iterations) {

    /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
    def algorithm: String = "MD5"
}

class SHA1StringDigester(iterations: Option[Int] = None) extends AbstractStringDigester(iterations) {

    /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
    def algorithm: String = "SHA-1"
}

class SHA256StringDigester(iterations: Option[Int] = None) extends AbstractStringDigester(iterations) {

    /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
    def algorithm: String = "SHA-256"

}

class SHA384StringDigester(iterations: Option[Int] = None) extends AbstractStringDigester(iterations) {

    /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
    def algorithm: String = "SHA-384"

}

class SHA512StringDigester(iterations: Option[Int] = None) extends AbstractStringDigester(iterations) {

    /** Digester 알고리즘 ( MD5, SHA-1, SHA-256, SHA-384, SHA-512 ) */
    def algorithm: String = "SHA-512"

}
