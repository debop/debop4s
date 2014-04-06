package debop4s.core.stests.utils

import debop4s.core.stests.AbstractCoreTest
import debop4s.core.utils.KoreanString


/**
 * KoreanStringTest
 * Created by debop on 2014. 3. 14.
 */
class KoreanStringTest extends AbstractCoreTest {

  test("자소 추출하기") {
    println(KoreanString.getJasoLetter("동해물과 백두산이 Hello World"))
  }

  test("초성 추출하기") {
    val chosungs = KoreanString.getChosung("배성혁")
    assert(chosungs(0) == 'ㅂ')
    assert(chosungs(1) == 'ㅅ')
    assert(chosungs(2) == 'ㅎ')
    assert(chosungs == Seq('ㅂ', 'ㅅ', 'ㅎ'))
  }
}
