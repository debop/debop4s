package kr.debop4s.core.parallels

import java.util.concurrent.Callable
import kr.debop4s.core.logging.Logger
import scala.collection.JavaConversions._
import scala.concurrent
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Scala 에서 비동기 작업을 수행합니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 10:59
 */
object Asyncs {

    lazy val log = Logger(this.getClass)


    val EMPTY_RUNNABLE = new Runnable {
        def run() {
            /* nothing to do. */
        }
    }

    def newTask[T](callable: Callable[T]): Future[T] = future {callable.call()}

    def newTask[T](function: => T): Future[T] = future {
                                                           function
                                                       }

    def newTask[T](runnable: Runnable, result: T): Future[T] = future {
                                                                          runnable.run()
                                                                          result
                                                                      }

    def newTask(runnable: Runnable) = future {runnable.run()}

    def newTaskBlock[T](result: T)(block: => Unit): Future[T] = future {
                                                                           block
                                                                           result
                                                                       }

    def newTaskBlock(block: => Unit): Future[Unit] = future {
                                                                block
                                                            }

    def startNew[V](callable: Callable[V]): concurrent.Future[V] = future {
                                                                              callable.call()
                                                                          }

    def startNew[V](block: => V): concurrent.Future[V] = future {
                                                                    block
                                                                }

    def startNew[V](runnable: Runnable, result: V): Future[V] = future {
                                                                           runnable.run()
                                                                           result
                                                                       }

    def startNewBlock[V](result: V)(block: => Unit): Future[V] = future {
                                                                            block
                                                                            result
                                                                        }

    def startNewBlock(block: => Unit): Future[Void] = startNewBlock[Void](null)(block)


    def continueTask[T, V](prevTask: Future[T], result: V)(block: T => Unit): Future[V] = future {
                                                                                                     block(Await
                                                                                                               .result(prevTask, 60 seconds))
                                                                                                     result
                                                                                                 }

    def continueTask[T, V](prevTask: Future[T])(block: T => V): Future[V] = future {
                                                                                       block(Await.result(prevTask, 60 seconds))
                                                                                   }

    def getTaskHasResult[T](result: T): Future[T] = newTask(EMPTY_RUNNABLE, result)


    def runAsync[T, R](elements: Iterable[T],
                       function: T => R): List[Future[R]] = {
        assert(function != null)

        elements.map(x => future {
                                     function(x)
                                 }).toList
    }

    def invokeAll[T](tasks: Seq[_ <: Callable[T]]) = {
        getAll(tasks.map(x => future {x.call()}))
    }

    def invokeAll[T](tasks: Seq[_ <: Callable[T]], timeout: Long, unit: TimeUnit) {
        getAll(tasks.map(x => future {x.call()}), timeout, unit)
    }

    def runAll[T](tasks: java.lang.Iterable[_ <: Future[T]]) {
        getAll(tasks)
    }

    def getAll[T](tasks: Iterable[_ <: Future[T]]): Iterable[T] = {
        tasks.map(task => Await.result(task, 60 seconds))
    }

    def getAll[T](tasks: Iterable[_ <: Future[T]], timeout: Long, unit: TimeUnit): Iterable[T] = {
        tasks.map(task => Await.result(task, Duration(timeout, unit)))
    }

    def waitAll[T](futures: Iterable[_ <: Future[T]]) {
        while (!futures.forall(task => task.isCompleted)) {
            Thread.sleep(1)
        }
    }

    def waitAllTasks[T](futures: Iterable[_ <: Future[T]]) {
        while (!futures.forall(task => task.isCompleted)) {
            Thread.sleep(1)
        }
    }

    def result[T](awaitable: Awaitable[T]): T =
        Await.result(awaitable, 60 seconds)

}
