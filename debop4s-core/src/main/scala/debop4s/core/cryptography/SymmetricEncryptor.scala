package debop4s.core.cryptography

import debop4s.core.BinaryStringFormat
import debop4s.core.utils.Strings
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 대칭형 암호화 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 3:22
 */
trait SymmetricEncryptor {

    protected lazy val log = LoggerFactory.getLogger(getClass)

    private val DEFAULT_PASSWORD = "sunghyouk.bae@gmail.com-21011"

    protected var byteEncryptor = new StandardPBEByteEncryptor()
    byteEncryptor.setAlgorithm(algorithm)
    byteEncryptor.setPassword(DEFAULT_PASSWORD)

    /**
     * 대칭형 암호화 알고리즘
     *
     * @return 대칭형 암호화 알고리즘
     */
    def algorithm: String

    /** 초기화 여부 */
    def isInitialized: Boolean = byteEncryptor.isInitialized

    /**
     * 비밀번호 지정
     *
     * @param password 비밀번호
     */
    def setPassword(password: String) {
        byteEncryptor.setPassword(password)
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

        log.trace(s"데이터를 암호화합니다. algorithm=[$algorithm]")
        byteEncryptor.encrypt(input)
    }

    /**
     * 문자열을 암호화 합니다.
     * @param plainText  암호화할 문자열
     * @return 암호화된 문자열
     */
    @inline
    def encrypt(plainText: String): String = {
        if (Strings.isEmpty(plainText)) Strings.EMPTY_STR

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

        log.trace(s"데이터를 복호화합니다. algorithm=[$algorithm]")
        byteEncryptor.decrypt(input)
    }

    /**
     * 암호화된 문자열을 복원합나다.
     * @param cipherText 암호화된 문자열
     * @return 복원된 데이터
     */
    @inline
    def decrypt(cipherText: String): String = {
        if (Strings.isEmpty(cipherText)) Strings.EMPTY_STR

        val plainBytes = Strings.getBytesFromString(cipherText, BinaryStringFormat.HexDecimal)
        Strings.getUtf8String(decrypt(plainBytes))
    }
}


/**
 * RC2 대칭형 알고리즘을 사용한 암호화 클래스
 */
@Component
class RC2Encryptor extends SymmetricEncryptor {

    def algorithm: String = "PBEwithSHA1andRC2_40"
}

/**
 * DES 대칭형 알고리즘을 사용한 암호화 클래스
 */
@Component
class DESEncryptor extends SymmetricEncryptor {

    def algorithm: String = "PBEwithMD5andDES"
}

/**
 * TripleDES 알고리즘을 사용하는 암호기
 */
@Component
class TripleDESEncryptor extends SymmetricEncryptor {

    def algorithm: String = "PBEwithSHA1andDESEDE"
}