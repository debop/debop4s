package debop4s.web.spring.listener

import javax.servlet.{ServletContextEvent, ServletContextListener}

import org.slf4j.LoggerFactory

/**
 * [[ServletContextListener]] 를 구현한 기본 클래스입니다.
 *
 * @author Sunghyouk Bae
 */
abstract class AbstractServletContextListener extends ServletContextListener {

  private val log = LoggerFactory.getLogger(getClass)

  override def contextInitialized(sce: ServletContextEvent) {
    log.trace(s"ServletContext 리스너를 시작했습니다. servlet name=${ sce.getServletContext.getServletContextName }")
  }

  override def contextDestroyed(sce: ServletContextEvent) {
    log.trace(s"ServletContext 리스너를 종료합니다. servlet name=${ sce.getServletContext.getServletContextName }")
  }
}
