package debop4s.core.cryptography

import debop4s.core.AbstractCoreTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ ContextConfiguration, TestContextManager }

import scala.collection.JavaConverters._

/**
 * debop4s.core.stests.cryptography.StringDigesterTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 10:27
 */
@ContextConfiguration(classes = Array(classOf[CryptographyConfiguration]),
                       loader = classOf[AnnotationConfigContextLoader])
class StringDigesterTest extends AbstractCoreTest {

  @Autowired val ctx: ApplicationContext = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  val PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록~ Hello World! 1234567890 ~!@#$%^&*()"

  test("string digest") {
    val digesters = ctx.getBeansOfType(classOf[StringDigesterSupport]).values()

    digesters.asScala.foreach { digester =>
      log.debug(s"Digest message by ${ digester.algorithm }")
      val digestedText = digester.digest(PLAIN_TEXT)
      digester.matches(PLAIN_TEXT, digestedText) shouldEqual true
    }
  }

  test("digest matches") {
    val digester = new SHA512StringDigester()
    val digest1 = digester.digest(PLAIN_TEXT)
    digester.matches(PLAIN_TEXT, digest1) shouldEqual true
  }

  test("digest multiple") {
    val digester = new SHA512StringDigester()
    val digest1 = digester.digest(PLAIN_TEXT)
    val digest2 = digester.digest(PLAIN_TEXT)
    digest1 shouldEqual digest2
  }
}
