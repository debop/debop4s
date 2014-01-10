package kr.debop4s.core.utils

import org.joda.time.DateTime
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.tools.Converts
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 10:46
 */
object Converts {

    implicit lazy val log = LoggerFactory.getLogger(getClass)

    def toInt(x: Any): Int = {
        try {
            x match {
                case i: Byte => i.toInt
                case i: Int => i
                case i: Long => i.toInt
                case i: Float => i.toInt
                case i: Double => i.toInt
                case _ => x.toString.toInt
            }
        } catch {
            case e: Throwable => 0
        }
    }

    def toLong(x: Any): Long = {
        try {
            x match {
                case i: Byte => i.toLong
                case i: Int => i.toLong
                case i: Long => i
                case i: Float => i.toLong
                case i: Double => i.toLong
                case _ => x.toString.toLong
            }
        } catch {
            case e: Throwable => 0
        }
    }

    def toString(x: Any): String = if (x == null) "" else x.toString

    def toDateTime(millis: Long): DateTime = new DateTime(millis)

}
