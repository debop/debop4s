package debop4s.core.retry

import debop4s.core.AbstractCoreFunSuite

import scala.util.Try

/**
 * SuccessfulFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class SuccessfulFunSuite extends AbstractCoreFunSuite {

  test("Successful either") {
    val either = implicitly[Successful[Either[String, String]]]

    either.predicate(Right("")) shouldEqual true
    either.predicate(Left("")) shouldEqual false
  }

  test("Successful option") {
    val option = implicitly[Successful[Option[String]]]

    option.predicate(Some("")) shouldEqual true
    option.predicate(None) shouldEqual false
  }

  test("Successful tried") {
    val tried = implicitly[Successful[Try[String]]]

    tried.predicate(Try("")) shouldEqual true
    tried.predicate(Try { throw new RuntimeException("") }) shouldEqual false
  }

  test("Successful combinators") {
    val a = Successful[Int](_ > 1)
    val b = Successful[Int](_ < 3)

    a.and(b).predicate(2) shouldEqual true
    a.and(false).predicate(2) shouldEqual false
    a.and(true).predicate(2) shouldEqual true

    a.or(b).predicate(4) shouldEqual true
    a.or(true).predicate(0) shouldEqual true
    a.or(false).predicate(0) shouldEqual false
  }

}
