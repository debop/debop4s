package debop4s.data.mybatis.session

import org.apache.ibatis.session.{ExecutorType => ET}

/**
 * ExecutorType
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
trait ExecutorType {

  val unwrap: ET

}

object ExecutorType {
  val SIMPLE = new ExecutorType {
    override val unwrap: ET = ET.SIMPLE
  }

  val REUSE = new ExecutorType {
    override val unwrap: ET = ET.REUSE
  }

  val BATCH = new ExecutorType {
    override val unwrap: ET = ET.BATCH
  }
}
