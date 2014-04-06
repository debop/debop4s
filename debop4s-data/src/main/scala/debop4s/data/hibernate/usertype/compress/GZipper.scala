package debop4s.data.hibernate.usertype.compress

import debop4s.core.compress.{Compressor, GZipCompressor}

/**
 * debop4s.data.hibernate.usertype.compress.GZipper
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:29
 */
private[data] trait GZipper {

  private lazy val _compressor = new GZipCompressor()

  def compressor: Compressor = _compressor

}

/**
 * GZip 알고리즘을 이용하여 바이트 배열 데이터를 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:23
 */
class GZipBinaryUserType extends AbstractCompressedBinaryUserType with GZipper {}

/**
 * GZip 알고리즘을 이용하여 문자열을 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:25
 */
class GZipStringUserType extends AbstractCompressedStringUserType with GZipper {}
