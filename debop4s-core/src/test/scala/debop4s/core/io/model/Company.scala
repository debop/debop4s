package debop4s.core.io.model

import debop4s.core.ValueObject
import debop4s.core.utils.{ToStringHelper, Hashs}
import java.util

/**
 * debop4s.core.tests.io.model.Company
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:06
 */
@SerialVersionUID(4442244029750273886L)
class Company extends ValueObject {

  var code: String = _
  var name: String = _
  var description: String = _
  var amount: Long = 0

  val users = new util.ArrayList[User]()

  override def hashCode(): Int = Hashs.compute(code, name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("code", code)
    .add("name", name)
    .add("amount", amount)
}
