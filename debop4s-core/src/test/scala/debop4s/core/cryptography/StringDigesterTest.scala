package debop4s.core.cryptography

import debop4s.core.AbstractCoreTest
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}
import scala.collection.JavaConversions._

/**
 * debop4s.core.stests.cryptography.StringDigesterTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 10:27
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[CryptographyConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class StringDigesterTest extends AbstractCoreTest {

  @Autowired val ctx: ApplicationContext = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  val PLAIN_TEXT = "동해물과 백두산이 마르고 닳도록~ Hello World! 1234567890 ~!@#$%^&*()"

  test("string digest") {
    val digesters = ctx.getBeansOfType(classOf[StringDigester]).values()

    digesters.foreach {
      digester =>
        log.debug(s"Digest message by $digester")
        val digestedText = digester.digest(PLAIN_TEXT)
        digester.matches(PLAIN_TEXT, digestedText) should equal(true)
    }
  }
}
