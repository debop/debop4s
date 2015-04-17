package debop4s.web.spring

import java.io.File

import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.catalina.core._
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener
import org.apache.catalina.startup.Tomcat
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal


/**
 * Tomcat Server 를 Embedded 방식으로 실행할 수 있도록 지원합니다.
 *
 * {{{
 *   object JettyLauncher extends TomcatLauncherSupport {
 *    override def port:Int = 8080
 *   }
 * }}}
 * @author sunghyouk.bae@gmail.com
 */
trait TomcatLauncherSupport extends App {

  private val log = LoggerFactory.getLogger(getClass)

  def port: Int = 8080
  def contextPath: String = "/"
  def resourceBase: String = "src/main/webapp"

  def protocol: String = "org.apache.coyote.http11.Http11Protocol"

  protected def initContext(ctx: Context) {}

  try {
    val tomcat = new Tomcat()

    tomcat.getServer.addLifecycleListener(new AprLifecycleListener)
    tomcat.getServer.addLifecycleListener(new JreMemoryLeakPreventionListener)
    tomcat.getServer.addLifecycleListener(new GlobalResourcesLifecycleListener)
    tomcat.getServer.addLifecycleListener(new ThreadLocalLeakPreventionListener)

    val connector: Connector = new Connector(protocol)
    connector.setPort(port)
    connector.setURIEncoding("utf-8")
    connector.setEnableLookups(false)

    tomcat.getService.addConnector(connector)
    tomcat.setConnector(connector)

    tomcat.setPort(port.toInt)

    val context: Context = tomcat.addWebapp(contextPath, new File(resourceBase).getAbsolutePath)

    initContext(context)

    tomcat.start()
    log.info(s"Start Tomcat Web Server")

    tomcat.getServer.await()

  } catch {
    case NonFatal(e) => log.error("TomcatLauncherSupport ERROR", e)
  }

}
