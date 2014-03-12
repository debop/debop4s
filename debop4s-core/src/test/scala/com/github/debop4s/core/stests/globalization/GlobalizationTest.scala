package com.github.debop4s.core.globalization

import java.util.{ResourceBundle, Locale}
import org.scalatest.{Matchers, FunSuite}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{MessageSource, ApplicationContext}
import org.springframework.test.context.support.GenericXmlContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * GlobalizationTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 5:26
 */
//
// NOTE: classpath에 있는 messages 를 xml configuration 에서 똑같이 정의하면 제대로 읽어드리는데,
// NOTE: java config에서 작업하면 파일을 찾지 못한다!!!
//
@ContextConfiguration(locations = Array("classpath:globalization.xml"), loader = classOf[GenericXmlContextLoader])
// @ContextConfiguration(classes = Array(classOf[GlobalizationConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class GlobalizationTest extends FunSuite with Matchers {

    lazy val log = LoggerFactory.getLogger(getClass)

    @Autowired val applicationContext: ApplicationContext = null
    @Autowired val messageSource: MessageSource = null

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    test("load localized messages") {
        require(messageSource != null, "messageSource should not be null")

        val intro = messageSource.getMessage("intro", null, Locale.getDefault)
        intro shouldEqual "안녕하세요."
        log.debug(s"intro=[$intro]")

        val english = messageSource.getMessage("intro", null, Locale.US)
        assert(english === "Hello.")
        log.debug(s"english=[$english]")

        val korean = messageSource.getMessage("intro", null, Locale.KOREA)
        assert(korean === "안녕하세요.")
        log.debug(s"korean=[$korean]")
    }

    test("load resource bundle") {
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
