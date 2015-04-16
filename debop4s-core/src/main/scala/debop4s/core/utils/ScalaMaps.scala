package debop4s.core.utils

import debop4s.core.Logging

/**
 * ScalaMaps
 * Created by debop on 2014. 1. 30.
 */
object ScalaMaps extends Logging {

  def sortByKey[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._1 < _._1).toMap

  def sortByKeyDescending[K <: Ordered[K], V](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._1 > _._1).toMap


  def sortByValue[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._2 < _._2).toMap

  def sortByValueDescending[K, V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._2 > _._2).toMap
}
