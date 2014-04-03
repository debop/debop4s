package com.github.debop4s.core.stests.json

import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.stests.model.User
import org.joda.time.DateTime
import org.json4s.ext._
import org.json4s.jackson.Serialization.{read, write => swrite}
import org.json4s.{jackson, NoTypeHints}

/**
 * Json4sTest
 * @author Sunghyouk Bae
 */
class Json4sTest extends AbstractCoreTest {

    implicit val formats = jackson.Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

    val user = User(10)

    case class Project(name: String, startDate: DateTime, lang: Option[Language], teams: List[Team])
    case class Language(name: String, version: Double)
    case class Team(role: String, members: List[Employee])
    case class Employee(name: String, experience: Int)

    test("case class equals") {
        val a = Nullable("ab")
        val a2 = Nullable("ab")
        assert(a == a2)
    }

    val project = Project(
        "test",
        DateTime.now(),
        Some(Language("Scala", 2.75)),
        List(Team("QA", List(Employee("John Doe", 5), Employee("Mike", 3))),
            Team("Impl", List(Employee("Mark", 4), Employee("Mary", 5), Employee("Nick Noob", 1)))
        )
    )

    test("json4s serialize/deserialize") {
        val ser = swrite(project)
        val des = read[Project](ser)

        des.hashCode() shouldEqual project.hashCode()
        des.toString shouldEqual project.toString

    }

    case class Nullable(name: String)

    test("Null example") {
        val nullable = Nullable(null)
        val ser = swrite(nullable)
        val des = read[Nullable](ser)

        des.hashCode() shouldEqual nullable.hashCode()
        des.toString shouldEqual nullable.toString
    }

    case class Primitives(v1: Int, v2: Long, v3: Double, v4: Float, v5: String, v6: Symbol, v7: Int, v8: Byte, v9: Boolean)

    test("Primitive serialization") {
        val primitives = Primitives(124, 123L, 126.5, 127.5.floatValue, "128", 's, 125, 129.byteValue, true)
        val ser = swrite(primitives)
        val des = read[Primitives](ser)

        des.hashCode() shouldEqual primitives.hashCode()
    }

    case class Ints(x: List[List[Int]] = List(List[Int]()))

    test("Multidimensional list") {
        val ints = Ints(List(List(1, 2), List(3), List(4, 5)))
        val ser = swrite(ints)
        val des = read[Ints](ser)
        des shouldEqual ints
    }

    case class Rec(n: Int, xs: List[Rec])

    case class Members(x: String, y: Int) {
        val foo1 = "foo"
        lazy val foo2 = "foo"
    }
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