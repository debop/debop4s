package debop4s.core.collections

import java.util

import org.apache.commons.collections.map.LRUMap

import scala.collection.convert.Wrappers.JMapWrapper

/**
 * LRU (Least Recently Used) Map
 * 최근 사용이 가장 적은 것부터 버리는 알고리즘을 가진 Map 입니다.
 * Created by debop on 2014. 4. 5.
 */
class LruMap[K, V](val maxSize: Int, underlying: util.Map[K, V]) extends JMapWrapper[K, V](underlying) {
  def this(maxSize: Int) = this(maxSize, LruMap.makeUnderlying(maxSize))
}

/**
 * companion object for [[LruMap]]
 */
object LruMap {
  def makeUnderlying[K, V](maxSize: Int): util.Map[K, V] =
    new LRUMap(maxSize).asInstanceOf[util.Map[K, V]]
}

/**
 * Thread-safe 한 LRU (Least Recently Used) Map 입니다.
 * @param maxSize     맵의 최대 크기
 * @param underlying  기반 Map
 */
class SynchronizedLruMap[K, V](maxSize: Int, underlying: util.Map[K, V])
  extends LruMap[K, V](maxSize, util.Collections.synchronizedMap(underlying)) {

  def this(maxSize: Int) = this(maxSize, LruMap.makeUnderlying(maxSize))
}

