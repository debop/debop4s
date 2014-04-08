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
   * NOTE: classpath에 있는 messages 를 xml configuration 에서 똑같이 정의하면 제대로 읽어드리는데,
   * NOTE: java config에서 작업하면 파일을 찾지 못한다!!!
   */
  @Bean
  def resourceBundleMessageSource() = {
    val rbms = new ReloadableResourceBundleMessageSource()
    rbms.setBasename("classpath:messages")
    rbms.setDefaultEncoding("UTF-8")
    rbms.setUseCodeAsDefaultMessage(true)

    rbms.getParentMessageSource
  }

  @Bean
  def messageSourceAccessor() =
    new MessageSourceAccessor(resourceBundleMessageSource())

}
