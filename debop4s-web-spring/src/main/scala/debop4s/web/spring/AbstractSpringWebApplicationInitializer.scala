package debop4s.web.spring

import javax.servlet.Filter

import debop4s.core.spring.JProfiles
import debop4s.core.spring.JProfiles._
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

/**
 * Spring MVC 용 Servlet Initializer 입니다.
 * web.xml 을 사용하지 않고, 코드 상에서 정의합니다.
 *
 * @author Sunghyouk Bae
 */
abstract class AbstractSpringWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  private lazy val log = LoggerFactory.getLogger(getClass)

  override def getServletMappings: Array[String] = Array("/")

  override def getServletFilters: Array[Filter] = {
    val characterEncodingFilter = new CharacterEncodingFilter()
    characterEncodingFilter.setEncoding("UTF-8")

    Array(characterEncodingFilter)
  }

  protected override def createServletApplicationContext: WebApplicationContext = {
    val waCtx = super.createServletApplicationContext
    setProfiles(waCtx)
  }

  private def setProfiles(waCtx: WebApplicationContext): WebApplicationContext = {
    val ctx = waCtx.asInstanceOf[ConfigurableApplicationContext]
    val profile = System.getProperty("profile", LOCAL.name)

    log.info(s"환경설정중 active profile 을 지정합니다. profile=[$profile]")

    if (ctx != null) {
      JProfiles.valueOf(profile) match {
        case LOCAL => ctx.getEnvironment.setActiveProfiles(LOCAL.name)
        case DEVELOP => ctx.getEnvironment.setActiveProfiles(DEVELOP.name)
        case TEST => ctx.getEnvironment.setActiveProfiles(TEST.name)
        case PRODUCTION => ctx.getEnvironment.setActiveProfiles(PRODUCTION.name)
        case _ => ctx.getEnvironment.setActiveProfiles(LOCAL.name)
      }
    }
    ctx.asInstanceOf[WebApplicationContext]
  }
}
