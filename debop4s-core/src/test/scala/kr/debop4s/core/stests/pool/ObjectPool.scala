package kr.debop4s.core.stests.pool

import java.util.Properties
import kr.debop4s.core.pool.{AbstractObjectPoolConfig, AbstractObjectPool}

/**
 * kr.debop4s.core.tests.pool.ObjectPool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:40
 */
class ObjectPool(cfg: AbstractObjectPoolConfig, val props: Properties)
  extends AbstractObjectPool[PoolObject](cfg, new ObjectFactory(props)) {

}


