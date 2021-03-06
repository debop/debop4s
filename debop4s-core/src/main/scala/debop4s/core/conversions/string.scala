package debop4s.core.conversions

import debop4s.core.utils.Strings


/**
 * 문자열 관련 Helper Object
 * Created by debop on 2014. 4. 6.
 */
object string {

  final class RichString(wrapped: String) {
    def quoteC: String = Strings.quoteC(wrapped)
    def unquoteC: String = Strings.unquoteC(wrapped)
    def fromHexString: Array[Byte] = Strings.fromHexString(wrapped)
  }

  final class RichByteArray(bytes: Array[Byte]) {
    def toHexString: String = Strings.toHexString(bytes)
  }

  implicit def richString(s: String): RichString = new RichString(s)
  implicit def richByteArray(bytes: Array[Byte]): RichByteArray = new RichByteArray(bytes)
}

