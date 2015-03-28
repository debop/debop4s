package debop4s.core.utils

import debop4s.core.AbstractCoreTest

/**
 * BijectionTest
 * @author Sunghyouk Bae
 */
class BijectionTest extends AbstractCoreTest {

  case class Foo(i: Int)

  val fooject = new Bijection[Foo, Int] {
    def apply(f: Foo) = f.i
    def invert(i: Int) = if (i % 2 == 0) Foo(i) else sys.error("not really a bijection, natch")
  }

  def isAFoo(i: Int) = i match {
    case fooject(f) => "a foo! " + f.toString
    case _ => "not a foo"
  }

  test("Bijection - return the original when inverting the inverse") {
    fooject.inverse.inverse shouldEqual fooject
  }

  test("Bijection - can be used for pattern-match") {
    isAFoo(2) shouldEqual "a foo! Foo(2)"
    isAFoo(1) shouldEqual "not a foo"
  }
}
