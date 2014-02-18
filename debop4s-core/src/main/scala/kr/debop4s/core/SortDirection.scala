package kr.debop4s.core

/**
 * 정렬 방법
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:25
 */
object SortDirection extends Enumeration {

    type SortDirection = Value

    val ASC = Value(1, "ASC")

    val DESC = Value(-1, "DESC")
}
