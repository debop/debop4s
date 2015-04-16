package debop4s.core

/**
 * GuardFunSuite
 * @author Sunghyouk Bae
 */
class GuardFunSuite extends AbstractCoreFunSuite {

  test("Guard shouldBe") {
    Guard.shouldBe(1 > 0, "always true")

    intercept[AssertionError] {
      Guard.shouldBe(1 > 2, "always false")
    }
  }

  test("Gaurd shouldNotBeNull") {
    Guard.shouldNotBeNull("value", "allways true")

    intercept[AssertionError] {
      Guard.shouldNotBeNull(null, "oops! it's null")
    }
  }

  test("Guard shouldBeInRange") {
    Guard.shouldBeInRange(3, 0, 5, "value")

    intercept[AssertionError] {
      Guard.shouldBeInRange(10, 0, 5, "value")
    }
  }

}
