package debop4s.core.io.model

import java.{ math, util }

import debop4s.core.{ToStringHelper, ValueObject}
import debop4s.core.utils.Hashs

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

  var sales: java.math.BigDecimal = new math.BigDecimal(100)
  var employeeCount: Integer = 2000

  val users = new util.ArrayList[User]()

  override def hashCode(): Int = Hashs.compute(code, name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("code", code)
    .add("name", name)
    .add("amount", amount)
}
