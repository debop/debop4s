package debop4s.mongo.springdata.model

import java.math.BigDecimal
import java.util

import debop4s.mongo.AbstractMongoDocument
import org.springframework.data.mongodb.core.mapping.{DBRef, Document}

import scala.collection.JavaConverters._

/**
 * Order
 * @author sunghyouk.bae@gmail.com
 */
@Document
class Order extends AbstractMongoDocument {

  def this(customer: Customer, shippingAddress: Address, billingAddress: Address = null) {
    this()
    this.customer = customer
    this.shippingAddress = shippingAddress
    this.billingAddress = billingAddress
  }

  @DBRef
  var customer: Customer = _

  var billingAddress: Address = _
  var shippingAddress: Address = _

  val lineItems = new util.HashSet[LineItem]()

  def addItem(item: LineItem): Unit = {
    this.lineItems.add(item)
  }

  def getTotal: BigDecimal = {
    var total = BigDecimal.ZERO
    lineItems.asScala.foreach { item =>
      total = total.add(item.getTotal)
    }
    total
  }

}
