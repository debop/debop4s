package debop4s.core

import org.scalatest.{FunSuite, Matchers}
import org.slf4j.LoggerFactory

/**
 * ClassFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ClassFunSuite extends FunSuite with Matchers {

  private lazy val log = LoggerFactory.getLogger(getClass)

  test("create instance") {
    val dummy: Dummy = new Dummy {
      id = 1
      name = "debop"
    }
    println(s"Dummy class = ${ dummy.getClass }")
  }

  class Dummy {
    var id: Int = _
    var name: String = _
  }
}
