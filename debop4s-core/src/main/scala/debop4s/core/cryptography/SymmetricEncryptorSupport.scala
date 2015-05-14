package debop4s.core.cryptography

import debop4s.core.BinaryStringFormat
import debop4s.core.utils.Strings
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor
import org.jasypt.salt.ZeroSaltGenerator
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

/**
 * 대칭형 암호화 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 3:22
 */
trait SymmetricEncryptorSupport {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  private[this] val DEFAULT_PASSWORD: String = "debop@hconnect.co.kr-21011"
  private[this] var password: String = DEFAULT_PASSWORD

  /**
   * 대칭형 암호화 알고리즘
   * @return 대칭형 암호화 알고리즘
   */
  def algorithm: String

  /** 암호화 객체 */
  protected lazy val encryptor: StandardPBEByteEncryptor = {
    val encryptor = new StandardPBEByteEncryptor()

    encryptor.setSaltGenerator(new ZeroSaltGenerator())
    encryptor.setPassword(password)
    encryptor.setAlgorithm(algorithm)

    encryptor
  }

  private lazy val encryptorWithoutSalt: StandardPBEByteEncryptor = {
    val encryptor = new StandardPBEByteEncryptor()

    encryptor.setPassword(password)
    encryptor.setAlgorithm(algorithm)

    encryptor
  }

  /** 초기화 여부 */
  def isInitialized: Boolean = encryptor.isInitialized

  /**
   * 비밀번호 지정
   *
   * @param password 비밀번호
   */
  def setPassword(password: String): Unit = {
    this.password = password
  }

  /**
   * 데이터를 암호화합니다.
   *
   * @param input 암호화할 데이터
   * @return 암호화된 데이터
   */
  def encrypt(input: Array[Byte]): Array[Byte] = {
    if (input == null || input.length == 0)
      return Array.emptyByteArray

    Try {
      encryptor.encrypt(input)
    } match {
      case Success(x) => x
      case Failure(ex) =>
        log.warn(s"암호화에 실패했습니다. 지정된 값을 반환합니다.", ex)
        input
    }
  }

  /**
   * 문자열을 암호화 합니다.
   * @param plainText  암호화할 문자열
   * @return 암호화된 문자열
   */
  def encrypt(plainText: String): String = {
    if (Strings.isEmpty(plainText))
      return ""

    val cipher = encrypt(Strings.getUtf8Bytes(plainText))
    Strings.getStringFromBytes(cipher, BinaryStringFormat.HexDecimal)
  }

  /**
   * 암호화된 데이터를 복원합니다.
   *
   * @param input 암호화된 정보
   * @return 복원된 데이터
   */
  def decrypt(input: Array[Byte]): Array[Byte] = {
    if (input == null || input.length == 0)
      return Array.emptyByteArray

    Try {
      encryptor.decrypt(input)
    } match {
      case Success(x) => x
      case Failure(e) =>
        log.debug(s"복원에 실패했습니다. salt 값 없이 복원을 시도합니다.")
        Try {
          encryptorWithoutSalt.decrypt(input)
        } match {
          case Success(x) => x
          case Failure(es) =>
            log.warn(s"복원에 실패했습니다. algorithm=$algorithm, input=$input", es)
            input
        }
    }
  }

  /**
   * 암호화된 문자열을 복원합나다.
   * @param cipherText 암호화된 문자열
   * @return 복원된 데이터
   */
  def decrypt(cipherText: String): String = {
    if (Strings.isEmpty(cipherText))
      return ""

    val plainBytes = Strings.getBytesFromString(cipherText, BinaryStringFormat.HexDecimal)
    Strings.getUtf8String(decrypt(plainBytes))
  }
}

abstract class AbstractSymmetricEncryptor extends SymmetricEncryptorSupport {}

/**
 * RC2 대칭형 알고리즘을 사용한 암호화 클래스
 */
class RC2Encryptor extends AbstractSymmetricEncryptor {
  override val algorithm: String = "PBEwithSHA1andRC2_40"
}

/**
 * DES 대칭형 알고리즘을 사용한 암호화 클래스
 */
class DESEncryptor extends AbstractSymmetricEncryptor {
  override val algorithm: String = "PBEwithMD5andDES"
}

/**
 * TripleDES 알고리즘을 사용하는 암호기
 */
class TripleDESEncryptor extends AbstractSymmetricEncryptor {
  override val algorithm: String = "PBEwithSHA1andDESEDE"
}