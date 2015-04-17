package debop4s.web.scalatra.controller

import debop4s.web.scalatra.annotations.ServletPath
import debop4s.web.scalatra.scalate.ScalatraWebStack
import org.scalatra.{AsyncResult, FutureSupport}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import scala.concurrent._

/**
 * IndexServlet
 * @author sunghyouk.bae@gmail.com
 */
@ServletPath("/")
@Component
class IndexServlet extends ScalatraWebStack with FutureSupport {

  private val log = LoggerFactory.getLogger(getClass)

  implicit override protected def executor: ExecutionContextExecutor = ExecutionContext.Implicits.global

  before() {
    contentType = "text/html"
    log.debug(s"before...")
  }
  after() {
    log.debug(s"after...")
  }

  get("/") {
    <h1>Scalatra Web Application</h1>
  }

  get("/async") {
    new AsyncResult() {
      override val is = Future {<h1>Scalatra Async Response</h1>}
    }
  }

  get("/index") {
    new AsyncResult() {
      override val is = Future {
        ssp("index")
      }
    }
  }
}
