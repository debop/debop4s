package debop4s.core.cryptography

import java.security.Security

import debop4s.core.AbstractCoreFunSuite
import org.jasypt.digest.{PooledStringDigester, StandardStringDigester}
import org.jasypt.encryption.pbe.{StandardPBEByteEncryptor, StandardPBEStringEncryptor}
import org.jasypt.util.text.BasicTextEncryptor

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

/**
 * debop4s.core.stests.cryptography.JasyptFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 9:14
 */
class JasyptFunSuite extends AbstractCoreFunSuite {

  val DigesterAlgorithms = Array("MD5", "SHA", "SHA-256", "SHA-384", "SHA-512")

  test("load algorithms") {
    Security.getProviders.foreach { provider =>
      log.trace(s"provider=${ provider.getName }")
      provider.getServices.asScala.foreach { service =>
        log.trace(s"\tAlgorithm=${ service.getAlgorithm }")
      }
    }
  }

  test("load algorithm of message digest") {
    Security.getAlgorithms("MessageDigest").asScala.foreach { algorithm =>
      log.trace(s"MessageDigest algorithm=$algorithm")
    }
  }

  test("load ciphers") {
    Security.getAlgorithms("Cipher").asScala.foreach { algorithm =>
      log.trace(s"Symmetric algorithm=$algorithm")
    }
  }

  test("standard string digest") {
    DigesterAlgorithms.foreach { algorithm =>
      val digester = new StandardStringDigester()
      digester.setAlgorithm(algorithm)
      digester.setIterations(10)

      val digest = digester.digest("password")

      digester.matches("Password", digest) shouldEqual false
      digester.matches("passworD", digest) shouldEqual false
      digester.matches("password", digest) shouldEqual true
    }
  }

  test("pooled string digest") {
    DigesterAlgorithms.foreach(algorithm => {
      val digester = new PooledStringDigester()
      digester.setPoolSize(5)
      digester.setAlgorithm(algorithm)
      digester.setIterations(10)

      (0 until 10).par.foreach { x =>
        val digest = digester.digest("password")

        digester.matches("Password", digest) shouldEqual false
        digester.matches("passworD", digest) shouldEqual false
        digester.matches("password", digest) shouldEqual true
      }
    })
  }

  val PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록~ Hello World! 1234567890 ~!@#$%^&*()"
  val EncryptorAlgorithm = Array("AES", "AESWARP", "ARCFOUR", "BLOWFISH", "DES", "DESEDE",
    "DESEDEWARP", "PBEWITHMD5ANDDES", "PBEWITHMD5ANDTRIPLEDES",
    "PBEWITHSHA1ANDDESEDE", "PBEWITHSHA1ANDRC2_40", "RC2")

  test("basic test encryptor") {
    Security.getAlgorithms("Cipher").asScala.foreach { algorithm =>
      log.trace(s"Algorithm=$algorithm")

      val encryptor = new BasicTextEncryptor()
      encryptor.setPassword("debop")

      val encrypted = encryptor.encrypt(PLAIN_TEXT)
      val decrypted = encryptor.decrypt(encrypted)

      decrypted shouldEqual PLAIN_TEXT
    }
  }

  val PBEAlgorithms = Array("PBEWITHSHA1ANDDESEDE", "PBEWITHMD5ANDDES", "PBEWITHSHA1ANDRC2_40")

  test("standard PBE String encryptor") {
    PBEAlgorithms.foreach { algorithm =>
      log.trace(s"StandardPBEStringEncryptor algorith=$algorithm")
      try {
        val encryptor = new StandardPBEStringEncryptor()
        encryptor.setAlgorithm(algorithm)
        encryptor.setPassword("debop")

        val encrypted = encryptor.encrypt(PLAIN_TEXT)
        val decrypted = encryptor.decrypt(encrypted)

        decrypted shouldEqual PLAIN_TEXT
      } catch {
        case NonFatal(e) => log.error(s"$algorithm 은 지원하지 않습니다.", e)
      }
    }
  }

  test("standard PBE Byte encryptor") {
    PBEAlgorithms.foreach { algorithm =>
      log.trace(s"StandardPBEByteEncryptor algorith=$algorithm")
      try {
        val encryptor = new StandardPBEByteEncryptor()
        encryptor.setAlgorithm(algorithm)
        encryptor.setPassword("debop")

        val encrypted = encryptor.encrypt(PLAIN_TEXT.getBytes("UTF-8"))
        val decrypted = encryptor.decrypt(encrypted)
        new String(decrypted, "UTF-8") shouldEqual PLAIN_TEXT
      } catch {
        case NonFatal(e) => log.error(s"$algorithm 은 지원하지 않습니다.", e)
      }
    }
  }
}
