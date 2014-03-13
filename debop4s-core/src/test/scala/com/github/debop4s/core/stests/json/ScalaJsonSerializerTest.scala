package com.github.debop4s.core.stests.json

import com.github.debop4s.core.json.{JacksonSerializer, JsonSerializer, ScalaJacksonSerializer}
import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.stests.model.User

/**
 * ScalaJsonSerializer
 * Created by debop on 2014. 2. 22.
 */
class ScalaJsonSerializerTest extends AbstractCoreTest {

    // NOTE: Gson 은 scala 를 지원하지 않는다.
    val serializers = Array[JsonSerializer](ScalaJacksonSerializer(), JacksonSerializer())

    val user = User(10)

    test("scala jsonserialize / deserialize") {
        serializers.foreach {
            serializer =>

                println(s"JsonSerializer=${serializer.getClass }")

                val bytes = serializer.serialize(user)
                val deserializedUser = serializer.deserialize(bytes, classOf[User])

                deserializedUser should equal(user)
        }
    }
}
