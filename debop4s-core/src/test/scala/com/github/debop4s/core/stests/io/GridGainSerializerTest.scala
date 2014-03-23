package com.github.debop4s.core.stests.io

import com.github.debop4s.core.io.{GridGainSerializer, Serializer}

/**
 * GridGainSerializerTest
 * Created by debop on 2014. 3. 23.
 */
class GridGainSerializerTest extends AbstractSerializerTest {

    private val _serializer = new GridGainSerializer()

    override def serializer: Serializer = _serializer
}
