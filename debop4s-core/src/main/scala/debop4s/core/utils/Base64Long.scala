package debop4s.core.utils

/**
 * `Long` 수형 값을 base 64 문자열로 인코딩을 수행합니다.
 * 캐시 키 값에 사용하면 좋습니다.
 */
object Base64Long {

  private[this] lazy val standardBase64Alphabet: (Int) => Char =
    Array[Char](
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', '+', '/'
    )

  /** The bit width of a base 64 digit. */
  private[this] val digitWidth: Int = 6

  /** Mask for the least-sgnificant digit. */
  private[this] val digitMask: Int = (1 << digitWidth) - 1

  /** The amount to shift right for the first base 64 digit in a Long. */
  private[this] val startingBitPosition: Int = 60

  /** Enable re-use of the StringBuilder for toBase64(Long): String */
  private[this] val threadLocalBuilder = new ThreadLocal[StringBuilder] {
    override def initialValue = new StringBuilder
  }

  /** Convert this Long to a base 64 String, using the standard base 64 alphabet. */
  def toBase64(n: Long): String = {
    val builder = threadLocalBuilder.get()
    builder.clear()
    setBase64(builder, n)
    builder.toString()
  }

  /**
   * `Long` 수형의 값을 base 64 인코딩을 수행하여 `StringBuilder` 에 추가합니다.
   * The Base64 encoding uses the standard Base64 alphabet (with '+' and '/'). It does not pad the
   * result. The representation is just like base 10 or base 16, where leading zero digits are
   * omitted.
   *
   * The number is treated as unsigned, so there is never a leading negative sign, and the
   * representations of negative numbers are larger than positive numbers.
   */
  private def setBase64(builder: StringBuilder, n: Long, alphabet: Int => Char = standardBase64Alphabet): Unit = {
    if (n == 0) {
      // 0 은 특수 문자
      builder append alphabet(0)
    } else {
      var bitPosition = startingBitPosition
      while ((n >>> bitPosition) == 0) {
        bitPosition -= digitWidth
      }
      // Copy in the 6-bit segments, one at a time.
      while (bitPosition >= 0) {
        val shifted = n >>> bitPosition
        val digitValue = (shifted & digitMask).toInt
        builder.append(alphabet(digitValue))
        bitPosition -= digitWidth
      }
    }
  }
}
