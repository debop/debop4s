package debop4s.core.utils

import debop4s.core.{AbstractCoreTest, YearWeek}

/**
 * HashsTest
 * @author Sunghyouk Bae
 */
class HashsTest extends AbstractCoreTest {

  test("Hash 계산") {
    val a = Hashs.compute(1, 2)
    val b = Hashs.compute(2, 1)

    assert(a != b)
    assert(a == Hashs.compute(1, 2))
    assert(b == Hashs.compute(2, 1))

    assert(Hashs.compute(1, null) != Hashs.compute(null, 1))

    val withNull1 = Hashs.compute(YearWeek(2013, 1), null)
    val withNull2 = Hashs.compute(null, YearWeek(2013, 1))
    val withNull3 = Hashs.compute(YearWeek(2013, 1), null)

    assert(withNull1 != withNull2)
    assert(withNull1 == withNull3)
  }

}
