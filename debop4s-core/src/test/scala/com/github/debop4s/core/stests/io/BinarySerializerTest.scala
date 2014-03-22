package com.github.debop4s.core.stests.io

import com.github.debop4s.core.io.{Serializer, BinarySerializer}
import com.github.debop4s.core.stests.io.model.User
import org.scalatest.BeforeAndAfter
import com.github.debop4s.core.stests.YearWeek

/**
 * com.github.debop4s.core.tests.io.BinarySerializerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:06
 */
class BinarySerializerTest extends AbstractSerializerTest {

    val _serializer = new BinarySerializer

    override def serializer: Serializer =
        _serializer

}
