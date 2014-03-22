package com.github.debop4s.experiments.tests.serializers

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import scala.pickling._
import scala.pickling.binary._
import scala.reflect.ClassTag

/**
 * PicklingTest
 * Created by debop on 2014. 3. 22.
 */
class PicklingTest extends AbstractExperimentTest {

    test("scala pickling basic sample") {
        val person = Person("John Oliver", 36)
        val json = person.pickle
        println(s"Pickling person = $json")

        val converted = json.unpickle[Person]
        assert(converted == person)
    }

    test("scala serialize/deserialize by Scala-Pickle") {
        val pickler = new BinaryPickler[Person]()

        val person = Person("배성혁", 46)

        val bytes = person.pickle.value
        val converted = bytes.unpickle[Person]

        assert(converted == person)
    }
}

case class Person(name: String, age: Int)

class BinaryPickler[T <: Person : ClassTag] {

    implicit val format: BinaryPickleFormat = scala.pickling.binary.pickleFormat

    //    def serialize(graph: T) = {
    //        graph.pickle(format).value
    //    }

    //    def deserialize(bytes: Array[Byte]): T = {
    //        bytes.unpickle[T]
    //    }


}
