package com.github.debop4s.core.utils

/**
 * com.github.debop4s.core.tools.ClosableStopwatch
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:54
 */
class ClosableStopwatch(msg: String = "", runGC: Boolean = false) extends Stopwatch(msg, runGC) with AutoCloseable {

  start()

  def close() {
    stop()
  }
}
