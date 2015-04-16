package debop4s.core.io

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.io.model.{Company, CompanyEntity}

class PicklingSerializerFunSuite extends AbstractCoreFunSuite {

  val serializer = new PicklingSerializer()

  test("case class serialize") {
    val com = CompanyEntity(0, "구글", "google")
    val ser = serializer.serialize(com)

    val converted = serializer.deserialize(ser, classOf[CompanyEntity])
    converted should not be null
    converted shouldEqual com
  }

  //
  // NOTE: Scala Pickling 은 Java 수형은 지원하지 않는다. Integer, Double, Float 등!!!
  //
  test("class serialize") {
    intercept[ArrayIndexOutOfBoundsException] {
      val com = new Company()
      com.code = "HCT"
      com.name = "HealthConnect"
      com.employeeCount = 50
      val ser = serializer.serialize(com)

      val converted = serializer.deserialize(ser, classOf[CompanyEntity])
      converted should not be null
      converted shouldEqual com
    }
  }
}
