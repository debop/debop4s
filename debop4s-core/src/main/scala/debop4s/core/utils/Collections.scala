package debop4s.core.utils

import scala.collection.mutable
import scala.math.Ordering

/**
 * Collections
 * @author Sunghyouk Bae
 */
object Collections {

  def sortWith[K, V](map: Map[K, V])(compare: ((K, V), (K, V)) => Boolean) = {
    mutable.LinkedHashMap(map.toSeq.sortWith(compare): _*)
  }

  def sortAscByKey[K, V](map: Map[K, V])(implicit ord: Ordering[K]) = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._1): _*)
  }

  def sortDescByKey[K, V](map: Map[K, V])(implicit ord: Ordering[K]) = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._1).reverse: _*)
  }

  def sortAscByValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]) = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._2): _*)
  }

  def sortDescByValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]) = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._2).reverse: _*)
  }

  def maxValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]) = {
    map.maxBy[V](_._2)
  }

  def minValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]) = {
    map.minBy[V](_._2)
  }
}
