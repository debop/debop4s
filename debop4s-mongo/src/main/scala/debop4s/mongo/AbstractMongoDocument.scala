package debop4s.mongo

import debop4s.core.{ToStringHelper, ValueObject}

/**
 * AbstractMongoDocument
 * @author sunghyouk.bae@gmail.com
 */
@SerialVersionUID(1290814343700461157L)
class AbstractMongoDocument extends ValueObject {

  @org.springframework.data.annotation.Id
  val id: String = null

  def getId: String = id

  override def equals(obj: Any): Boolean = {
    obj match {
      case doc: AbstractMongoDocument =>
        this.hashCode == doc.hashCode

      case _ => false
    }
  }

  override def hashCode: Int =
    if (id != null) id.hashCode else System.identityHashCode(this)

  override protected def buildStringHelper: ToStringHelper = {
    super.buildStringHelper
    .add("id", id)
  }
}
