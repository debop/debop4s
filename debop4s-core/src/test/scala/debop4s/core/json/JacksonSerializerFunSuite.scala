package debop4s.core.json

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.json.model.Models._
import debop4s.core.model.User
import org.joda.time.{DateTime, DateTimeZone}

/**
 * JacksonSerializerFunSuite
 * Created by debop on 2014. 4. 4.
 */
class JacksonSerializerFunSuite extends AbstractCoreFunSuite {

  val serializer = JacksonSerializer()

  def serialize[@miniboxed T](graph: T) = serializer.serializeToText(graph)

  def deserialize[@miniboxed T: Manifest](text: String) = serializer.deserializeFromText[T](text)

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

