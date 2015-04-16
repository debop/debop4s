package debop4s.mongo.springdata.model

import java.math.BigDecimal

import debop4s.core.utils.Hashs
import debop4s.mongo.AbstractMongoDocument
import org.springframework.data.mongodb.core.mapping.DBRef

/**
 * LineItem
 * @author sunghyouk.bae@gmail.com
 */
class LineItem extends AbstractMongoDocument {

  def this(product: Product, amount: Int = 1) {
    this()
    this.product = product
    this.amount = amount
    this.price = product.price
  }

  @DBRef
  var product: Product = _
  var price: BigDecimal = _
  var amount: Int = _

  def getTotal: BigDecimal = price.multiply(BigDecimal.valueOf(amount))

  override def hashCode: Int = Hashs.compute(product)
}
