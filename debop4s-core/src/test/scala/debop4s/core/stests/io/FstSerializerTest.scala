package debop4s.core.stests.io

import org.slf4j.LoggerFactory
import debop4s.core.io.{Serializer, FstSerializer}

/**
 * FstSerializerTest
 * @author Sunghyouk Bae
 */
class FstSerializerTest extends AbstractSerializerTest {

  override lazy val log = LoggerFactory.getLogger(getClass)

  val _serializer = new FstSerializer()

  override def serializer: Serializer = _serializer
}
