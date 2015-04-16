package debop4s.rediscala.serializer

import scala.collection.mutable

/**
 * FstRedisSerializerFunSuite
 * @author Sunghyouk Bae
 */
class FstRedisSerializerFunSuite extends AbstractRedisSerializerSuite[mutable.HashSet[String]] {

  override val serializer = new FstRedisSerializer[mutable.HashSet[String]]()

  override def createEmpty() = mutable.HashSet[String]()

  override def createSample() = {
    val data = createEmpty()
    data ++= Set("a", "b", "c")
  }
}
