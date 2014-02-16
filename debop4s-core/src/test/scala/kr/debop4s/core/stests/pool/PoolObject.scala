package kr.debop4s.core.stests.pool

import java.net.URI

/**
 * kr.debop4s.core.tests.pool.PoolObject
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:41
 */
class PoolObject(var name: String, var intValue: Int, var uriValue: URI) {

  // 생성 시간 소요를 위해
  Thread.sleep(100)

  var isActive: Boolean = true
}
