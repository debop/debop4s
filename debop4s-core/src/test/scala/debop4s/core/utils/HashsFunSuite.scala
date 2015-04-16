package debop4s.core.utils

import debop4s.core.{AbstractCoreFunSuite, YearWeek}

/**
 * HashsFunSuite
 * @author Sunghyouk Bae
 */
class HashsFunSuite extends AbstractCoreFunSuite {

  test("Hash 계산") {
    val a = Hashs.compute(1, 2)
    val b = Hashs.compute(2, 1)

    a should not be b
    a shouldEqual Hashs.compute(1, 2)
    b shouldEqual Hashs.compute(2, 1)

    Hashs.compute(1, null) should not be Hashs.compute(null, 1)

    val withNull1 = Hashs.compute(YearWeek(2013, 1), null)
    val withNull2 = Hashs.compute(null, YearWeek(2013, 1))
    val withNull3 = Hashs.compute(YearWeek(2013, 1), null)

    withNull1 should not be withNull2
    withNull1 shouldEqual withNull3
  }

}
