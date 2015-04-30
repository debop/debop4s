package debop4s.core.utils

import debop4s.core.Logging

/**
 * ScalaMaps
 * Created by debop on 2014. 1. 30.
 */
object ScalaMaps extends Logging {

  def sortByKey[@miniboxed K <: Ordered[K], @miniboxed V](m: Map[K, V]): Map[K, V] =
    m.toList.sortBy(_._1).toMap
  // m.toList.sortWith(_._1 < _._1).toMap

  def sortByKeyDescending[@miniboxed K <: Ordered[K], @miniboxed V](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._1 > _._1).toMap


  def sortByValue[@miniboxed K, @miniboxed V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortBy(_._2).toMap
  // m.toList.sortWith(_._2 < _._2).toMap

  def sortByValueDescending[@miniboxed K, @miniboxed V <: Ordered[V]](m: Map[K, V]): Map[K, V] =
    m.toList.sortWith(_._2 > _._2).toMap
}
