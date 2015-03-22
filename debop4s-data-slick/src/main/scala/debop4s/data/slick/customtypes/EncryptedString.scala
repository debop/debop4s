package debop4s.data.slick.customtypes

/**
 * 암호화된 문자열에 대한 사용자 정의 컬럼 수형 (Custom Column Type) 입니다.
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
case class EncryptedString(text: String) extends AnyVal
