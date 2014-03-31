package com.github.debop4s.experiments.tests.json

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import net.liftweb.json._
import net.liftweb.json.ext.JodaTimeSerializers
import org.joda.time.DateTime


case class Person(name: String, birthday: DateTime, address: Address)

case class Address(city: String, state: String)

class LiftJsonTest extends AbstractExperimentTest {

    implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

    test("using lift-json") {
        val p = Person("Sunghyouk Bae", new DateTime(1968, 10, 14, 0, 0), Address("Seoul", "KR"))
        val jsonString = Serialization.write[Person](p)
        println(jsonString)

        val loaded = Serialization.read[Person](jsonString)

        assert(loaded == p)

    }

}
