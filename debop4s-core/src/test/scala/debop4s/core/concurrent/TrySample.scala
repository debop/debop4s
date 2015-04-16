package debop4s.core.concurrent

import org.scalatest.{FunSuite, Matchers}

import scala.util.{Failure, Success, Try}

/**
 * TrySample
 * @author Sunghyouk Bae
 */
class TrySample extends FunSuite with Matchers {

  class MyException extends Exception
  val e = new Exception("this is an exception")

  test("Try should catch exceptions and lift into the Try type") {
    val a = Try[Int] { throw e }
    a match {
      case Failure(_) => // ok
      case Success(x) => fail()
    }

    intercept[AbstractMethodError] {
      Try[Int] { throw new AbstractMethodError }
    }
  }
}
