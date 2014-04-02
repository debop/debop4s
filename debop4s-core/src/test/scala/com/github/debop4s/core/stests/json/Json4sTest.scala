package com.github.debop4s.core.stests.json

import com.github.debop4s.core.stests.AbstractCoreTest
import com.github.debop4s.core.stests.model.User
import org.json4s.native.Serialization
import org.json4s.NoTypeHints
import java.util.Date

/**
 * Json4sTest
 * @author Sunghyouk Bae
 */
class Json4sTest extends AbstractCoreTest {

    implicit val formats = Serialization.formats(NoTypeHints)

    val user = User(10)

    val project = Project("test", new Date, Some(Language("Scala", 2.75)), List(
        Team("QA", List(Employee("John Doe", 5), Employee("Mike", 3))),
        Team("Impl", List(Employee("Mark", 4), Employee("Mary", 5), Employee("Nick Noob", 1)))))

    test("json4s serialize/deserialize") {
        val ser = Serialization.write(project)
        assert(project.toString == Serialization.read[Project](ser).toString)
    }

    case class Project(name: String, startDate: Date, lang: Option[Language], teams: List[Team])

    case class Language(name: String, version: Double)

    case class Team(role: String, members: List[Employee])

    case class Employee(name: String, experience: Int)

    test("Null example") {
        val ser = Serialization.write(Nullable(null))
        assert(Nullable(null).toString == Serialization.read[Nullable](ser).toString)
    }

    case class Nullable(name: String)

}
