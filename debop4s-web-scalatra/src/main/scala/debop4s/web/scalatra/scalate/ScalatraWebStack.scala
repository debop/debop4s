package debop4s.web.scalatra.scalate

import javax.servlet.http.HttpServletRequest

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.layout.DefaultLayoutStrategy
import org.scalatra._
import org.scalatra.scalate._

import scala.collection.mutable

/**
 * Scalate Template Engine을 이용하여 Web Page를 제공하는 Servlet에 기본 제공하는 Trait 입니다.
 * {{{
 *   class MyServlet extends ScalatraWebStack {
 *      before() {
 *        contentType = "text/html"
 *      }
 *      get("/") {
 *        // webapp/WEB-INF/templates/views/index.ssp 를 컴파일해서 제공한다.
 *        ssp("index")
 *      }
 *   }
 * }}}
 * @author sunghyouk.bae@gmail.com
 */
trait ScalatraWebStack extends ScalatraServlet with ScalateSupport {

  // wire up the precompiled template
  override protected def defaultTemplatePath: List[String] = List("/templates/views")
  override protected def createTemplateEngine(config: ConfigT) = {
    val engine = super.createTemplateEngine(config)
    engine.layoutStrategy =
      new DefaultLayoutStrategy(engine, TemplateEngine.templateTypes.map("/templates/layouts/default." + _): _*)
    engine.packagePrefix = "templates"
    engine
  }

  override protected def templateAttributes(implicit request: HttpServletRequest): mutable.Map[String, Any] = {
    super.templateAttributes ++ mutable.Map.empty
  }

  notFound {
    // remove content type in case it was set through an action
    contentType = null

    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
