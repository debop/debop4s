package debop4s.core.utils

import java.lang.{Iterable => JIterable}
import java.util
import java.util.concurrent.{ConcurrentLinkedQueue, LinkedBlockingDeque}

import debop4s.core.{Func1, Logging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Graph 알고리즘에 해당하는 메소드를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 11:08
 */
object Graphs extends Logging {

  /**
   * 폭 우선 탐색을 수행합니다.
   * @param source 시작 노드
   * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
   */
  @inline
  def breadthFirstScan[T](source: T, getAdjacent: T => JIterable[T]): JIterable[T] = {
    require(source != null)
    require(getAdjacent != null)

    val toScan = new ConcurrentLinkedQueue[T]()
    toScan.add(source)
    val scanned = new util.HashSet[T]()

    while (toScan.size > 0) {
      val current = toScan.poll()
      log.trace(s"scanned $current")
      scanned.add(current)

      val it = getAdjacent(current).iterator()
      while (it.hasNext) {
        val x = it.next()
        if (!scanned.contains(x)) {
          toScan.add(x)
        }
      }
    }
    scanned
  }

  def breadthFirstScanJava[T](source: T, getAdjacent: Func1[T, JIterable[T]]): JIterable[T] =
    breadthFirstScan[T](source, (x: T) => getAdjacent.execute(x))

  /**
   * 폭 우선 탐색을 비동기 방식으로 수행합니다.
   * @param source 시작 노드
   * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
   */
  def breathFirstScanAsync[T](source: T, getAdjacent: T => JIterable[T]): Future[JIterable[T]] =
    Future {
      breadthFirstScan(source, getAdjacent)
    }

  /**
   * 깊이 우선 탐색을 수행합니다.
   * @param source 시작 노드
   * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
   */
  @inline
  def depthFirstScan[T](source: T, getAdjacent: T => JIterable[T]): JIterable[T] = {
    require(source != null)
    require(getAdjacent != null)

    val toScan = new LinkedBlockingDeque[T]()
    toScan.add(source)

    val scanned = new util.HashSet[T]()

    while (toScan.size > 0) {
      val current = toScan.pop()
      log.trace(s"scanned $current")
      scanned.add(current)

      val it = getAdjacent(current).iterator()
      while (it.hasNext) {
        val x = it.next
        if (!scanned.contains(x)) {
          toScan.push(x)
        }
      }
    }
    scanned
  }

  /**
   * 깊이 우선 탐색을 비동기 방식으로 수행합니다.
   * @param source 시작 노드
   * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
   */
  @inline
  def depthFirstScanJava[T](source: T, getAdjacent: Func1[T, JIterable[T]]): JIterable[T] =
    depthFirstScan[T](source, (x: T) => getAdjacent.execute(x))

  /**
   * 깊이 우선 탐색을 비동기 방식으로 수행합니다.
   * @param source 시작 노드
   * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
   */
  def depthFirstScanAsync[T](source: T, getAdjacent: T => JIterable[T]): Future[JIterable[T]] =
    Future {
      depthFirstScan(source, getAdjacent)
    }
}

