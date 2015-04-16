package debop4s.core.globalization

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.support.{MessageSourceAccessor, ReloadableResourceBundleMessageSource}

/**
 * GlobalizationConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 5:26
 */
@Configuration
class GlobalizationConfiguration {

  /**
   * 반환값을 꼭 `ReloadableResourceBundleMessageSource` 로 하고, Autowired 도 같은 수형으로 해야 제대로 처리한다.
   * 다국어 properties 파일은 외부에 놓을 경우 file: 접두사를 두고, resources 에 있는 경우에는 classpath: 를 두면 된다.
   * @return
   */
  @Bean
  def messageSource(): ReloadableResourceBundleMessageSource = {
    val rbms = new ReloadableResourceBundleMessageSource()

    // resources 에 있는 것은 classpath:i18n/messages 또는 i18n/messages 를 쓰면 된다.
    rbms.setBasename("file:i18n/messages")
    rbms.setDefaultEncoding("UTF-8")
    rbms.setCacheSeconds(60)
    rbms.setFallbackToSystemLocale(true)

    rbms
  }

  @Bean
  def messageSourceAccessor(): MessageSourceAccessor = {
    new MessageSourceAccessor(messageSource())
  }

}
