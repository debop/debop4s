package org.hibernate.cache.rediscala.tests.serializer

import java.util.Date
import org.hibernate.cache.rediscala.serializer.BinaryRedisSerializer
import org.hibernate.cache.rediscala.tests.domain.{Event, Person}
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
 * SerializerTest
 * Created by debop on 2014. 3. 14.
 */
class SerializerTest extends FunSuite with BeforeAndAfter {

    var smallPerson: Person = _
    var largePerson: Person = _

    val binary = new BinaryRedisSerializer[AnyRef]()

    before {
        smallPerson = new Person()
        smallPerson.lastName = "배성혁"
        smallPerson.firstName = "동해물과 백두산이 마르고 닳도록"

        largePerson = new Person()
        largePerson.lastName = "배성혁" * 10
        largePerson.firstName = "동해물과 백두산이 마르고 닳도록" * 10
        for (x <- 0 until 100) {
            val event = new Event()
            event.title = "이벤트 타이틀 " * 10
            event.date = new Date()
            largePerson.events.add(event)
        }
    }

    test("Binary Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = binary.serialize(smallPerson)
            val loaded = binary.deserialize(bytes)

            val bytes2 = binary.serialize(largePerson)
            val loaded2 = binary.deserialize(bytes2)
        }

        stopwatch("binary-small", 100) {
            val bytes = binary.serialize(smallPerson)
            val loaded = binary.deserialize(bytes)
            // assert(smallPerson == loaded)
        }

        stopwatch("binary-long", 100) {
            val bytes = binary.serialize(largePerson)
            val loaded = binary.deserialize(bytes)
            // assert(largePerson == loaded)
        }
    }


    private def stopwatch(title: String, count: Int)(block: => Unit) {
        val start = System.nanoTime()
        for (x <- 0 until count) {
            block
        }
        val elapsed = System.nanoTime() - start
        println(s"$title : ${elapsed / 1000000}")
    }

}
