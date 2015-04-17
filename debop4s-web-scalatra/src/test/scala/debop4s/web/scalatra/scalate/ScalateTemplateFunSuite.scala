package debop4s.web.scalatra.scalate

import java.util.Date

import org.fusesource.scalate.{Template, TemplateEngine}
import org.scalatest.{FunSuite, Matchers, OptionValues}
import org.slf4j.LoggerFactory

/**
 * ScalateTemplateFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ScalateTemplateFunSuite extends FunSuite with Matchers with OptionValues {

  private val log = LoggerFactory.getLogger(getClass)

  private var _engine: TemplateEngine = _

  def engine: TemplateEngine = synchronized {
    if (_engine == null)
      _engine = new TemplateEngine()

    _engine
  }

  test("simple template text") {
    val templateText = "<%@ val username:String %> Hello ${username}!"
    val template = engine.compileSsp(templateText)
    val output = engine.layout(template.source, Map("username" -> "배성혁"))

    output should include("Hello 배성혁!")
  }

  test("parameterized template") {
    val params = Map("amount" -> 128.0, "time" -> new Date())

    val templateText =
      """
        |<%@ val time: java.util.Date %>
        |<%@ val amount:java.lang.Double %>
        |${time} ${amount}
      """.stripMargin

    val template = engine.compileSsp(templateText)
    val text = engine.layout(template.source, params)

    text should include("128")
  }

  test("template file") {
    val template: Template = engine.load("templates/Greetings.ssp")
    val output = engine.layout(template.source,
      Map("title" -> "환영 메일", "username" -> "Sunghyouk Bae"))

    log.trace(s"output=$output")
    output should include("Sunghyouk Bae")
  }


  test("template file with class") {

    val person = Person("1", "배성혁", "debop@hconnect.co.kr")
    val params = Map("person" -> person)
    val output = engine.layout("templates/welcome.ssp", params)

    log.trace(s"output=$output")
    output should include("배성혁")
  }

  test("multi thread test") {
    val template: Template = engine.load("templates/Greetings.ssp")

    (0 until 100).par.foreach { _ =>
      val output = engine.layout(template.source,
        Map("title" -> "환영 메일", "username" -> "Sunghyouk Bae"))

      log.trace(s"output=$output")
      output should include("Sunghyouk Bae")
    }
  }

}
