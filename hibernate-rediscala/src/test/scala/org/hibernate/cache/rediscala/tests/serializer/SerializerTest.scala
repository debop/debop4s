package org.hibernate.cache.rediscala.tests.serializer

import java.util.Date
import org.hibernate.cache.rediscala.serializer.{FstRedisSerializer, SnappyRedisSerializer, BinaryRedisSerializer}
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
    var snappy = new SnappyRedisSerializer[AnyRef](new BinaryRedisSerializer[AnyRef]())
    var fst = new FstRedisSerializer[AnyRef]()
    var fst_snappy = new SnappyRedisSerializer[AnyRef](new FstRedisSerializer[AnyRef]())

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

    test("Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = snappy.serialize(smallPerson)
            val loaded = snappy.deserialize(bytes)

            val bytes2 = snappy.serialize(largePerson)
            val loaded2 = snappy.deserialize(bytes2)
        }

        stopwatch("snappy-small", 100) {
            val bytes = snappy.serialize(smallPerson)
            val loaded = snappy.deserialize(bytes)
            // assert(smallPerson == loaded)
        }

        stopwatch("snappy-long", 100) {
            val bytes = snappy.serialize(largePerson)
            val loaded = snappy.deserialize(bytes)
            // assert(largePerson == loaded)
        }
    }

    test("FST Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = fst.serialize(smallPerson)
            val loaded = fst.deserialize(bytes)

            val bytes2 = fst.serialize(largePerson)
            val loaded2 = fst.deserialize(bytes2)
        }

        stopwatch("FST-small", 100) {
            val bytes = fst.serialize(smallPerson)
            val loaded = fst.deserialize(bytes)
            // assert(smallPerson == loaded)
        }

        stopwatch("FST-long", 100) {
            val bytes = fst.serialize(largePerson)
            val loaded = fst.deserialize(bytes)
            // assert(largePerson == loaded)
        }
    }

    test("FST-Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = fst_snappy.serialize(smallPerson)
            val loaded = fst_snappy.deserialize(bytes)

            val bytes2 = fst_snappy.serialize(largePerson)
            val loaded2 = fst_snappy.deserialize(bytes2)
        }

        stopwatch("FST-Snappy-small", 100) {
            val bytes = fst_snappy.serialize(smallPerson)
            val loaded = fst_snappy.deserialize(bytes)
            // assert(smallPerson == loaded)
        }

        stopwatch("FST-Snappy-long", 100) {
            val bytes = fst_snappy.serialize(largePerson)
            val loaded = fst_snappy.deserialize(bytes)
            // assert(largePerson == loaded)
        }
    }

    private def stopwatch(title: String, count: Int)(block: => Unit) {
        val start = System.nanoTime()
        for (x <- 0 until count) {
            block
        }
        val elapsed = System.nanoTime() - start
        println(s"$title : ${elapsed / 1000}")
    }

}
