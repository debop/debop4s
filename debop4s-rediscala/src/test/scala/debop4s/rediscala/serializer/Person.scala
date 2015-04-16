package debop4s.rediscala.serializer

import java.util

import debop4s.core.ValueObject
import debop4s.core.utils.Hashs
import org.joda.time.DateTime


@SerialVersionUID(-8245742950718661800L)
class Person extends ValueObject {

  var id: java.lang.Long = _

  var age: Integer = _
  var firstName: String = _
  var lastName: String = _

  // FST 에서 Float 수형이 제대로 안된다.
  var height: java.lang.Double = 180.8
  var weight: java.lang.Float = 77.7f

  var birth: DateTime = DateTime.now()

  var emailAddress: util.Set[String] = new util.HashSet[String]()

  var tailsmans: util.List[String] = new util.ArrayList[String]()

  override def hashCode: Int =
    Hashs.compute(age, firstName, lastName, height, weight)

}
