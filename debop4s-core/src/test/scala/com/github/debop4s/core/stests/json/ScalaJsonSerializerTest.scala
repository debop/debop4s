package com.github.debop4s.core.stests.json

import com.github.debop4s.core.json.{Json4sSerializer, JsonSerializer, ScalaJacksonSerializer}
import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.stests.model.User

/**
 * ScalaJsonSerializer
 * Created by debop on 2014. 2. 22.
 */
class ScalaJsonSerializerTest extends AbstractCoreTest {

    val serializers = Array[JsonSerializer](
        ScalaJacksonSerializer(),
        Json4sSerializer()
    )

    val user = User(10)

    test("scala jsonserialize / deserialize") {
        serializers.foreach { serializer =>
            println(s"JsonSerializer=${serializer.getClass}")

            val text = serializer.serializeToText(user)
            val deserializedUser = serializer.deserializeFromText(text, classOf[User])

            log.debug(s"text=$text")

            deserializedUser should equal(user)
        }
    }
}
