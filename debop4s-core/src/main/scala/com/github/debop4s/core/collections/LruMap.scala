package com.github.debop4s.core.collections

import java.util
import org.apache.commons.collections.map.LRUMap
import scala.collection.convert.Wrappers.JMapWrapper
import scala.collection.mutable

/**
 * LruMap
 * Created by debop on 2014. 4. 5.
 */
class LruMap[K, V](val maxSize: Int, underlying: util.Map[K, V]) extends JMapWrapper[K, V](underlying) {
  def this(maxSize: Int) = this(maxSize, LruMap.makeUnderlying(maxSize))
}

object LruMap {
  def makeUnderlying[K, V](maxSize: Int) = new LRUMap(maxSize).asInstanceOf[util.Map[K, V]]
}

class SynchronizedLruMap[K, V](maxSize: Int, underlying: util.Map[K, V])
  extends LruMap[K, V](maxSize, util.Collections.synchronizedMap(underlying))
  with mutable.SynchronizedMap[K, V] {

  def this(maxSize: Int) = this(maxSize, LruMap.makeUnderlying(maxSize))
}
