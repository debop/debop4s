package com.github.debop4s.autocomplete.utils

import scala.collection.mutable.ArrayBuffer

/**
 * KoreanString
 * Created by debop on 2014. 3. 14.
 */
object KoreanString {

    val CHO_SUNG = Array[Char](0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139,
        0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149,
        0x314a, 0x314b, 0x314c, 0x314d, 0x314e)

    val JUNG_SUNG = Array[Char](0x314f, 0x3150, 0x3151, 0x3152,
        0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159,
        0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163)

    val JONG_SUNG = Array[Char](0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135,
        0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e,
        0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146,
        0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e)

    /**
     * 문자열 내에서 한글 자소를 분리하여 초/중/종성 순으로 제공합니다.
     */
    def getJasoLetter(target: String): String = {

        val jasoLetters = new StringBuilder()
        val count = target.length

        for (i <- 0 until count) {
            val char = target.charAt(i)

            if (0xAC00 < char && char < 0xD7AF) {
                val initIdx = char - 0xAC00

                val jongIdx = initIdx % 28
                val jungIdx = ((initIdx - jongIdx) / 28) % 21
                val choIdx = ((initIdx / 28) - jungIdx) / 21

                jasoLetters.append("%C".format(CHO_SUNG(choIdx)))
                jasoLetters.append("%C".format(JUNG_SUNG(jungIdx)))
                if (jongIdx != 0x0000) {
                    jasoLetters.append("%C".format(JONG_SUNG(jongIdx)))
                }

            } else {
                jasoLetters.append("%C".format(char))
            }
        }
        jasoLetters.toString()
    }

    /**
    * 한글 초성만 반환합니다.
*/
    def getChosung(str: String): Seq[Char] = {
        if (str == null || str.isEmpty)
            return Seq[Char]()

        val chosungs = new ArrayBuffer[Char](str.length)
        val count = str.length

        for (i <- 0 until count) {
            val char = str.charAt(i)

            if (0xAC00 < char && char < 0xD7AF) {
                val initIdx = char - 0xAC00

                val jongIdx = initIdx % 28
                val jungIdx = ((initIdx - jongIdx) / 28) % 21
                val choIdx = ((initIdx / 28) - jungIdx) / 21

                chosungs += CHO_SUNG(choIdx)
            }
        }
        chosungs
    }

}
