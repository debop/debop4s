package debop4s.web.spring

import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObjectBase}

import scala.beans.BeanProperty

/**
 * API 결과를 나타내는 클래스입니다.
 *
 * @author Sunghyouk Bae
 */
class ApiResult(@BeanProperty var header: ApiHeader,
                @BeanProperty var body: Any) extends ValueObjectBase {

  def this() = this(ApiHeader(), "")

  def this(body: Any) = this(ApiHeader(), body)

  override def hashCode: Int = Hashs.compute(header, body)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("header", header)
    .add("body", body)
}

object ApiResult {

  def apply(): ApiResult = new ApiResult()
  def apply(body: Any): ApiResult = new ApiResult(body)
  def apply(code: Int, message: String): ApiResult = new ApiResult(ApiHeader(code, message), "")

}
