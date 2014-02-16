package kr.debop4s.core.utils

import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
 * Graph 알고리즘에 해당하는 메소드를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 11:08
 */
object Graphs {

  lazy val log = LoggerFactory.getLogger(getClass)

  def breadthFirstScan[T](source: T, getAdjacent: T => Iterable[T]): collection.Set[T] = {
    require(source != null)
    require(getAdjacent != null)

    val toScan = mutable.Queue[T](source)
    val scanned = mutable.HashSet[T]()

    while (toScan.size > 0) {
      val current = toScan.dequeue()
      scanned += current
      for (node <- getAdjacent(current) if !scanned.contains(node)) {
        toScan.enqueue(node)
      }
    }
    scanned
  }

  def depthFirstScan[T](source: T, getAdjacent: T => Iterable[T]): collection.Set[T] = {
    require(source != null)
    require(getAdjacent != null)

    val toScan = mutable.Stack[T](source)
    val scanned = mutable.HashSet[T]()

    while (toScan.size > 0) {
      val current = toScan.pop()
      scanned += current
      for (node <- getAdjacent(current) if !scanned.contains(node)) {
        toScan.push(node)
      }
    }
    scanned
  }

}
