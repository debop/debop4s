package debop4s.core.io.model

import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObject}

/**
 * debop4s.core.tests.io.model.User
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:06
 */
@SerialVersionUID(-1375942267796202939L)
class User extends ValueObject {

  var name: String = _
  var empNo: String = _
  var address: String = _

  override def hashCode(): Int = Hashs.compute(name, empNo)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
    .add("empNo", empNo)
    .add("address", address)
}
