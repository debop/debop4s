package org.hibernate.cache.rediscala.tests.serializer

import java.util.Date
import org.hibernate.cache.rediscala.serializer._
import org.hibernate.cache.rediscala.tests.domain.{Event, Person}
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
 * SerializerTest
 * Created by debop on 2014. 3. 14.
 */
class SerializerTest extends FunSuite with BeforeAndAfter {

    val SMALL_LOOP = 1000
    val LARGE_LOOP = 1000

    var smallPerson: Person = null
    var largePerson: Person = null

    val binary = new BinaryRedisSerializer[AnyRef]()
    val snappy = new SnappyRedisSerializer[AnyRef](new BinaryRedisSerializer[AnyRef]())
    val fst = new FstRedisSerializer[AnyRef]()
    val fst_snappy = new SnappyRedisSerializer[AnyRef](new FstRedisSerializer[AnyRef]())
    val chill = new ChillRedisSerializer[Person]()
    val chill_snappy = new SnappyRedisSerializer[Person](new ChillRedisSerializer[Person]())
    val gridgain = new GridGainRedisSerializer[Person]()
    val gridgain_snappy = new SnappyRedisSerializer[Person](new GridGainRedisSerializer[Person]())

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

        stopwatch("binary-small", SMALL_LOOP) {
            val bytes = binary.serialize(smallPerson)
            val loaded = binary.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("binary-long", LARGE_LOOP) {
            val bytes = binary.serialize(largePerson)
            val loaded = binary.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = snappy.serialize(smallPerson)
            val loaded = snappy.deserialize(bytes)

            val bytes2 = snappy.serialize(largePerson)
            val loaded2 = snappy.deserialize(bytes2)
        }

        stopwatch("snappy-small", SMALL_LOOP) {
            val bytes = snappy.serialize(smallPerson)
            val loaded = snappy.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("snappy-long", LARGE_LOOP) {
            val bytes = snappy.serialize(largePerson)
            val loaded = snappy.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("FST Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = fst.serialize(smallPerson)
            val loaded = fst.deserialize(bytes)

            val bytes2 = fst.serialize(largePerson)
            val loaded2 = fst.deserialize(bytes2)
        }

        stopwatch("FST-small", SMALL_LOOP) {
            val bytes = fst.serialize(smallPerson)
            val loaded = fst.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("FST-long", LARGE_LOOP) {
            val bytes = fst.serialize(largePerson)
            val loaded = fst.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("FST-Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = fst_snappy.serialize(smallPerson)
            val loaded = fst_snappy.deserialize(bytes)

            val bytes2 = fst_snappy.serialize(largePerson)
            val loaded2 = fst_snappy.deserialize(bytes2)
        }

        stopwatch("FST-Snappy-small", SMALL_LOOP) {
            val bytes = fst_snappy.serialize(smallPerson)
            val loaded = fst_snappy.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("FST-Snappy-long", LARGE_LOOP) {
            val bytes = fst_snappy.serialize(largePerson)
            val loaded = fst_snappy.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("Chill Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = chill.serialize(smallPerson)
            val loaded = chill.deserialize(bytes)

            val bytes2 = chill.serialize(largePerson)
            val loaded2 = chill.deserialize(bytes2)
        }

        stopwatch("Chill-small", SMALL_LOOP) {
            val bytes = chill.serialize(smallPerson)
            val loaded = chill.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("Chill-long", LARGE_LOOP) {
            val bytes = chill.serialize(largePerson)
            val loaded = chill.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("Chill-Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = chill_snappy.serialize(smallPerson)
            val loaded = chill_snappy.deserialize(bytes)

            val bytes2 = chill_snappy.serialize(largePerson)
            val loaded2 = chill_snappy.deserialize(bytes2)
        }

        stopwatch("Chill-Snappy-small", SMALL_LOOP) {
            val bytes = chill_snappy.serialize(smallPerson)
            val loaded = chill_snappy.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("Chill-Snappy-long", LARGE_LOOP) {
            val bytes = chill_snappy.serialize(largePerson)
            val loaded = chill_snappy.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("GridGain Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = gridgain.serialize(smallPerson)
            val loaded = gridgain.deserialize(bytes)

            val bytes2 = gridgain.serialize(largePerson)
            val loaded2 = gridgain.deserialize(bytes2)
        }

        stopwatch("GridGain-small", SMALL_LOOP) {
            val bytes = gridgain.serialize(smallPerson)
            val loaded = gridgain.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("GridGain-long", LARGE_LOOP) {
            val bytes = gridgain.serialize(largePerson)
            val loaded = gridgain.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }

    test("GridGain-Snappy Serializer benchmark") {
        stopwatch("warn-up", 1) {
            val bytes = gridgain_snappy.serialize(smallPerson)
            val loaded = gridgain_snappy.deserialize(bytes)

            val bytes2 = gridgain_snappy.serialize(largePerson)
            val loaded2 = gridgain_snappy.deserialize(bytes2)
        }

        stopwatch("GridGain-Snappy-small", SMALL_LOOP) {
            val bytes = gridgain_snappy.serialize(smallPerson)
            val loaded = gridgain_snappy.deserialize(bytes)
            assert(smallPerson == loaded)
        }

        stopwatch("GridGain-Snappy-long", LARGE_LOOP) {
            val bytes = gridgain_snappy.serialize(largePerson)
            val loaded = gridgain_snappy.deserialize(bytes)
            assert(largePerson == loaded)
        }
    }


    private def stopwatch(title: String, count: Int)(block: => Unit) {
        val start = System.nanoTime()

        //        for (x <- 0 until count) {
        //            block
        //        }

        (0 until count).par.foreach(_ => block)

        val elapsed = System.nanoTime() - start
        println(s"$title : ${elapsed / 1000}")
    }

}
