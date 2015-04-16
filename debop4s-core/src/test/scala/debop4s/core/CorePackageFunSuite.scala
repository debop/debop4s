package debop4s.core

/**
 * CorePackageFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class CorePackageFunSuite extends AbstractCoreFunSuite {

  test("int convert") {
    "1".asInt shouldBe 1
    1L.asInt shouldBe 1
    1.0.asInt shouldBe 1
  }
}
