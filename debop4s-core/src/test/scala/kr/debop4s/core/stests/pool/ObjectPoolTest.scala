package kr.debop4s.core.stests.pool

import java.util.Properties
import kr.debop4s.core.logging.Logger
import kr.debop4s.core.parallels.Parallels
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
 * kr.debop4s.core.tests.pool.ObjectPoolTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:53
 */
class ObjectPoolTest extends AssertionsForJUnit {

    lazy val log = Logger[ObjectPoolTest]

    def getProperties(): Properties = {
        val props = new Properties()
        props.setProperty("pool.name", "scalaObjectPool")
        props.setProperty("pool.intValue", "100")
        props.setProperty("pool.uriValue", "http://localhost")

        props
    }

    @Test
    def returnObject() {
        val props = getProperties()
        var pool = new ObjectPool(new ObjectPoolConfig(), props)
        try {
            val po = pool.getResource
            assert(po != null)
            po.name = "newName"
            assert(po.name === "newName")

            pool.returnResource(po)
            pool.destroy()

            Thread.sleep(10)

            pool = new ObjectPool(new ObjectPoolConfig(), props)
            val po2 = pool.getResource
            assert(po2 != null)
            assert(po2.isActive)
            assert(po2.name === props.getProperty("pool.name"))
            pool.returnResource(po2)

        } finally {
            pool.destroy()
        }
    }

    @Test
    def multi() {
        val props = getProperties()
        val name = props.getProperty("pool.name")

        val pool = new ObjectPool(new ObjectPoolConfig(), props)
        try {
            Parallels.callFunction1(100)(x => {
                val po = pool.getResource
                assert(po != null)
                assert(po.isActive)
                assert(po.name === name)

                if (x % 5 == 0) {
                    po.name = "NewValue-" + x.toString
                    pool.returnBrokenResource(po)
                    log.trace("return Broken Resource")
                } else {
                    pool.returnResource(po)
                    log.trace("return Resource")
                }
            })
        }
        finally {
            pool.destroy()
        }
    }

}
