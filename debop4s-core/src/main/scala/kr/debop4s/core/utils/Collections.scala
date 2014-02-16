package kr.debop4s.core.utils

import org.slf4j.LoggerFactory


/**
 * kr.debop4s.core.tools.Collections
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:37
 */
object Collections {

  lazy val log = LoggerFactory.getLogger(getClass)

  def sortByKey[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._1 < _._1).toMap

  def sortByKeyDescending[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._1 > _._1).toMap


  def sortByValue[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._2 < _._2).toMap

  def sortByValueDescending[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._2 > _._2).toMap

}
