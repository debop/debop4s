package debop4s.data.slick.associations

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.associations.model.{ Order, OrderItem }
import debop4s.data.slick.associations.schema.AssociationDatabase._
import debop4s.data.slick.associations.schema.AssociationDatabase.driver.simple._
import org.joda.time.DateTime

import scala.util.Try

/**
 * OrderFunSuite
 * @author sunghyouk.bae@gmail.com at 15. 3. 23.
 */
class OrderFunSuite extends AbstractSlickFunSuite {

  val orderData = Seq(Order(None, "O-123", new DateTime(2014, 1, 1, 0, 0)),
                       Order(None, "A-128", new DateTime(2014, 2, 1, 0, 0)),
                       Order(None, "B-128", new DateTime(2014, 6, 1, 0, 0)))
  val orderItemData = Seq(OrderItem(None, "Pencil", 100, 1),
                           OrderItem(None, "Desk", 200, 1),
                           OrderItem(None, "Mouse", 100, 2),
                           OrderItem(None, "Keyboard", 200, 2),
                           OrderItem(None, "A4", 100, 3),
                           OrderItem(None, "BlackPen", 200, 3))

  override def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = orders.ddl ++ orderItems.ddl
    withTransaction { implicit session => Try { ddl.drop } }

    withTransaction { implicit session =>
      ddl.create

      orders.saveAll(orderData: _*)
      orderItems.saveAll(orderItemData: _*)
    }
  }

  test("one-to-many : order - orderItems") {
    withReadOnly { implicit session =>
      // select O.*, I.* from Order o inner join OrderItem I on (O.id = I.order_id)
      val items = orders.innerJoin(orderItems).on((o, i) => o.id === i.orderId)

      // where O.no = 'A-128'
      val q = items.filter(_._1.no === "A-128".bind)

      q.length.run shouldEqual 2
      LOG.debug(s"Query=${ q.selectStatement }")

      q.run foreach println

      val orderItems2 = orders.innerJoin(orderItems).on(_.id === _.orderId)
      orderItems2.length.run shouldEqual orderItems.count

      // GROUP BY

      val joinQuery =
        for {
          (o, i) <- orders innerJoin orderItems on ( _.id === _.orderId )
        } yield (o, i)

      val groupByQuery = joinQuery.groupBy(_._1.id) // group by order.id
    val itemAvgQuery = groupByQuery.map { case (id, its) => (id, its.length, its.map(_._2.price).avg) }

      LOG.debug(s"GroupBy:\n${ itemAvgQuery.selectStatement }")

      val itemAvgs = itemAvgQuery.list
      itemAvgs foreach println
      itemAvgs.foreach {
        case (id, sz, Some(avg)) => avg shouldEqual 150.0
      }
    }
  }
}
