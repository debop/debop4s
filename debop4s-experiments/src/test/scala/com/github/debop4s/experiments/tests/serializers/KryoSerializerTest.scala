package com.github.debop4s.experiments.tests.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.github.debop4s.experiments.tests.AbstractExperimentTest
import java.io.ByteArrayOutputStream

/**
 * KyroSerializerTest
 * Created by debop on 2014. 3. 22.
 */
class KryoSerializerTest extends AbstractExperimentTest {

    test("kryo serialize/deserializer test") {

        val kryo = new Kryo()

        val data = KryoSampleClass("debop", 47, 176.6f)
        val bos = new ByteArrayOutputStream()
        val output = new Output(bos)

        kryo.writeObject(output, data)
        output.close()

        val bytes = bos.toByteArray

        val input = new Input(bytes)
        val loaded = kryo.readObject(input, classOf[KryoSampleClass])

        assert(loaded != null)
        assert(loaded == data)
    }

}

case class KryoSampleClass(name: String, age: Int, score: Float) {
    def this() {
        this(null, 0, 0f)
    }
}
