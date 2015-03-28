package debop4s.data.slick3.customtypes

/**
 * 암호화된 문자열에 대한 사용자 정의 컬럼 수형 (Custom Column Type) 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
case class EncryptedString(text: String) {

  override def equals(obj: scala.Any): Boolean = {
    obj != null &&
    obj.isInstanceOf[EncryptedString] &&
    obj.asInstanceOf[EncryptedString].text.equals(text)
  }
}
