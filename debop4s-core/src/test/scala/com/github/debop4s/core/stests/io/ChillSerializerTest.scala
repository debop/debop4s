package com.github.debop4s.core.stests.io

import com.github.debop4s.core.io.{Serializer, ChillSerializer}

/**
 * ChillSerializerTest
 * Created by debop on 2014. 3. 23.
 */
class ChillSerializerTest extends AbstractSerializerTest {

    val _serializer = new ChillSerializer()

    override def serializer: Serializer = _serializer
}
