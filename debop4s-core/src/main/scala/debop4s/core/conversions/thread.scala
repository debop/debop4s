package debop4s.core.conversions

import java.util.concurrent.Callable

/**
 * Implicits for turning a block of code into a Runnable or Callable.
 * Created by debop on 2014. 4. 5.
 */
object thread {

    implicit def makeRunnable(f: => Unit): Runnable = new Runnable() {
        def run() = f
    }

    implicit def makeCallabke[T](f: => T): Callable[T] = new Callable[T]() {
        def call() = f
    }
}
