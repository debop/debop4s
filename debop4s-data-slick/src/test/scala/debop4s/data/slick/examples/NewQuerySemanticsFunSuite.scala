package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * NewQuerySemanticsFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class NewQuerySemanticsFunSuite extends AbstractSlickFunSuite {

  class SupplierStd(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "NC_SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey)
    def name = column[String]("SUR_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    def * = (id, name, street, city, state, zip)
  }
  val suppliersStd = TableQuery[SupplierStd]

  class CoffeesStd(tag: Tag) extends Table[(String, Int, Int, Int, Int)](tag, "NC_COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Int]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total)
    def supplier = foreignKey("FK_COF_SUP", supID, suppliersStd)(_.id)
  }
  val coffeesStd = TableQuery[CoffeesStd]

  class Supplier(tag: Tag) extends Table[(Int, String, String)](tag, "NC_SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey)
    def name = column[String]("SUR_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    def * = (id, name, street)
  }
  val suppliers = TableQuery[Supplier]

  class Coffees(tag: Tag) extends Table[(String, Int, Int, Int, Int)](tag, "NC_COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Int]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total * 10)
    def totalComputed = sales * price
    def supplier = foreignKey("FK_COF_SUP", supID, suppliersStd)(_.id)
  }
  val coffees = TableQuery[Coffees]

  class Users(tag: Tag) extends Table[(Int, String, String)](tag, "NC_Users") {
    def id = column[Int]("id")
    def first = column[String]("first")
    def last = column[String]("last")
    def * = (id, first, last)
  }
  val users = TableQuery[Users]

  class Orders(tag: Tag) extends Table[(Int, Int)](tag, "NC_Orders") {
    def userID = column[Int]("userID")
    def orderID = column[Int]("orderID")
    def * = (userID, orderID)
    def orderPrimaryKey = primaryKey("pk_orders", (userID, orderID))
  }
  val orders = TableQuery[Orders]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val ddl = suppliersStd.ddl ++ coffeesStd.ddl ++ users.ddl ++ orders.ddl

    withTransaction { implicit session =>
      Try { ddl.drop }
      ddl.create

      suppliersStd +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
      suppliersStd +=(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
      suppliersStd +=(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

      coffeesStd ++= Seq(
                          ("Colombian", 101, 799, 1, 0),
                          ("French_Roast", 49, 799, 2, 0),
                          ("Espresso", 150, 999, 3, 0),
                          ("Colombian_Decaf", 101, 849, 4, 0),
                          ("French_Roast_Decaf", 49, 999, 5, 0)
                        )
    }
  }

  override protected def afterAll(): Unit = {

    val ddl = suppliersStd.ddl ++ coffeesStd.ddl ++ users.ddl ++ orders.ddl
    withTransaction { implicit session => Try { ddl.drop } }

    super.afterAll()
  }

  private def show[C[_]](name: String, q: Query[_, _, C]) = {
    println("========================== " + name)
    println(q.selectStatement)
  }

  test("query take") {
    withSession { implicit session =>

      // H2:
      // select x2."SUP_ID", x2."COF_NAME", 42
      //   from (select x3."TOTAL" as "TOTAL", x3."COF_NAME" as "COF_NAME", x3."PRICE" as "PRICE", x3."SUP_ID" as "SUP_ID", x3."SALES" as "SALES"
      //           from "NC_COFFEES" x3 limit 3) x2
      val qa = coffees.take(3).map { c => (c.supID, (c.name, 42)) }
      show("qa", qa)
      val ra = qa.run.toSet
      println("ra:" + ra)

      // H2:
      // select x2.x3, x2.x4
      //   from (select x5."COF_NAME" as x3, 42 as x4
      //           from (select x6."TOTAL" as "TOTAL", x6."COF_NAME" as "COF_NAME", x6."PRICE" as "PRICE", x6."SUP_ID" as "SUP_ID", x6."SALES" as "SALES"
      //                   from "NC_COFFEES" x6 limit 3) x5
      //           limit 2) x2
      val qb = qa.take(2).map(_._2)
      show("qb", qb)
      val rb = qb.run.toSet
      println("rb:" + rb)
      rb.size shouldEqual 2
      rb.foreach { case (name: String, n: Int) => n shouldEqual 42 }

      val qb2 = qa.map(identity).take(2).map(_._2)
      show("qb2", qb2)
      val rb2 = qb2.run.toSet
      println("rb2: " + rb2)
      rb2.size shouldEqual 2
      rb2.foreach { case (name: String, n: Int) => n shouldEqual 42 }

      val qc = qa.take(2).map(_._2)
      show("qc", qc)
      val rc = qc.run.toSet
      println("rc:" + rc)
      rc.size shouldEqual 2
      rc.foreach { case (name: String, n: Int) => n shouldEqual 42 }
    }
  }

  test("plain table") {
    val r0e = Set(
                   ("Colombian", 101, 799, 1, 0),
                   ("French_Roast", 49, 799, 2, 0),
                   ("Espresso", 150, 999, 3, 0),
                   ("Colombian_Decaf", 101, 849, 4, 0),
                   ("French_Roast_Decaf", 49, 999, 5, 0)
                 )

    val q0 = coffees
    show("q0: Plain table", q0)

    withSession { implicit session =>
      val r0 = q0.run.toSet
      println("r0: " + r0)
      r0 shouldEqual r0e
    }
  }

  test("plain implicit join") {
    val r1e = Set(
                   (("Colombian", "Groundsville:"), ("Colombian", 101, 799, 1, 0), (101, "Acme, Inc.", "99 Market Street"), 799),
                   (("Colombian", "Mendocino:"), ("Colombian", 101, 799, 1, 0), (49, "Superior Coffee", "1 Party Place"), 799),
                   (("Colombian", "Meadows:"), ("Colombian", 101, 799, 1, 0), (150, "The High Ground", "100 Coffee Lane"), 799),
                   (("Colombian_Decaf", "Groundsville:"), ("Colombian_Decaf", 101, 849, 4, 0), (101, "Acme, Inc.", "99 Market Street"), 3396),
                   (("Colombian_Decaf", "Mendocino:"), ("Colombian_Decaf", 101, 849, 4, 0), (49, "Superior Coffee", "1 Party Place"), 3396),
                   (("Colombian_Decaf", "Meadows:"), ("Colombian_Decaf", 101, 849, 4, 0), (150, "The High Ground", "100 Coffee Lane"), 3396)
                 )

    val q1 = for {
      c <- coffees.sortBy(c => (c.name, c.price.desc)).take(2)
      s <- suppliers
    } yield ((c.name, s.city ++ ":"), c, s, c.totalComputed)
    show("q1: Plain implicit join", q1)

    withSession { implicit session =>
      val r1 = q1.run.toSet
      println("r1: " + r1)
      r1 shouldEqual r1e
    }
  }

  test("paging nested") {
    ifCap(rcap.pagingNested) {
      val q1b_0 = coffees.sortBy(_.price).take(3) join suppliers on ( _.supID === _.id )
      val q1b = for {
        (c, s) <- q1b_0.sortBy(_._1.price).take(2).filter(_._1.name =!= "Colombian")
        (c2, s2) <- q1b_0
      } yield (c.name, s.city, c2.name)

      val r1be = Set(
                      ("French_Roast", "Mendocino", "Colombian"),
                      ("French_Roast", "Mendocino", "French_Roast"),
                      ("French_Roast", "Mendocino", "Colombian_Decaf")
                    )

      show("q1b: Explicit join with condition", q1b)

      withSession { implicit session =>
        val r1b = q1b.run.toSet
        println("r1b: " + r1b)
        r1b shouldEqual r1be
      }
    }
  }

  test("More elaborate query") {
    val r2e = Set(
                   ("Colombian", "Acme, Inc."),
                   ("French_Roast", "Superior Coffee"),
                   ("Colombian_Decaf", "Acme, Inc.")
                 )

    val q2 = for {
      c <- coffees.filter(_.price < 900)
      s <- suppliers if s.id === c.supID
    } yield (c.name, s.name)
    show("q2: More elaborate query", q2)

    withSession { implicit session =>
      val r2 = q2.run.toSet
      println("r2: " + r2)
      r2 shouldEqual r2e
    }
  }

  test("Lifting scala values") {
    val q3 = coffees.flatMap { c =>
      val cf = Query(c).filter(_.price === 849)
      cf.flatMap { cf =>
        suppliers.filter(_.id === c.supID).map { s =>
          (c.name, s.name, cf.name, cf.total, cf.totalComputed)
        }
      }
    }
    show("q3: Lifting scala values", q3)
    withSession { implicit session =>
      val r3 = q3.run.toSet
      println("r3: " + r3)
      val r3e = Set(("Colombian_Decaf", "Acme, Inc.", "Colombian_Decaf", 0, 3396))
      r3 shouldEqual r3e
    }

    val q3b = coffees.flatMap { c =>
      val cf = Query((c, 42)).filter(_._1.price < 900)
      cf.flatMap { case (cf, num) =>
        suppliers.filter(_.id === c.supID).map { s =>
          (c.name, s.name, cf.name, cf.price, cf.total, cf.totalComputed, num)
        }
      }
    }
    show("q3b: Lifting scala values, with extra tuple", q3b)
    val r3be = Set(
                    ("Colombian", "Acme, Inc.", "Colombian", 799, 0, 799, 42),
                    ("French_Roast", "Superior Coffee", "French_Roast", 799, 0, 1598, 42),
                    ("Colombian_Decaf", "Acme, Inc.", "Colombian_Decaf", 849, 0, 3396, 42)
                  )

    withSession { implicit session =>
      val r3b = q3b.run.toSet
      println("r3b: " + r3b)
      r3b shouldEqual r3be
    }
  }

  test("Map to tuple, then filter") {
    val q = for {
      c <- coffees.map(c => (c.name, c.price, 42)).sortBy(_._1).take(2).filter(_._2 < 800)
    } yield (c._1, c._3)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r:" + r)
      r shouldEqual Set(("Colombian", 42))
    }
  }

  test("Map to tuple, then filter, with self-join") {
    val q_0 = coffees.map(c => (c.name, c.price, 42)).filter(_._2 < 800)
    val q = for {
      c <- q_0
      d <- q_0
    } yield (c, d)

    val re = Set(
                  (("Colombian", 799, 42), ("Colombian", 799, 42)),
                  (("Colombian", 799, 42), ("French_Roast", 799, 42)),
                  (("French_Roast", 799, 42), ("Colombian", 799, 42)),
                  (("French_Roast", 799, 42), ("French_Roast", 799, 42))
                )

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual re
    }
  }

  test("Implicit self-join") {
    val q0 = coffees.sortBy(_.price).take(2)
    val q = for {
      c1 <- q0
      c2 <- q0
    } yield (c1, c2)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual Set(
                         (("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)),
                         (("Colombian", 101, 799, 1, 0), ("French_Roast", 49, 799, 2, 0)),
                         (("French_Roast", 49, 799, 2, 0), ("Colombian", 101, 799, 1, 0)),
                         (("French_Roast", 49, 799, 2, 0), ("French_Roast", 49, 799, 2, 0))
                       )
    }
  }

  test("Explicit self-join with condition") {
    val q0 = coffees.sortBy(_.price).take(2)
    val q = for {
      t <- q0 join q0 on ( _.name === _.name )
    } yield (t._1, t._2)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r:" + r)

      r shouldEqual Set(
                         (("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)),
                         (("French_Roast", 49, 799, 2, 0), ("French_Roast", 49, 799, 2, 0))
                       )
    }
  }

  test("Unused outer query result, unbound TableQuery") {

    // H2:
    // select x2."SUP_ID", x2."SUR_NAME", x2."STREET" from "NC_COFFEES" x3, "NC_SUPPLIERS" x2
    val q = coffees.flatMap(c => suppliers)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)
      r shouldEqual Set(
                         (101, "Acme, Inc.", "99 Market Street"),
                         (49, "Superior Coffee", "1 Party Place"),
                         (150, "The High Ground", "100 Coffee Lane")
                       )
    }
  }

  test("Simple Union") {
    // H2:
    // select x2.x3, x2.x4, x2.x5
    //   from (select x6."TOTAL" as x5, x6."COF_NAME" as x3, x6."SUP_ID" as x4
    //           from "NC_COFFEES" x6 where x6."PRICE" < 800
    //          union
    //         select x7."TOTAL" as x5, x7."COF_NAME" as x3, x7."SUP_ID" as x4
    //           from "NC_COFFEES" x7 where x7."PRICE" > 800) x2
    val q = for {
      c <- coffees.filter(_.price < 800) union coffees.filter(_.price > 950)
    } yield (c.name, c.supID, c.total)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual Set(
                         ("Colombian", 101, 0),
                         ("French_Roast", 49, 0),
                         ("Espresso", 150, 0),
                         ("French_Roast_Decaf", 49, 0)
                       )
    }
  }

  test("Union") {
    //H2 :
    // select x2.x3, x2.x4, x2.x5
    //   from (select x6."COF_NAME" as x3, x6."SUP_ID" as x4, 1 as x5
    //           from "NC_COFFEES" x6
    //          where x6."PRICE" < 800
    //
    //         union
    //
    //         select x7."COF_NAME" as x3, x7."SUP_ID" as x4, 2 as x5
    //           from "NC_COFFEES" x7
    //          where x7."PRICE" > 950) x2
    val q = for {
      c <- coffees.filter(_.price < 800).map((_, 1)) union coffees.filter(_.price > 950).map((_, 2))
    } yield (c._1.name, c._1.supID, c._2)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual Set(
                         ("Colombian", 101, 1),
                         ("French_Roast", 49, 1),
                         ("Espresso", 150, 2),
                         ("French_Roast_Decaf", 49, 2)
                       )
    }
  }

  test("Transitive push-down without union") {
    val q = for {
      c <- coffees.filter(_.price < 800.bind).map((_, 1))
    } yield (c._1.name, c._1.supID, c._2)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r:" + r)

      r shouldEqual Set(
                         ("Colombian", 101, 1),
                         ("French_Roast", 49, 1)
                       )
    }
  }

  test("Union with filter on the outside") {
    val q0 = for {
      c <- coffees.filter(_.price < 800).map((_, 1)) union coffees.filter(_.price > 950).map((_, 2))
    } yield (c._1.name, c._1.supID, c._2)

    // H2:
    // select x2.x3, x2.x4, x2.x5
    //   from (select x6."COF_NAME" as x3, x6."SUP_ID" as x4, 1 as x5
    //           from "NC_COFFEES" x6
    //          where x6."PRICE" < 800
    //
    //         union
    //
    //         select x7."COF_NAME" as x3, x7."SUP_ID" as x4, 2 as x5
    //           from "NC_COFFEES" x7 where x7."PRICE" > 950) x2
    //  where not (x2.x3 = ?)
    val q = q0 filter ( _._1 =!= "Colombian".bind )

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual Set(
                         ("French_Roast", 49, 1),
                         ("Espresso", 150, 2),
                         ("French_Roast_Decaf", 49, 2)
                       )
    }
  }

  test("Outer join") {

    // H2:
    // select x2.x3, x4.x5
    //   from (select x6."COF_NAME" as x3 from "NC_COFFEES" x6 where x6."PRICE" < 900) x2
    //   left outer join (select x7."COF_NAME" as x5 from "NC_COFFEES" x7 where x7."PRICE" < 800) x4 on x2.x3 = x4.x5
    val q = for {
      (c1, c2) <- coffees.filter(_.price < 900) leftJoin coffees.filter(_.price < 800) on ( _.name === _.name )
    } yield (c1.name, c2.name.?)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)
      r shouldEqual Set(
                         ("Colombian", Some("Colombian")),
                         ("French_Roast", Some("French_Roast")),
                         ("Colombian_Decaf", None)
                       )
    }
  }

  test("Nested outer join") {
    // H2:
    //     select x2.x3, x2.x4, x2.x5, x2.x6, x2.x7 * 10, x2.x8, x2.x9, x2.x10, x2.x11, x2.x12 * 10, x13.x14, x13.x15, x13.x16, x13.x17, x13.x18 * 10
    //     from (select x19.x20 as x7, x19.x21 as x3, x19.x22 as x5, x19.x23 as x4, x19.x24 as x6, x25.x26 as x12, x25.x27 as x8, x25.x28 as x10, x25.x29 as x9, x25.x30 as x11
    //             from (select x31."TOTAL" as x20, x31."COF_NAME" as x21, x31."PRICE" as x22, x31."SUP_ID" as x23, x31."SALES" as x24
    //                     from (select x32."TOTAL" as "TOTAL", x32."COF_NAME" as "COF_NAME", x32."PRICE" as "PRICE", x32."SUP_ID" as "SUP_ID", x32."SALES" as "SALES"
    //                             from "NC_COFFEES" x32 order by x32."SALES" limit 1) x31) x19
    //                           left outer join
    //                           (select x33."TOTAL" as x26, x33."COF_NAME" as x27, x33."PRICE" as x28, x33."SUP_ID" as x29, x33."SALES" as x30
    //                              from (select x34."TOTAL" as "TOTAL", x34."COF_NAME" as "COF_NAME", x34."PRICE" as "PRICE", x34."SUP_ID" as "SUP_ID", x34."SALES" as "SALES"
    //                                      from "NC_COFFEES" x34 order by x34."SALES" limit 2) x33) x25
    //                           on x19.x21 = x25.x27) x2
    //                   left outer join (select x35."TOTAL" as x18, x35."COF_NAME" as x14, x35."PRICE" as x16, x35."SUP_ID" as x15, x35."SALES" as x17
    //                                      from (select x36."TOTAL" as "TOTAL", x36."COF_NAME" as "COF_NAME", x36."PRICE" as "PRICE", x36."SUP_ID" as "SUP_ID", x36."SALES" as "SALES"
    //                                              from "NC_COFFEES" x36 order by x36."SALES" limit 4) x35
    //                                   ) x13
    //                   on x2.x4 = x13.x15
    val q0 = coffees.sortBy(_.sales)
    val q = for {
      t <- q0.take(1) leftJoin q0.take(2) on ( _.name === _.name ) leftJoin q0.take(4) on ( _._1.supID === _.supID )
    } yield (t._1, t._2)

    withSession { implicit session =>
      val r = q.run.toSet
      println("r: " + r)

      r shouldEqual Set(
                         ((("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)), ("Colombian", 101, 799, 1, 0)),
                         ((("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)), ("Colombian_Decaf", 101, 849, 4, 0))
                       )
    }
  }

  test("old composition") {

    val q2 = for {
      u <- users.sortBy(u => (u.first, u.last))
      o <- orders.filter(o => o.userID === u.id)
    } yield (u.first, u.last, o.orderID)

    val q3 = for (u <- users.filter(_.id === 42)) yield (u.first, u.last)

    withSession { implicit session =>
      q3.run

      val q4 = (
                 for {
                   (u, o) <- users innerJoin orders on ( _.id === _.userID )
                 } yield (u.last, (u.first, o.orderID)) )
               .sortBy(_._1)
               .map(_._2)

      q4.run

      val q6a = (
                  for {
                    o <- orders if o.orderID === ( for {o2 <- orders if o.userID === o2.userID} yield o2.orderID ).max
                  } yield o.orderID )
                .sorted

      q6a.run

      val q6b = (
                  for (o <- orders if o.orderID === ( for (o2 <- orders if o.userID === o2.userID) yield o2.orderID ).max)
                    yield (o.orderID, o.userID) )
                .sortBy(_._1)
      q6b.run

      val q6c = (
                  for (o <- orders if o.orderID === ( for (o2 <- orders if o.userID === o2.userID) yield o2.orderID ).max) yield o
                  )
                .sortBy(_.orderID)
                .map(o => (o.orderID, o.userID))
      q6c.run
    }
  }

  test("advanced fusion") {

    class TableA(tag: Tag) extends Table[Int](tag, "NQ_TableA") {
      def id = column[Int]("id")
      def * = id
    }
    val tableA = TableQuery[TableA]

    class TableB(tag: Tag) extends Table[(Int, Int)](tag, "NQ_TableB") {
      def id = column[Int]("id")
      def start = column[Int]("start")
      def * = (id, start)
    }
    val tableB = TableQuery[TableB]

    class TableC(tag: Tag) extends Table[Int](tag, "NQ_TableC") {
      def start = column[Int]("start")
      def * = start
    }
    val tableC = TableQuery[TableC]

    // H2:
    // select x2."id", x2."start", x3."start"
    //   from "NQ_TableA" x4, "NQ_TableB" x2, "NQ_TableC" x3
    //  where (x2."id" = x4."id") and (x3."start" <= (x4."id" + 1))
    val queryErr2 = for {
      a <- tableA
      b <- tableB if b.id === a.id
      start = a.id + 1
      c <- tableC if c.start <= start
    } yield (b, c)

    withSession { implicit session =>
      val ddl = tableA.ddl ++ tableB.ddl ++ tableC.ddl
      Try { ddl.drop }
      ddl.create

      queryErr2.run
    }
  }

  test("subquery") {
    class A(tag: Tag) extends Table[Int](tag, "NQ_subquery_a") {
      def id = column[Int]("id")
      def * = id
    }
    val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      as += 42

      val q0 = as.filter(_.id === 42.bind).length
      q0.run shouldEqual 1

      // select x2.x3 from "NQ_subquery_a" x4, (select count(1) as x3 from (select x5."id" as x6 from "NQ_subquery_a" x5) x7) x2 where x4."id" = ?
      val q1 = Compiled { (n: Column[Int]) => as.filter(_.id === n).map(a => as.length) }
      q1(42).run shouldEqual Seq(1)

      // subquery
      // H2
      // select x2."id" from "NQ_subquery_a" x2 where x2."id" in (select x3."id" from "NQ_subquery_a" x3 order by x3."id")
      val q2 = as.filter(_.id in as.sortBy(_.id).map(_.id))
      q2.run shouldEqual Seq(42)
    }
  }

  test("expansion") {
    class A(tag: Tag) extends Table[(Int, String)](tag, "NQ_refexp_a") {
      def id = column[Int]("id")
      def a = column[String]("a")
      def b = column[String]("b")
      def * = (id, a)

      // NOTE: INSERT 시에만 id, a, b 모든 컬럼을 사용하고, READ 시에는 id, a 만 사용하게 합니다.
      override def create_* = collectFieldSymbols((id, a, b).shaped.toNode)
    }
    val as = TableQuery[A]

    withSession { implicit session =>
      Try { as.ddl.drop }
      as.ddl.create

      // insert into "NQ_refexp_a" ("id","a","b")  values (?,?,?)
      as.map(a => (a.id, a.a, a.b)) ++= Seq(
                                             (1, "a1", "b1"),
                                             (2, "a2", "b2"),
                                             (3, "a3", "b3")
                                           )

      // H2:
      // select x2."id", x2."a" from "NQ_refexp_a" x2 where x2."b" = 'b3'
      val q1 = as.map(identity).filter(_.b === "b3")
      val r1 = q1.run
      r1.toSet shouldEqual Set((3, "a3"))

      val q2a = as.sortBy(_.a) join as on ( _.b === _.b )

      // H2:
      // select x2.x3, x4."a"
      //   from (select x5."id" as x3, x5."b" as x6
      //           from "NQ_refexp_a" x5 order by x5."a") x2
      //   inner join (select x7."b" as x8 from "NQ_refexp_a" x7) x9
      //   on x2.x6 = x9.x8,
      //         "NQ_refexp_a" x4
      val q2 = for {
        (c, s) <- q2a
        c2 <- as
      } yield (c.id, c2.a)

      val r2 = q2.run.toSet
      r2 shouldEqual Set((1, "a1"), (1, "a2"), (1, "a3"), (2, "a1"), (2, "a2"), (2, "a3"), (3, "a1"), (3, "a2"), (3, "a3"))
    }
  }

}
