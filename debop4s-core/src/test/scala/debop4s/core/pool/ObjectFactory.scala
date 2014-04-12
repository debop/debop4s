package debop4s.core.pool

import java.net.URI
import java.util.Properties
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{PooledObject, BasePooledObjectFactory}

/**
 * debop4s.core.tests.pool.ObjectFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:48
 */
class ObjectFactory(val props: Properties) extends BasePooledObjectFactory[PoolObject] {

  val name = props.getProperty("pool.name", "name")
  val intValue = Integer.decode(props.getProperty("pool.intValue", "1"))
  val uriValue = URI.create(props.getProperty("pool.uriValue", "http://localhost"))

  override def create(): PoolObject = {
    val po = new PoolObject(name, intValue, uriValue)
    po.isActive = true
    po
  }

  override def wrap(obj: PoolObject): PooledObject[PoolObject] =
    new DefaultPooledObject[PoolObject](obj)
}
