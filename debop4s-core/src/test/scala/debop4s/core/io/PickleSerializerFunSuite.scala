package debop4s.core.io

import debop4s.core.AbstractCoreTest
import debop4s.core.io.model._

/**
 * NOTE: 이 클래스는 쓸모 없습니다. Pickling 은 그냥 코드 상에서 해주는 것이 좋습니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
class PickleSerializerFunSuite extends AbstractCoreTest {

  lazy val serializer = new PickleSerializer()

  test("case class serialization") {
    val person = PersonEntity(0, "joe", Array(3, 4, 13))

    val ser = serializer.serialize(person)
    val des = serializer.deserialize[PersonEntity](ser)

    des should not be null

    des match {
      case p: PersonEntity => p shouldEqual person
      case _ => fail("Fail to serialize case class")
    }

    // serializer 를 통해 super class로 unpickling 하면 예외가 발생하고,
    // 코드 내에서 수행하면 성공한다... 이유가???
    // 아마 serialization 관련 코드가 compile-time 에 생성되기 때문인 거 같다. 동적으로 생성되는게 아니므로, 알 수 없나 보다.
    intercept[NotImplementedError] {
      val superclass = serializer.deserialize[Entity[Long]](ser)
    }

    ser.unpickle[Entity[Long]] match {
      case p: PersonEntity => p shouldEqual person
      case _ => fail("Fail to unpickle case class by trait")
    }
  }

}
