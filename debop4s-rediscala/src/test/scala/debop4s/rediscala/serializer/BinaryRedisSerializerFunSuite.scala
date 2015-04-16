package debop4s.rediscala.serializer

import scala.collection.mutable


/**
 * BinaryRedisSerializerFunSuite
 * @author Sunghyouk Bae
 */
class BinaryRedisSerializerFunSuite extends AbstractRedisSerializerSuite[mutable.HashSet[String]] {

  override val serializer = new BinaryRedisSerializer[mutable.HashSet[String]]()

  override def createEmpty() = mutable.HashSet[String]()

  override def createSample() = {
    val data = createEmpty()
    data ++= Set("a", "b", "c")
  }
}
