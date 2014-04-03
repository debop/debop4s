package com.github.debop4s.core.stests.json

import com.github.debop4s.core.json.ScalaJacksonSerializer
import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.stests.model.User
import org.joda.time.DateTime
import scala.Some

/**
 * ScalaJsonSerializer
 * Created by debop on 2014. 2. 22.
 */
class ScalaJsonSerializerTest extends AbstractCoreTest {

    val serializer = ScalaJacksonSerializer()


    val user = User(10)

    test("scala jsonserialize / deserialize") {
        println(s"JsonSerializer=${serializer.getClass}")

        val text = serializer.serializeToText(user)
        val des = serializer.deserializeFromText[User](text)

        log.debug(s"text=$text")

        des shouldEqual user
    }

    case class Project(name: String, startDate: DateTime, lang: Option[Language], teams: List[Team])
    case class Language(name: String, version: Double)
    case class Team(role: String, members: List[Employee])
    case class Employee(name: String, experience: Int)

    val project = Project(
        "test",
        DateTime.now(),
        Some(Language("Scala", 2.75)),
        List(Team("QA", List(Employee("John Doe", 5), Employee("Mike", 3))),
            Team("Impl", List(Employee("Mark", 4), Employee("Mary", 5), Employee("Nick Noob", 1)))
        )
    )

    test("json4s serialize/deserialize") {
        val ser = serializer.serialize(project)
        val des = serializer.deserialize[Project](ser)

        des.hashCode() shouldEqual project.hashCode()
        des.toString shouldEqual project.toString

    }

    case class Nullable(name: String)

    test("Null example") {
        val nullable = Nullable(null)
        val ser = serializer.serialize(nullable)
        val des = serializer.deserialize[Nullable](ser)

        des.hashCode() shouldEqual nullable.hashCode()
        des.toString shouldEqual nullable.toString
    }

    case class Primitives(v1: Int, v2: Long, v3: Double, v4: Float, v5: String, v6: Symbol, v7: Int, v8: Byte, v9: Boolean)
    test("Primitive serialization") {
        val primitives = Primitives(124, 123L, 126.5, 127.5.floatValue, "128", 's, 125, 129.byteValue, true)
        val ser = serializer.serialize(primitives)
        val des = serializer.deserialize[Primitives](ser)

        des.hashCode() shouldEqual primitives.hashCode()
    }

    test("Multidimensional list") {
        val ints = Ints(List(List(1, 2), List(3), List(4, 5)))
        val ser = serializer.serialize(ints)
        val des = serializer.deserialize[Ints](ser)
        des shouldEqual ints
    }

    case class Ints(x: List[List[Int]])

    case class Rec(n: Int, xs: List[Rec])

    case class Members(x: String, y: Int) {
        val foo1 = "foo"
        lazy val foo2 = "foo"
    }
}

