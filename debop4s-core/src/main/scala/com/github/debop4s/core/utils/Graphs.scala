package com.github.debop4s.core.utils

import org.slf4j.LoggerFactory
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 * Graph 알고리즘에 해당하는 메소드를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 11:08
 */
object Graphs {

    private lazy val log = LoggerFactory.getLogger(getClass)

    /**
    * 폭 우선 탐색을 수행합니다.
    * @param source 시작 노드
    * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
    */
    @inline
    def breadthFirstScan[T](source: T, getAdjacent: T => Iterable[T]): Seq[T] = {
        require(source != null)
        require(getAdjacent != null)

        val toScan = new mutable.SynchronizedQueue[T]()
        toScan += source
        val scanned = mutable.HashSet[T]()

        while (toScan.size > 0) {
            val current = toScan.dequeue()
            scanned += current

            getAdjacent(current)
            .filter(x => !scanned.contains(x))
            .foreach(x => toScan.enqueue(x))
        }
        scanned.toSeq
    }

    /**
    * 폭 우선 탐색을 비동기 방식으로 수행합니다.
    * @param source 시작 노드
    * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
    */
    def breathFirstScanAsync[T](source: T, getAdjacent: T => Iterable[T]): Future[Seq[T]] = future {
        breadthFirstScan(source, getAdjacent)
    }

    /**
    * 깊이 우선 탐색을 수행합니다.
    * @param source 시작 노드
    * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
    */
    @inline
    def depthFirstScan[T](source: T, getAdjacent: T => Iterable[T]): Seq[T] = {
        require(source != null)
        require(getAdjacent != null)

        val toScan = new mutable.SynchronizedStack[T]()
        toScan.push(source)

        val scanned = mutable.HashSet[T]()

        while (toScan.size > 0) {
            val current = toScan.pop()
            scanned += current

            getAdjacent(current)
            .filter(x => !scanned.contains(x))
            .foreach(toScan.push)
        }
        scanned.toSeq
    }

    /**
    * 깊이 우선 탐색을 비동기 방식으로 수행합니다.
    * @param source 시작 노드
    * @param getAdjacent 노드의 근처 노드들 (다음으로 탐색할 노드들)
    */
    def depthFirstScanAsync[T](source: T, getAdjacent: T => Iterable[T]): Future[Seq[T]] = future {
        depthFirstScan(source, getAdjacent)
    }
}
