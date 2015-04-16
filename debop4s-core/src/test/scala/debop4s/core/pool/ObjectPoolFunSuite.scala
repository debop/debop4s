package debop4s.core.pool

import java.util.Properties

import debop4s.core._
import debop4s.core.parallels.Parallels

/**
 * debop4s.core.tests.pool.ObjectPoolFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:53
 */
class ObjectPoolFunSuite extends AbstractCoreFunSuite {

  def getProperties: Properties = {
    val props = new Properties()
    props.setProperty("pool.name", "scalaObjectPool")
    props.setProperty("pool.intValue", "100")
    props.setProperty("pool.uriValue", "http://localhost")

    props
  }

  test("return object") {
    val props = getProperties
    var pool = new ObjectPool(new ObjectPoolConfig(), props)
    try {
      val po = pool.getResource
      po should not equal null
      po.name = "newName"
      assert(po.name == "newName")

      pool.returnResource(po)
      pool.destroy()

      Thread.sleep(10)

      pool = new ObjectPool(new ObjectPoolConfig(), props)
      val po2 = pool.getResource

      po2 should not equal null
      po2.isActive should equal(true)
      po2.name should equal(props.getProperty("pool.name"))

      pool.returnResource(po2)

    } finally {
      pool.destroy()
    }
  }

  test("multithread test") {
    val props = getProperties
    val name = props.getProperty("pool.name")

    using(new ObjectPool(new ObjectPoolConfig(), props)) { pool =>
      Parallels.callFunction1(100) { x =>
        val po = pool.getResource
        po should not equal null
        po.isActive should equal(true)
        po.name should equal(name)

        if (x % 5 == 0) {
          po.name = "NewValue-" + x.toString
          pool.returnBrokenResource(po)
          log.trace("return Broken Resource")
        } else {
          pool.returnResource(po)
          log.trace("return Resource")
        }
      }
    }
  }

}
