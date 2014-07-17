package debop4s.core.json

import debop4s.core.AbstractCoreTest

/**
 * JsonSerializerFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JsonSerializerFunSuite extends AbstractCoreTest {

  lazy val json = JacksonSerializer()

  test("case class ser/des") {
    val prof = Professor("professor", 50, "engineering")

    val profSer = json.serialize(prof)
    val profDes = json.deserialize(profSer, classOf[Professor])

    profDes should not be null
    profDes shouldEqual prof

    val bob = Student("bob", 22, "Freshman")
    val bobSer = json.serialize(bob)
    val bobDes = json.deserialize(bobSer, classOf[Student])

    bobDes should not be null
    bobDes shouldEqual bob

  }

  test("unapply") {
    val prof = Professor("professor", 50, "engineering")

    val profSer = json.serialize(prof)
    val profDes = json.deserialize(profSer, classOf[Professor])

    profDes match {
      case Professor(name, _, _) => name shouldEqual prof.name
      case _ => fail("Not Professor")
    }
  }
}

sealed trait Person {
  def name: String
  def age: Int
}

case class Professor(override val name: String, override val age: Int, spec: String) extends Person

case class Student(override val name: String, override val age: Int, degree: String) extends Person
