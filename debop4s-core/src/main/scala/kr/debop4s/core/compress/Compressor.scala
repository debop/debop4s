package kr.debop4s.core.compress

import org.slf4j.LoggerFactory

/**
 * 데이터를 압축/복원을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:52
 */
trait Compressor {

    lazy val log = LoggerFactory.getLogger(getClass)

    val BUFFER_SIZE = 4096

    protected def doCompress(plainBytes: Array[Byte]): Array[Byte]

    protected def doDecompress(compressedBytes: Array[Byte]): Array[Byte]

    /**
     * 데이터를 압축합니다.
     * @param plainBytes 압축할 데이터
     * @return 압축된 데이터
     */
    def compress(plainBytes: Array[Byte]): Array[Byte] = {
        if (plainBytes == null || plainBytes.length == 0)
            return Array.emptyByteArray

        val result = doCompress(plainBytes)

        log.trace(s"데이터를 압축했습니다. 압축률=[${result.length * 100.0 / plainBytes.length}], " +
                  s"original=[${plainBytes.length}], compressed=[${result.length}]")
        result
    }

    /**
     * 압축된 데이터를 복원합니다.
     * @param compressedBytes 압축된 데이터
     * @return 복원된 데이터
     */
    def decompress(compressedBytes: Array[Byte]): Array[Byte] = {
        if (compressedBytes == null || compressedBytes.length == 0)
            return Array.emptyByteArray

        val result = doDecompress(compressedBytes)

        log.trace(s"데이터를 복했습니다. 압축률=[${result.length * 100.0 / result.length}], " +
                  s"압축=[${compressedBytes.length}}], 원본=[${result.length}]")
        result
    }
}