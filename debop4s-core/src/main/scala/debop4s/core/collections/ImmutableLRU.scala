package debop4s.core.collections

import scala.collection.SortedMap

/**
 * 변경이 불가능한 LRU cache
 */
object ImmutableLRU {
  def apply[K, V](maxSize: Int): ImmutableLRU[K, V] = {
    new ImmutableLRU(maxSize, 0, Map.empty[K, (Long, V)], SortedMap.empty[Long, K])
  }
}

/**
 * "map" is the backing store used to stay key->(index,value)
 * pairs. The index tracks the access time for a particular key. "ord"
 * is used to determine the Least-Recently-Used key in "map" by taking
 * the minimum index.
 */
class ImmutableLRU[@miniboxed K, @miniboxed V] private(maxSize: Int,
                                                       idx: Long,
                                                       map: Map[K, (Long, V)],
                                                       ord: SortedMap[Long, K]) {

  // Scala's SortedMap requires a key ordering; ImmutableLRU doesn't
  // care about pulling a minimum value out of the SortedMap, so the
  // following keyOrd treats every value as equal.
  protected implicit val keyOrd = new Ordering[K] {def compare(l: K, r: K) = 0 }

  def size: Int = map.size
  def keySet: Set[K] = map.keySet

  // Put in and return the Key it evicts and the new LRU
  def +(kv: (K, V)): (Option[K], ImmutableLRU[K, V]) = {
    require(kv != null)
    val (key, value) = kv
    val newIdx = idx + 1
    val newMap = map + (key ->(newIdx, value))

    // now update the ordered cache
    val baseOrd = map.get(key).fold(ord)({ case (id, _) => ord - id })
    val ordWithNewKey = baseOrd + (newIdx -> key)

    // do we need to remove an old key
    val (evicts, finalMap, finalOrd) =
      if (ordWithNewKey.size > maxSize) {
        val (minIdx, eKey) = ordWithNewKey.min
        (Some(eKey), newMap - eKey, ordWithNewKey - minIdx)
      } else {
        (None, newMap, ordWithNewKey)
      }

    (evicts, new ImmutableLRU[K, V](maxSize, newIdx, finalMap, finalOrd))
  }

  def get(k: K): (Option[V], ImmutableLRU[K, V]) = {
    val (optionalValue, lru) = remove(k)
    val newLru = optionalValue.fold(lru)(v => (lru + (k -> v))._2)

    (optionalValue, newLru)
  }

  // If the key is present in the cache, returns the pair of
  // Some(value) and the cache with the key removed. Else, returns
  // None and the unmodified cache.
  def remove(k: K): (Option[V], ImmutableLRU[K, V]) = {
    map.get(k).map { case (kidx, v) =>
      val newMap = map - k
      val newOrd = ord - kidx
      // Note we don't increase the idx on a remove, only on put
      (Some(v), new ImmutableLRU[K, V](maxSize, idx, newMap, newOrd))
    }.getOrElse((None, this))
  }

  override def toString: String = s"ImmutableLRU(${ map.toList.mkString(",") })"
}