package kr.debop4s.core.stests.pool

import java.net.URI
import java.util.Properties
import org.apache.commons.pool.BasePoolableObjectFactory

/**
 * kr.debop4s.core.tests.pool.ObjectFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:48
 */
class ObjectFactory(val props: Properties) extends BasePoolableObjectFactory[PoolObject] {

    val name = props.getProperty("pool.name", "name")
    val intValue = Integer.decode(props.getProperty("pool.intValue", "1"))
    val uriValue = URI.create(props.getProperty("pool.uriValue", "http://localhost"))


    override def makeObject(): PoolObject = {
        val po = new PoolObject(name, intValue, uriValue)
        po.isActive = true
        po
    }

    override def destroyObject(obj: PoolObject) {
        val po = obj.asInstanceOf[PoolObject]
        if (po.isActive)
            po.isActive = false
    }

    override def validateObject(obj: PoolObject): Boolean =
        obj.asInstanceOf[PoolObject].isActive

}
