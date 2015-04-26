package debop4s.data.slick3.associations

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.associations.AssociationDatabase._
import debop4s.data.slick3.associations.AssociationDatabase.driver.api._
import org.joda.time.DateTime

/**
 * OrderFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class OrderFunSuite extends AbstractSlickFunSuite {

  val orderData = Seq(
    Order("O-123", new DateTime(2014, 1, 1, 0, 0)),
    Order("A-128", new DateTime(2014, 2, 1, 0, 0)),
    Order("B-128", new DateTime(2014, 6, 1, 0, 0)))

  val orderItemData = Seq(
    OrderItem("Pencil", 100, 1),
    OrderItem("Desk", 200, 1),
    OrderItem("Mouse", 100, 2),
    OrderItem("Keyboard", 200, 2),
    OrderItem("A4", 100, 3),
    OrderItem("BlackPen", 200, 3))

  lazy val schema = orders.schema ++ orderItems.schema

  override def beforeAll(): Unit = {
    super.beforeAll()

    commit {
      schema.drop.asTry >>
      schema.create >>
      (orders ++= orderData) >>
      (orderItems ++= orderItemData)
    }
  }

  override def afterAll(): Unit = {
    commit { schema.drop }
    super.afterAll()
  }

  test("one-to-many : Order and OrderItems") {
    val (ods, items) = readonly {
      for {
        ods <- orders.result
        items <- orderItems.result
      } yield (ods, items)
    }
    ods foreach { order => log.debug(s"order=$order") }
    items foreach { item => log.debug(s"orderItem=$item") }

    val innerJoin = orders join orderItems on { (o, i) => o.id === i.orderId }
    val q = innerJoin.filter(_._1.no === "A-128")

    val rs = readonly {
      for {
        _ <- q.length.result map (_ shouldEqual 2)
        rs <- q.result
      } yield rs
    }
    rs foreach { r => log.debug(r.toString) }
    //    q.length.exec shouldEqual 2
    //    q.exec foreach println


    val innerJoin2 = orders join orderItems on (_.id === _.orderId)
    val (length1, length2) = readonly {
      for {
        l1 <- innerJoin2.length.result
        l2 <- orderItems.count()
      } yield (l1, l2)
    }
    length1 shouldEqual length2

    // group by
    val joinQuery = for {
      (o, i) <- orders join orderItems on (_.id === _.orderId)
    } yield (o, i)

    val groupQuery = joinQuery.groupBy(_._1.id)
    val itemAvg = groupQuery.map { case (id, ois) => (id, ois.length, ois.map(_._2.price).avg) }

    val res = readonly { itemAvg.to[Set].result }
    res foreach { r => log.debug(r.toString) }
    res shouldEqual Set((1, 2, Some(150.0)),
                        (2, 2, Some(150.0)),
                        (3, 2, Some(150.0))
    )
  }

}
