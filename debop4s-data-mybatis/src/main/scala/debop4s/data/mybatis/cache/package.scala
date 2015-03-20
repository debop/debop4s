package debop4s.data.mybatis

import debop4s.data.mybatis.mapping.T
import org.apache.ibatis.cache.decorators._
import org.apache.ibatis.cache.impl.PerpetualCache

/**
 * Provides Cache supporting types and objects
 *
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
package object cache {

  type Cache = org.apache.ibatis.cache.Cache

  val DefaultCache = T[PerpetualCache]

  /**
   * Default eviction policies
   */
  object Eviction {

    /**
     * Least Recently Used: Remove objects that haven't been used for the longest period of time.
     */
    val LRU = T[LruCache]

    /**
     * First In First Out: Removes objects in the order that they entered the cache.
     */
    val FIFO = T[FifoCache]

    /**
     * Soft Reference: Removes objects based on the garbage collector state and the rules of Soft References.
     */
    val SOFT = T[SoftCache]

    /**
     * Weak Reference: More aggressively than Soft, remove objects based on the garbage collector state and rules of Weak References.
     */
    val WEAK = T[WeakCache]
  }

}
