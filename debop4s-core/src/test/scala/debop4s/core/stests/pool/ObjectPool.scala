package debop4s.core.stests.pool

import debop4s.core.pool.AbstractObjectPool
import java.util.Properties
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.slf4j.LoggerFactory

/**
 * debop4s.core.tests.pool.ObjectPool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:40
 */
class ObjectPool(cfg: GenericObjectPoolConfig, val props: Properties)
  extends AbstractObjectPool[PoolObject](cfg, new ObjectFactory(props)) {

  private lazy val log = LoggerFactory.getLogger(getClass)
}


