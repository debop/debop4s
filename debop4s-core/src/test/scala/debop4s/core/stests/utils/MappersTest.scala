package debop4s.core.stests.utils

import debop4s.core.ValueObject
import debop4s.core.stests.AbstractCoreTest
import debop4s.core.utils.Mappers
import java.util
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * debop4s.core.tests.tools.MappersTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오전 10:24
 */
class MappersTest extends AbstractCoreTest {

  //
  // NOTE: Java용 Mapper 들은 Scala 고유의 수형이나 case class 에서는 작동하지 않는다.
  // NOTE: Reflection을 이용한 Mapper 보다 차라리 implicit method 를 쓰는 것이 낫다.
  //

  case class Order(customer: Customer, items: List[OrderLineItem] = List()) {
    def addItem(product: Product, quantity: Int) =
      copy(items = OrderLineItem(product, quantity) :: items)

    def total = items.foldLeft(0.0) {
      _ + _.total
    }
  }

  case class Product(name: String, price: Double)

  case class OrderLineItem(product: Product, quantity: Int) {
    def total = quantity * product.price
  }

  case class Customer(name: String)

  case class OrderDTO(customerName: String, total: Double)

  implicit def order2OrderDTO(order: Order) =
    OrderDTO(order.customer.name, order.total)

  test("Scala sytle - Implicit conversion") {
    val customer = Customer("배성혁")
    val bosco = Product("Bosco", 4.99)
    val order = Order(customer).addItem(bosco, 15)

    val dto: OrderDTO = order
    assert(dto.customerName == customer.name)
    assert(dto.total == order.total)
  }

  test("map") {
    val a = new A(100)
    val b = Mappers.map[B](a)
    assert(b.x == a.x)
  }

  test("map list") {
    val as = Range(0, 100).map(x => new A(x)) // for (x <- 0 until 100) yield new A(x)
    val bs = Mappers.mapAll[B](as)
    assert(bs.size == as.size)

    val bsf = Mappers.mapAllAsync[B](as)
    Await.result(bsf, 100 milli)
    bsf.value.get.get.size shouldEqual as.size
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  test("map array") {
    val bs = Mappers.mapAll[B](List(new A(0), new A(1), new A(2)))
    assert(bs.size == 3)

    val bsf = Mappers.mapAllAsync[B](List(new A(0), new A(1), new A(2)))
    bsf onComplete {
      case Success(result) => result.size shouldEqual 3
      case Failure(t) => throw new RuntimeException(t)
    }
  }

  test("Nested mapping") {
    val parent = createParent()
    val parentDTO = Mappers.map[ParentDTO](parent)

    assert(parentDTO != null)
    assert(parentDTO.children.size == parent.children.size)
    assert(parentDTO.name == parent.name)

    val sz = parent.children.size
    for (i <- 0 until sz) {
      assert(parentDTO.children.get(i).id == parent.children.get(i).id)
      assert(parentDTO.children.get(i).age == parent.children.get(i).age)
      assert(parentDTO.children.get(i).name == parent.children.get(i).name)
      assert(parentDTO.children.get(i).description == parent.children.get(i).description)
    }
  }

  private def createParent() = {
    val parent = new Parent()
    parent.id = 1
    parent.age = 45
    parent.name = "배성혁"
    parent.description = "부모 객체입니다."
    for (i <- 0 until 10) {
      val child = new Child()
      child.id = i
      child.age = i + 1
      child.name = "자식-" + i
      child.description = "자식입니다."
      child.parent = parent

      parent.children.add(child)
    }
    parent
  }
}


class A(var x: Int) {
  def this() {
    this(0)
  }

  var y: String = _
}

class B(var x: Int) {
  def this() {
    this(0)
  }
}

class Parent extends ValueObject {

  var id: java.lang.Long = _
  var age: java.lang.Long = _
  var name: String = _
  var description: String = _

  val children = new util.ArrayList[Child]()
}

class Child extends ValueObject {
  var id: java.lang.Long = _
  var age: java.lang.Long = _
  var name: String = _
  var description: String = _

  var parent: Parent = _
}

class ParentDTO extends ValueObject {

  var id: java.lang.Long = _
  var age: java.lang.Long = _
  var name: String = _
  var description: String = _

  val children = new util.ArrayList[ChildDTO]()
}

class ChildDTO extends ValueObject {
  var id: java.lang.Long = _
  var age: java.lang.Long = _
  var name: String = _
  var description: String = _

  var parent: ParentDTO = _
}