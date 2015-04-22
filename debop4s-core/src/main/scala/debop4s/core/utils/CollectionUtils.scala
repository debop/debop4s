package debop4s.core.utils

import scala.collection.mutable
import scala.math.Ordering

/**
 * Collection 관련 Helper Class
 *
 * @author Sunghyouk Bae
 */
object CollectionUtils {

  def sortWith[K, V](map: Map[K, V])(compare: ((K, V), (K, V)) => Boolean): mutable.LinkedHashMap[K, V] = {
    mutable.LinkedHashMap(map.toSeq.sortWith(compare): _*)
  }

  def sortAscByKey[K, V](map: Map[K, V])(implicit ord: Ordering[K]): mutable.LinkedHashMap[K, V] = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._1): _*)
  }

  def sortDescByKey[K, V](map: Map[K, V])(implicit ord: Ordering[K]): mutable.LinkedHashMap[K, V] = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._1).reverse: _*)
  }

  def sortAscByValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]): mutable.LinkedHashMap[K, V] = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._2): _*)
  }

  def sortDescByValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]): mutable.LinkedHashMap[K, V] = {
    mutable.LinkedHashMap(map.toSeq.sortBy(_._2).reverse: _*)
  }

  def maxValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]): (K, V) = {
    map.maxBy[V](_._2)
  }

  def minValue[K, V](map: Map[K, V])(implicit ord: Ordering[V]): (K, V) = {
    map.minBy[V](_._2)
  }
}
