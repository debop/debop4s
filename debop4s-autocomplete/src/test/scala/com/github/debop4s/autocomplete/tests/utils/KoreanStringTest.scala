package com.github.debop4s.autocomplete.tests.utils

import com.github.debop4s.autocomplete.tests.AbstractAutoCompleteTest
import com.github.debop4s.autocomplete.utils.KoreanString
import scala.collection.mutable.ListBuffer

/**
 * KoreanStringTest
 * Created by debop on 2014. 3. 14.
 */
class KoreanStringTest extends AbstractAutoCompleteTest {

    test("자소 추출하기") {
        println(KoreanString.getJasoLetter("동해물과 백두산이 Hello World"))
    }

    test("초성 추출하기") {
        val chosungs = KoreanString.getChosung("배성혁")
        assert(chosungs(0) == 'ㅂ')
        assert(chosungs(1) == 'ㅅ')
        assert(chosungs(2) == 'ㅎ')
        assert(chosungs == ListBuffer('ㅂ', 'ㅅ', 'ㅎ'))
    }
}
