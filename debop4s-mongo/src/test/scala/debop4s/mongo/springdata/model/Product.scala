package debop4s.mongo.springdata.model

import java.math.BigDecimal
import java.util

import debop4s.core.utils.Hashs
import debop4s.mongo.AbstractMongoDocument
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Product
 * @author sunghyouk.bae@gmail.com
 */
@Document
class Product extends AbstractMongoDocument {

  def this(name: String, price: BigDecimal, description: String = null) {
    this()
    this.name = name
    this.price = price
    this.description = description
  }

  var name: String = _
  var description: String = _

  // NOTE: scala.math.BigDecimal 은 기본 생성자가 없어서 사용할 수 없습니다.
  var price: BigDecimal = _
  val attributes = new util.HashMap[String, String]()

  def setAttribute(name: String, value: String): Unit = {
    if (value == null) attributes.remove(name)
    else attributes.put(name, value)
  }

  override def hashCode: Int = Hashs.compute(name, price)
}
