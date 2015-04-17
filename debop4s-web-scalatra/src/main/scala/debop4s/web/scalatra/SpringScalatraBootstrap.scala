package debop4s.web.scalatra

import javax.servlet.ServletContext

import debop4s.web.scalatra.annotations.ServletPath
import org.scalatra.{LifeCycle, ScalatraServlet}
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.GenericApplicationContext

import scala.collection.JavaConverters._

/**
 * ScalatraBootstrap을 Spring framework을 이용하여 등록하도록하는 abstract class 입니다.
 * `ScalatraBootstrap`클래스를 정의할 때 이 클래스를 상속 받고, Spring 환경설정을 지정해 주면 됩니다.
 * Servlet은 @Servlet 과 @Component 로 등록하면 됩니다.
 *
 * {{{
 * class ScalatraBootstrap extends SpringScalatraBootstrap {
 *    override protected def configClasses(): Array[Class[_]] = {
 *       Array(classOf[SpringConfiguration])
 *    }
 * }
 * }}}
 *
 * @see http://debop.tumblr.com/post/88952216641/scalatra-spring
 * @author sunghyouk.bae@gmail.com
 */
trait SpringScalatraBootstrap extends LifeCycle {

  private lazy val log = LoggerFactory.getLogger(getClass)

  protected def configClasses(): Array[Class[_]] = Array()

  protected def basePackages(): Array[String] = Array()

  var appContext: GenericApplicationContext = _

  override def init(context: ServletContext) {
    log.info(s"Scalatra Servlet을 등록합니다.")

    loadApplicationContext()
    assert(appContext != null)

    val resources = appContext.getBeansWithAnnotation(classOf[ServletPath])

    if (resources.size() == 0)
      log.warn(s"Controller에 @ServletPath를 지정하지 않았거나, ComponentScan으로 찾도록 설정하지 않았습니다!!!")

    resources.values().asScala.foreach {
      case servlet: ScalatraServlet =>
        var path = servlet.getClass.getAnnotation(classOf[ServletPath]).value()
        if (!path.startsWith("/"))
          path = "/" + path

        log.info(s"Mount servlet. servlet=$servlet, path=$path")
        context.mount(servlet, path)

      case resource =>
        log.warn(s"ScalatraServlet 을 상속받지 않는 class에 @ServletPath 를 지정했습니다. resource=$resource")
    }
  }

  protected def loadApplicationContext() {
    val classes = configClasses()
    val packages = basePackages()

    log.info(s"Load configurations. classes=${ classes.mkString }, packages=${ packages.mkString }")

    if (classes.length > 0)
      appContext = new AnnotationConfigApplicationContext(classes: _*)
    else if (packages.length > 0)
      appContext = new AnnotationConfigApplicationContext(packages: _*)
  }

}
