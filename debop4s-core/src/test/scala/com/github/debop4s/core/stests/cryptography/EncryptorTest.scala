package com.github.debop4s.core.stests.cryptography

import com.github.debop4s.core.cryptography.SymmetricEncryptor
import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.utils.Strings
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}
import scala.collection.JavaConversions._

/**
 * com.github.debop4s.core.stests.cryptography.EncryptorTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 10:24
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[CryptographyConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class EncryptorTest extends AbstractCoreTest {

    @Autowired val ctx: ApplicationContext = null

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    val PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록~ Hello World! 1234567890 ~!@#$%^&*()"

    test("byte encryptor") {
        val byteEncryptors = ctx.getBeansOfType(classOf[SymmetricEncryptor]).values()

        byteEncryptors.par.foreach { encryptor =>
            log.debug(s"encryptor=$encryptor")

            encryptor.setPassword("debop")

            val encryptedBytes = encryptor.encrypt(Strings.getUtf8Bytes(PLAIN_TEXT))
            val decryptedBytes = encryptor.decrypt(encryptedBytes)

            decryptedBytes should not equal null
            Strings.getUtf8String(decryptedBytes) should equal(PLAIN_TEXT)
        }
    }

}
