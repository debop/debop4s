package debop4s.core.io

import org.slf4j.LoggerFactory

/**
 * FstSerializerTest
 * @author Sunghyouk Bae
 */
class FstSerializerTest extends AbstractSerializerTest {

    override lazy val log = LoggerFactory.getLogger(getClass)

    val _serializer = new FstSerializer()

    override def serializer: Serializer = _serializer
}
