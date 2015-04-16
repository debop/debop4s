package debop4s.core.io

/**
 * FstSerializerFunSuite
 * @author Sunghyouk Bae
 */
class FstSerializerFunSuite extends AbstractSerializerFunSuite {

  override val serializer: Serializer = new FstSerializer()
}
