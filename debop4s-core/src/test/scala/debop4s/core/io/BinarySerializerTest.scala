package debop4s.core.io

/**
 * debop4s.core.tests.io.BinarySerializerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:06
 */
class BinarySerializerTest extends AbstractSerializerTest {

  override val serializer: Serializer = new BinarySerializer

}
