package com.github.debop4s.data.hibernate.usertype.compress

import com.github.debop4s.core.compress.{Compressor, DeflateCompressor}

/**
 * com.github.debop4s.data.hibernate.usertype.compress.Deflator 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:28
 */
private[data] trait Deflator {

    private lazy val _compressor = new DeflateCompressor()

    def compressor: Compressor = _compressor
}

/**
 * Deflator 를 이용하여 바이트 배열 데이터를 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:26
 */
class DeflateBinaryUserType extends AbstractCompressedBinaryUserType with Deflator {}

/**
 * Deflator 를 이용하여 문자열를 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:27
 */
class DeflateStringUserType extends AbstractCompressedStringUserType with Deflator {}