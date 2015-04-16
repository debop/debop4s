package debop4s.core.io

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.io.model._

import scala.pickling._
import scala.pickling.binary._

/**
 * PicklingFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PicklingFunSuite extends AbstractCoreFunSuite {

  test("case class pickling") {
    val joe = PersonEntity(0, "joe", Array(3, 4, 13))
    val pickled = joe.pickle
    log.debug(s"pickled=$pickled")
    val converted = pickled.unpickle[PersonEntity]

    converted.name shouldEqual joe.name
    converted shouldEqual joe
  }

  test("pickling trait") {
    val com = CompanyEntity(0, "구글", "google")
    val pickled = com.pickle

    pickled.unpickle[Entity[Long]] match {
      case x: CompanyEntity => x shouldEqual com
      case _ => fail("fail unpickle trait")
    }
  }

  test("pickling any") {
    val com = CompanyEntity(0, "구글", "google")
    val pickled = com.pickle

    pickled.unpickle[Any] match {
      case x: CompanyEntity => x shouldEqual com
      case _ => fail("fail unpickle trait")
    }

    val converted = pickled.unpickle[Any].asInstanceOf[CompanyEntity]
    converted should not be null
  }
}



