package kr.debop4s.core.globalization

import java.util.{ResourceBundle, Locale}
import kr.debop4s.core.logging.Logger
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.AssertionsForJUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{MessageSource, ApplicationContext}
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * GlobalizationTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 5:26
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
//
// NOTE: classpath에 있는 messages 를 xml configuration 에서 똑같이 정의하면 제대로 읽어드리는데,
// NOTE: java config에서 작업하면 파일을 찾지 못한다!!!
//
@ContextConfiguration(locations = Array("classpath:globalization.xml"))
// @ContextConfiguration(classes = Array(classOf[GlobalizationConfiguration]))
class GlobalizationTest extends AssertionsForJUnit {

    lazy val log = Logger[GlobalizationTest]

    @Autowired
    var applicationContext: ApplicationContext = _

    @Autowired
    var messageSource: MessageSource = _

    @Test
    def loadLocalMessages() {
        val intro = messageSource.getMessage("intro", null, Locale.getDefault)
        assert(intro === "안녕하세요.")
        log.debug(s"intro=[$intro]")

        val english = messageSource.getMessage("intro", null, Locale.US)
        assert(english === "Hello.")
        log.debug(s"english=[$english]")

        val korean = messageSource.getMessage("intro", null, Locale.KOREA)
        assert(korean === "안녕하세요.")
        log.debug(s"korean=[$korean]")
    }

    @Test
    def resourceBundleTest() {
        val bundle = ResourceBundle.getBundle("messages")
        val deftext = bundle.getString("intro")
        assert(deftext === "안녕하세요.")

        val bundleUS = ResourceBundle.getBundle("messages", Locale.US)
        val english = bundleUS.getString("intro")
        assert(english === "Hello.")

        val bundleKr = ResourceBundle.getBundle("messages", Locale.KOREA)
        val korean = bundleKr.getString("intro")
        assert(korean === "안녕하세요.")
    }
}
