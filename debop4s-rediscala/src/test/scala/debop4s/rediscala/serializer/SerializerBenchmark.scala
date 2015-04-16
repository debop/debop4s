package debop4s.rediscala.serializer

import org.scalameter.{Gen, PerformanceTest}
import org.scalatest.junit.AssertionsForJUnit


/**
 * SerializerBenchmark
 * @author Sunghyouk Bae
 */
object SerializerBenchmark extends PerformanceTest.Quickbenchmark with AssertionsForJUnit {

  val sizes = Gen.range("size")(1000, 10000, 1000)

  val serializers = Gen.enumeration("serializers")(
    new BinaryRedisSerializer[Any](),
    new FstRedisSerializer[Any](),
    // new PicklingSerializer[Any](), // NOTE: java primitives 는 안된다.
    new SnappyRedisSerializer[Any](new BinaryRedisSerializer[Any]()),
    new SnappyRedisSerializer[Any](new FstRedisSerializer[Any]()),
    new LZ4RedisSerializer[Any](new BinaryRedisSerializer[Any]),
    new LZ4RedisSerializer[Any](new FstRedisSerializer[Any])
  )


  var smallPerson = new Person()
  smallPerson.lastName = "배성혁"
  smallPerson.firstName = "동해물과 백두산이 마르고 닳도록"
  smallPerson.emailAddress add "sunghyouk.bae@gmail.com"

  var largePerson: Person = _
  largePerson = new Person()
  largePerson.lastName = "배성혁" * 10
  largePerson.firstName = "동해물과 백두산이 마르고 닳도록" * 10

  (0 until 1000) foreach { x =>
    largePerson.emailAddress add s"sunghyouk.bae$x@gmail.com"
  }
  (0 until 1000) foreach { x =>
    largePerson.tailsmans add s"debop-$x"
  }

  val persons = Gen.enumeration("persons")(smallPerson, largePerson)

  val inputs = Gen.tupled(persons, serializers)

  var i = 0L
  performance of "Serializer" in {
    performance of "person" in {
      using(inputs) in {
        case (person, serializer) =>
          person.id = i.toLong
          person.age = i.toInt
          val bytes = serializer.serialize(person)
          val converted = serializer.deserialize(bytes)
          assert(converted != null)
          assert(converted == person)
          i += 1L
      }
    }
  }

}
