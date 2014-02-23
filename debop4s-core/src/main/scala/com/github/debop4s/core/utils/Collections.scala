package com.github.debop4s.core.utils

import org.slf4j.LoggerFactory


/**
 * com.github.debop4s.core.tools.Collections
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:37
 */
object Collections {

    private lazy val log = LoggerFactory.getLogger(getClass)

    /**
    * Map의 Key 값으로 정렬합니다.
    */
    def sortByKey[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
        m.toList.sortWith(_._1 < _._1).toMap

    /**
    * Map이 Key로 역순 정렬을 합니다.
    */
    def sortByKeyDescending[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
        m.toList.sortWith(_._1 > _._1).toMap

    /**
    * Map의 Value로 정렬합니다.
    */
    def sortByValue[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
        m.toList.sortWith(_._2 < _._2).toMap

    /**
    * Map의 Value로 역순 정렬합니다.
    */
    def sortByValueDescending[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
        m.toList.sortWith(_._2 > _._2).toMap

}
