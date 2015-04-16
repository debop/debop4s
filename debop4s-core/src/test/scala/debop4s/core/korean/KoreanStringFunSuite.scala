package debop4s.core.korean

import debop4s.core.AbstractCoreFunSuite

import scala.collection.JavaConverters._


class KoreanStringFunSuite extends AbstractCoreFunSuite {

  test("자소 추출하기") {
    KoreanString.getJasoLetter("동해물과 백두산이 Hello World") shouldEqual "ㄷㅗㅇㅎㅐㅁㅜㄹㄱㅘ ㅂㅐㄱㄷㅜㅅㅏㄴㅇㅣ HELLO WORLD"
  }

  test("초성 추출하기") {
    val chosungs = KoreanString.getChosung("배성혁")
    chosungs.get(0) shouldEqual 'ㅂ'
    chosungs.get(1) shouldEqual 'ㅅ'
    chosungs.get(2) shouldEqual 'ㅎ'
    chosungs shouldEqual List('ㅂ', 'ㅅ', 'ㅎ').asJava
  }
}
