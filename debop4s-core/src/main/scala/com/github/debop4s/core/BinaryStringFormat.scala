package com.github.debop4s.core

/**
 * com.github.debop4s.core.BinaryStringFormat
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:20
 */
object BinaryStringFormat extends Enumeration {

    type BinaryStringFormat = Value

    val Base64 = Value(0, "Base64")

    val HexDecimal = Value(1, "HexDecimal")

}
