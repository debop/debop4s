package debop4s.core.cryptography

import debop4s.core.AbstractCoreFunSuite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

@ContextConfiguration(classes = Array(classOf[CryptographyConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class EncryptorFunSuite extends AbstractCoreFunSuite {

  @Autowired val ctx: ApplicationContext = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  val PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록~ Hello World! 1234567890 ~!@#$%^&*()"
  val PLAIN_BYTES = PLAIN_TEXT.getBytes("UTF-8")

  test("byte encryptor") {
    val encryptors = ctx.getBeansOfType(classOf[SymmetricEncryptorSupport]).values().asScala

    encryptors.foreach { encryptor =>
      log.debug(s"encryptor=${ encryptor.algorithm }")

      val encryptedBytes = encryptor.encrypt(PLAIN_BYTES)
      val encryptedBytes2 = encryptor.encrypt(PLAIN_BYTES)

      encryptedBytes shouldEqual encryptedBytes2

      val decryptedBytes = encryptor.decrypt(encryptedBytes)

      decryptedBytes should not be null
      new String(decryptedBytes, "UTF-8") shouldEqual PLAIN_TEXT
    }
  }

  test("문자열 암호화") {
    val encryptors = ctx.getBeansOfType(classOf[SymmetricEncryptorSupport]).values().asScala

    encryptors.foreach { encryptor =>
      log.debug(s"encryptor=${ encryptor.algorithm }")

      val cipherText1 = encryptor.encrypt(PLAIN_TEXT)
      val cipherText2 = encryptor.encrypt(PLAIN_TEXT)

      cipherText1 shouldEqual cipherText2

      val decryptedText = encryptor.decrypt(cipherText1)

      decryptedText shouldEqual PLAIN_TEXT

    }
  }

  test("RC2 암호화 - salt 로 안정화 시") {
    val encryptor = new RC2Encryptor()

    val plainPassword = "@baekwon9"
    val cipherPassword_salt = "f7919045e173ab06471a515a55cd42c6"

    val cipherPassword2 = encryptor.encrypt(plainPassword)

    log.debug(s"cipherPassword=$cipherPassword_salt, cipherPassword2=$cipherPassword2")
    cipherPassword2 shouldEqual cipherPassword_salt

    val decryptPassword = encryptor.decrypt(cipherPassword_salt)
    decryptPassword shouldEqual plainPassword
  }

  test("RC2 암호화 - salt 없이 복호화!!!") {
    val encryptorWithoutSalt = new RC2Encryptor()

    val plainPassword = "@baekwon9"
    val cipherPassword = "f1fccb9c6bd30640cdac5d06e9d47a13202303079c827b6f"

    val decryptPassword = encryptorWithoutSalt.decrypt(cipherPassword)
    log.debug(s"plainPassword=$plainPassword, decryptPassword=$decryptPassword")
    decryptPassword shouldEqual plainPassword
  }

  test("salt 없이 암호화된 정보를 복원합니다.") {
    val encryptor = new RC2Encryptor()
    val encryptorWithoutSalt = new RC2Encryptor()

    val plainPassword = "@baekwon9"
    val cipherPassword = "f1fccb9c6bd30640cdac5d06e9d47a13202303079c827b6f"

    // salt 를 가진 값으로 암호화를 수행
    val decryptPassword =
      try {
        encryptor.decrypt(cipherPassword)
      } catch {
        case NonFatal(e) => encryptorWithoutSalt.decrypt(cipherPassword)
      }

    decryptPassword shouldEqual plainPassword
  }

}
