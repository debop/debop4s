package debop4s.data.slick.customtypes

/**
 * 데이터베이스에 정보를 압축 저장할 때 사용하는 수형 변환을 수행합니다.
 * @author sunghyouk.bae@gmail.com
 */
case class CompressedArray(value: Array[Byte]) extends AnyVal
