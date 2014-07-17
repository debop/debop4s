package debop4s.core.json

import com.fasterxml.jackson.databind.JsonMappingException
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

  test("trait deserialize") {
    val prof = Professor("professor", 50, "engineering")

    val profSer = json.serialize(prof)


    intercept[JsonMappingException] {
      val profDes = json.deserialize(profSer, classOf[Person])
      profDes match {
        case Professor(name, _, _) => name shouldEqual prof.name
        case _ => fail("Not Professor")
      }
    }
  }

  // NOTE: case-to-case 상속은 금지되어있습니다 (이유는 super case class와 sub case class 간의 equality 문제입니다)
  //  test("case class inheritances") {
  //    val foreign = ForeignStudent("sam", 20, "korean", "america")
  //    val ser = json.serialize(foreign)
  //    var des = json.deserialize[Student](ser)
  //
  //    des match {
  //      case x: ForeignStudent => x shouldEqual foreign
  //      case _ => fail("Not Foreign")
  //    }
  //  }
}

sealed trait Person {
  def name: String
  def age: Int
}

case class Professor(override val name: String, override val age: Int, spec: String) extends Person

case class Student(override val name: String, override val age: Int, degree: String) extends Person

//case class ForeignStudent(override val name: String,
//                          override val age: Int,
//                          override val degree: String,
//                          country: String) extends Student(name, age, degree)
