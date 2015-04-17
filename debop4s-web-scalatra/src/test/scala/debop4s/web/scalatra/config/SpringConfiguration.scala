package debop4s.web.scalatra.config

import debop4s.web.scalatra.controller.IndexServlet
import org.springframework.context.annotation.{ComponentScan, Configuration}

/**
 * SpringConfiguration
 * @author sunghyouk.bae@gmail.com
 */
@Configuration
@ComponentScan(basePackageClasses = Array(classOf[IndexServlet]))
class SpringConfiguration {

}
