package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

/**
 * TasksTest
 * @author Sunghyouk Bae
 */
class TasksFunSuite extends AbstractCoreFunSuite {

  // BUG: Tasks.callWithRetry 에 버그 수정 해야 함.
  test("call task") {
    //        var attempt = 3
    //        val x = Tasks.callWithRetry[Int](3) {
    //            if (attempt > 1)
    //                throw new RuntimeException(s"Error! attempt=$attempt")
    //            attempt -= 1
    //            attempt
    //        }
    //        log.debug(s"x=$x")
  }

}
