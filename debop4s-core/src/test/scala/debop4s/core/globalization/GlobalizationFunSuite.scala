package debop4s.core.globalization

import java.util.{ResourceBundle, Locale}
import debop4s.core.AbstractCoreFunSuite
import org.scalatest.{Matchers, FunSuite}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.{MessageSourceAccessor, ReloadableResourceBundleMessageSource}
import org.springframework.context.{MessageSource, ApplicationContext}
import org.springframework.test.context.support.{AnnotationConfigContextLoader, GenericXmlContextLoader}
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * GlobalizationTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 5:26
 */
//
/**
 * MessageSource 를 얻을 때 꼭 `ReloadableResourceBundleMessageSource` 수형을 써줘야 제대로 작동합니다.
 * @author sunghyouk.bae@gmail.com
 */
@ContextConfiguration(classes = Array(classOf[GlobalizationConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class GlobalizationFunSuite extends AbstractCoreFunSuite {

  @Autowired val context: ApplicationContext = null

  @Autowired val messageSource: ReloadableResourceBundleMessageSource = null
  @Autowired val messageSourceAccessor: MessageSourceAccessor = null

  // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  test("configuration") {
    context should not be null
    messageSource should not be null
    messageSourceAccessor should not be null
  }

  test("load Locale message") {

    val defaultText = messageSource.getMessage("intro", null, Locale.getDefault)
    defaultText shouldEqual "안녕하세요."

    val englishText = messageSource.getMessage("intro", null, Locale.US) // en_US
    englishText shouldEqual "Hello."

    val koreanText = messageSource.getMessage("intro", null, Locale.KOREA) // ko_KR
    koreanText shouldEqual "안녕하세요."

    val koreaText = messageSource.getMessage("intro", null, Locale.KOREAN) // ko -> default
    koreaText shouldEqual "안녕하세요."

    val chineseText = messageSource.getMessage("intro", null, Locale.SIMPLIFIED_CHINESE) // zh_CN -> default
    chineseText shouldEqual "안녕하세요."
  }

  test("load Locale Message by Context") {
    val defaultText = context.getMessage("intro", null, "기본값", Locale.getDefault)
    defaultText shouldEqual "안녕하세요."
  }

}
