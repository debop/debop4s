package debop4s.web.scalatra

import java.net.InetSocketAddress

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * Jetty Server 를 직접 실행할 수 있도록 지원합니다.
 *
 * {{{
 *   object JettyLauncher extends JettyLauncherSupport {
 *    override def port:Int = 9000
 *   }
 * }}}
 * @author Sunghyouk Bae
 */
trait JettyLauncherSupport extends App {

  def hostname: String = "localhost"
  def port: Int = 8080
  def contextPath: String = "/"
  def resourceBase: String = "src/main/webapp"

  private lazy val log = LoggerFactory.getLogger(getClass)

  try {
    log.info(s"Start jetty web server. " +
             s"host=$hostname, port=$port, contextPath=$contextPath, resourceBase=$resourceBase")

    val server = new Server(new InetSocketAddress(hostname, port))
    server.setStopAtShutdown(true)

    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath(contextPath)
    context.setResourceBase(resourceBase)

    context.addEventListener(new ScalatraListener())
    server.setHandler(context)

    server.start()
    log.info(s"start jetty web server")

    server.join()

  } catch {
    case NonFatal(e) => log.error("JettyLauncherSupport ERROR", e)
  }

}
