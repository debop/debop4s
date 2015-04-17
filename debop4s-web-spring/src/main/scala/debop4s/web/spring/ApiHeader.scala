package debop4s.web.spring

import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObjectBase}

import scala.beans.BeanProperty

/**
 * API 결과의 Header 정보압니다.
 *
 * @author Sunghyouk Bae
 */
@SerialVersionUID(-3823273096277519657L)
class ApiHeader(@BeanProperty var code: Int,
                @BeanProperty var message: String) extends ValueObjectBase {

  def this() = this(200, "Success")

  override def hashCode = Hashs.compute(code)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("code", code)
    .add("message", message)
}

object ApiHeader {

  def apply(code: Int = 200, message: String = "Success"): ApiHeader =
    new ApiHeader(code, message)

}
