package debop4s.core.json

import debop4s.core.utils.Hashs
import debop4s.core.{ToStringHelper, ValueObject}

import scala.beans.BeanProperty

/**
 * 객체를 JSON 직렬화를 수행하여, 그 결과를 저장하려고 할 때 사용한다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 4:54
 */
@SerialVersionUID(2934074553940326254L)
class JsonTextObject(@BeanProperty val className: String,
                     @BeanProperty val jsonText: String) extends ValueObject {

  override def hashCode: Int = Hashs.compute(className, jsonText)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("className", className)
    .add("jsonText", jsonText)
}

object JsonTextObject {

  private[this] lazy val serializer: JsonSerializer = JacksonSerializer()

  val Empty: JsonTextObject = apply()

  def apply(): JsonTextObject = new JsonTextObject(null, null)

  def apply(graph: Any): JsonTextObject = {
    graph match {
      case null => Empty
      case x: JsonTextObject => x
      case _ => new JsonTextObject(graph.getClass.getName, serializer.serializeToText(graph))
    }
  }

  def apply(className: String, jsonText: String): JsonTextObject =
    new JsonTextObject(className, jsonText)

  def apply(src: JsonTextObject): JsonTextObject = {
    require(src != null)
    new JsonTextObject(src.className, src.jsonText)
  }

  def unapply(jto: JsonTextObject): (String, String) =
    (jto.className, jto.jsonText)
}
