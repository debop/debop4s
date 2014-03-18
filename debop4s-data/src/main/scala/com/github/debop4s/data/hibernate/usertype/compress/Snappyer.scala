package com.github.debop4s.data.hibernate.usertype.compress

import com.github.debop4s.core.compress.{SnappyCompressor, Compressor}

/**
 * Snappyer
 * Created by debop on 2014. 3. 18.
 */
private[data] trait Snappyer {

    private lazy val _compressor = new SnappyCompressor()

    def compressor: Compressor = _compressor
}

/**
 * Snappy 알고리즘을 이용하여 바이트 배열 데이터를 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:23
 */
class SnappyBinaryUserType extends AbstractCompressedBinaryUserType with Snappyer {

}

/**
 * Snappy 알고리즘을 이용하여 문자열을 압축하여 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 6:25
 */
class SnappyStringUserType extends AbstractCompressedStringUserType with Snappyer {

}
