package debop4s.core.json

import debop4s.core.AbstractCoreTest
import debop4s.core.json.JacksonSerializerTest._
import debop4s.core.model.User
import org.joda.time.{DateTimeZone, DateTime}
import scala.Some

/**
 * JacksonSerializerTest
 * Created by debop on 2014. 4. 4.
 */
class JacksonSerializerTest extends AbstractCoreTest {

  val serializer = JacksonSerializer()

  def serialize[T](graph: T) = serializer.serializeToText(graph)

  def deserialize[T: Manifest](text: String) = serializer.deserializeFromText[T](text)

  test("user serialize") {
    val user = User(10)
    val text = serialize(user)
    val des = deserialize[User](text)

    assert(des == user)
  }

  test("constructor") {
    val caseClass = ConstructorTestCaseClass(1, "foo")
    val text = serializer.serializeToText(caseClass)
    deserialize[ConstructorTestCaseClass](text) shouldEqual caseClass
  }

  val project = Project(
    "test",
    DateTime.now(DateTimeZone.UTC), // 시간은 모두 UTC 로 나타내야 합니다.
    Some(Language("Scala", 2.75)),
    List(Team("QA", List(Employee("John Doe", 5), Employee("Mike", 3))),
      Team("Impl", List(Employee("Mark", 4), Employee("Mary", 5), Employee("Nick Noob", 1)))
    )
  )

  test("case class serialize/deserialize") {
    val ser = serialize(project)
    println(s"project=$ser")
    val des = deserialize[Project](ser)

    des shouldEqual project

  }

  test("Null example") {
    val nullable = Nullable(null)
    val ser = serialize(nullable)
    println(s"project=$ser")
    val des = deserialize[Nullable](ser)

    des shouldEqual nullable
  }

  test("Primitive serialization") {
    val primitives = Primitives(124, 123L, 126.5, 127.5.floatValue, "128", 125, 129.byteValue, true)
    val ser = serialize(primitives)
    val des = deserialize[Primitives](ser)

    des shouldEqual primitives
  }

  test("Multidimensional list") {
    val ints = Ints(List(List(1, 2), List(3), List(4, 5)))
    val ser = serialize(ints)
    val des = deserialize[Ints](ser)
    des shouldEqual ints
  }

}

object JacksonSerializerTest {

  case class ConstructorTestCaseClass(intValue: Int, stringValue: String)

  case class Project(name: String, startDate: DateTime, lang: Option[Language], teams: List[Team])

  case class Language(name: String, version: Double)

  case class Team(role: String, members: List[Employee])

  case class Employee(name: String, experience: Int)

  case class Nullable(name: String)

  case class Primitives(v1: Int, v2: Long, v3: Double, v4: Float, v5: String, v6: Int, v7: Byte, v8: Boolean)

  case class Ints(x: List[List[Int]] = List(List[Int]()))

  case class Rec(n: Int, xs: List[Rec])

  case class Members(x: String, y: Int) {
    val foo1 = "foo"
    lazy val foo2 = "foo"
  }

  case class Meeting(place: String, time: DateTime)

  case class Times(times: List[DateTime])

  sealed abstract class Bool

  case class True() extends Bool

  case class False() extends Bool

  case class Ambiguous(child: Bool)

  trait Bird

  case class Falcon(weight: Double) extends Bird

  case class Chicken(eggs: Int) extends Bird

  case class AmbiguousP(bird: Bird)

  case class OptionOfAmbiguous(opt: Option[Bool])

  case class OptionOfAmbiguousP(opt: Option[Bird])

  case class SetContainer(set: Set[String])

  case class ArrayContainer(array: Array[String])

  case class SeqContainer(seq: Seq[String])

  case class OptionOfTupleOfDouble(position: Option[(Double, Double)])

  case class Player(name: String)

  case class TypeConstructor[A](x: A)

  case class ProperType(x: TypeConstructor[Chicken], t: (Int, Player))

  case class PlayerWithDefault(name: String, credits: Int = 5)

  case class PlayerWithOptionDefault(name: String, score: Option[Int] = Some(6))

  case class Gimmick(name: String)

  case class PlayerWithGimmick(name: String, gimmick: Gimmick = Gimmick("default"))

  case class PlayerWithBird(name: String, bird: Bird = Chicken(3))

  case class PlayerWithList(name: String, badges: List[String] = List("intro", "tutorial"))

  case class MeetingWithDefault(place: String, time: DateTime = new DateTime(7777L))

  case class TimesWithDefault(times: List[DateTime] = List(new DateTime(8888L)))

}