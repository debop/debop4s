package org.hibernate.cache.rediscala.serializer

import java.util.Date

import org.hibernate.cache.rediscala.domain.{Event, Person}
import org.scalameter.{Gen, PerformanceTest}
import org.scalatest.junit.AssertionsForJUnit

/**
 * SerializerBenchmark
 * @author Sunghyouk Bae
 */
object SerializerBenchmark extends PerformanceTest.Quickbenchmark with AssertionsForJUnit {

  val sizes = Gen.range("size")(1000, 10000, 1000)

  val serializers = Gen.enumeration("serializers")(
                                                    new BinaryRedisSerializer[AnyRef](),
                                                    new FstRedisSerializer[AnyRef](),
                                                    new SnappyRedisSerializer[AnyRef](new BinaryRedisSerializer[AnyRef]()),
    new SnappyRedisSerializer[AnyRef](new FstRedisSerializer[AnyRef]()),
    new LZ4RedisSerializer[Any](new BinaryRedisSerializer[Any]),
    new LZ4RedisSerializer[Any](new FstRedisSerializer[Any])
                                                  )


  var smallPerson = new Person()
  smallPerson.lastName = "배성혁"
  smallPerson.firstName = "동해물과 백두산이 마르고 닳도록"

  var largePerson: Person = _
  largePerson = new Person()
  largePerson.lastName = "배성혁" * 10
  largePerson.firstName = "동해물과 백두산이 마르고 닳도록" * 10
  var x = 0
  while (x < 100) {
    val event = new Event()
    event.title = "이벤트 타이틀 " * 10
    event.date = new Date()
    largePerson.events.add(event)
    x += 1
  }
  val persons = Gen.enumeration("persons")(smallPerson, largePerson)

  val inputs = Gen.tupled(persons, serializers)

  performance of "Serializer" in {
    performance of "person" in {
      using(inputs) in {
        case (person, serializer) =>
          val bytes = serializer.serialize(person)
          val converted = serializer.deserialize(bytes)
          assert(converted != null)
          assert(converted == person)
      }
    }
  }

}
