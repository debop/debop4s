package com.github.debop4s.core.stests.io

import com.github.debop4s.core.stests.YearWeek
import org.slf4j.LoggerFactory
import com.github.debop4s.core.io.{Serializer, FstSerializer}
import com.github.debop4s.core.stests.io.model.Company

/**
 * FstSerializerTest
 * @author Sunghyouk Bae
 */
class FstSerializerTest extends AbstractSerializerTest {

    override lazy val log = LoggerFactory.getLogger(getClass)

    val _serializer = new FstSerializer()

    override def serializer: Serializer = _serializer
}
