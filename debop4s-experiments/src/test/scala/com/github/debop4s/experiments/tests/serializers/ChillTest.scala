package com.github.debop4s.experiments.tests.serializers

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import com.twitter.chill.ScalaKryoInstantiator

/**
 * ChillTest
 * Created by debop on 2014. 3. 22.
 */
class ChillTest extends AbstractExperimentTest {

    test("chill simple test") {
        val data = ChillSampleClass("debop", 47, 176.6f)

        val bytes = ScalaKryoInstantiator.defaultPool.toBytesWithClass(data)
        val converted = ScalaKryoInstantiator.defaultPool.fromBytes(bytes).asInstanceOf[ChillSampleClass]

        assert(converted == data)
    }
}

case class ChillSampleClass(name: String, age: Int, score: Float)
