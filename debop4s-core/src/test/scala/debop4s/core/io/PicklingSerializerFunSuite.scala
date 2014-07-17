package debop4s.core.io

import debop4s.core.io.model.CompanyEntity
import org.scalatest.{FunSuite, Matchers}

class PicklingSerializerFunSuite extends FunSuite with Matchers {

  val serializer = new PicklingSerializer()

  test("serialize") {
    val com = CompanyEntity(0, "구글", "google")
    val ser = serializer.serialize(com)

    val converted = serializer.deserialize(ser, classOf[CompanyEntity])
    converted should not be null
    converted shouldEqual com
  }
}
