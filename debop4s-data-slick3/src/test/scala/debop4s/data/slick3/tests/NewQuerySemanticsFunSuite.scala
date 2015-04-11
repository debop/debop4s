package debop4s.data.slick3.tests

import debop4s.core.concurrent._
import debop4s.data.slick3.AbstractSlickFunSuite

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
import debop4s.data.slick3.SlickContext._
import slick.backend.DatabasePublisher
import slick.util.TupleMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * NewQuerySemanticsFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class NewQuerySemanticsFunSuite extends AbstractSlickFunSuite {

  test("new composition") {
    class SuppliersStd(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "suppliers") {
      def id = column[Int]("sup_id", O.PrimaryKey)
      def name = column[String]("sup_name")
      def street = column[String]("street")
      def city = column[String]("city")
      def state = column[String]("state")
      def zip = column[String]("zip")
      def * = (id, name, street, city, state, zip)
    }
    lazy val suppliersStd = TableQuery[SuppliersStd]

    class CoffeesStd(tag: Tag) extends Table[(String, Int, Int, Int, Int)](tag, "coffees") {
      def name = column[String]("cof_name", O.PrimaryKey, O.Length(254))
      def supId = column[Int]("sup_id")
      def price = column[Int]("price")
      def sales = column[Int]("sales")
      def total = column[Int]("total")
      def * = (name, supId, price, sales, total)
      def supplier = foreignKey("fk_coffee_supplier", supId, suppliersStd)(_.id)
    }
    lazy val coffeesStd = TableQuery[CoffeesStd]

    // NOTE: SuppliersStd 와 같은 테이블을 사용하면서, projection 만 다르게 합니다. (단 primary key 는 제공해야 insert 를 수행할 수 있습니다.)
    //
    class Suppliers(tag: Tag) extends Table[(Int, String, String)](tag, "suppliers") {
      def id = column[Int]("sup_id", O.PrimaryKey)
      def name = column[String]("sup_name")
      def street = column[String]("street")
      def city = column[String]("city")
      def state = column[String]("state")
      def zip = column[String]("zip")
      def * = (id, name, street)
    }
    lazy val suppliers = TableQuery[Suppliers]

    // NOTE: CoffeesStd 와 같은 테이블을 사용하면서, projection 만 다르게 합니다. (단 primary key 는 제공해야 insert 를 수행할 수 있습니다.)
    //
    class Coffees(tag: Tag) extends Table[(String, Int, Int, Int, Int)](tag, "coffees") {
      def name = column[String]("cof_name", O.PrimaryKey, O.Length(254))
      def supId = column[Int]("sup_id")
      def price = column[Int]("price")
      def sales = column[Int]("sales")
      def total = column[Int]("total")
      def * = (name, supId, price, sales, (total * 10))
      def totalComputed = sales * price

      // NOTE: 원본테이블인 STD 와 같은 foreignKey 를 지정해 놓는다. schema 생성 정보를 참조하세요.
      def supplier = foreignKey("fk_coffee_supplier", supId, suppliers)(_.id)
    }
    lazy val coffees = TableQuery[Coffees]

    val schema = suppliersStd.schema ++ coffeesStd.schema

    val setup = DBIO.seq(
      schema.drop.asTry,
      schema.create,
      suppliersStd +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliersStd +=(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliersStd +=(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      coffeesStd ++= Seq(
        ("Colombian", 101, 799, 1, 0),
        ("French_Roast", 49, 799, 2, 0),
        ("Espresso", 150, 999, 3, 0),
        ("Colombian_Decaf", 101, 849, 4, 0),
        ("French_Roast_Decaf", 49, 999, 5, 0)
      )
    ).named("setup")

    val qa = for {c <- coffees.take(3)} yield (c.supId, (c.name, 42))
    val qb = qa.take(2).map(_._2)
    val qb2 = qa.map(identity).take(2).map(_._2)
    val qc = qa.map(_._2).take(2)

    val a1 = DBIO.seq(
      qa.result.map(_.toSet).map { ra =>
        ra.size shouldBe 3
        // No sorting, so result contents can vary
        ra shouldAllMatch { case (s: Int, (i: String, 42)) => () }
      },
      qb.result.map(_.toSet).map { rb =>
        rb.size shouldBe 2
        // No sorting, so result contents can vary
        rb shouldAllMatch { case (i: String, 42) => () }
      },
      qb2.result.map(_.toSet).map { rb2 =>
        rb2.size shouldBe 2
        rb2 shouldAllMatch { case (i: String, 42) => () }
      },
      qc.result.map(_.toSet).map { rc =>
        rc.size shouldBe 2
        rc shouldAllMatch { case (i: String, 42) => () }
      }
    )

    // Plain table
    val q0 = coffees

    // Plain implicit join
    val q1 = for {
      c <- coffees.sortBy(c => (c.name, c.price.desc)).take(2)
      s <- suppliers
    } yield ((c.name, s.city ++ ":"), c, s, c.totalComputed)

    // Explicit join with condition
    val q1b_0 = coffees.sortBy(_.price).take(3) join suppliers on (_.supId === _.id)
    /*
    ┇ select x2.x3, x2.x4, x5.x6
    ┇ from (
    ┇   select x7.x8 as x9, x7.x10 as x3, x11.x12 as x4
    ┇   from (
    ┇     select x13."sales" as x14, x13."total" as x15, x13."price" as x8, x13."sup_id" as x16, x13."cof_name" as x10
    ┇     from (
    ┇       select x17."sales" as "sales", x17."total" as "total", x17."price" as "price", x17."sup_id" as "sup_id", x17."cof_name" as "cof_name"
    ┇       from "coffees" x17
    ┇       order by x17."price"
    ┇       limit 3
    ┇     ) x13
    ┇   ) x7
    ┇   inner join (
    ┇     select x18."sup_id" as x19, x18."sup_name" as x20, x18."street" as x21, x18."city" as x12
    ┇     from "suppliers" x18
    ┇   ) x11
    ┇   on x7.x16 = x11.x19
    ┇   order by x7.x8
    ┇   limit 2
    ┇ ) x2, (
    ┇   select x22."sup_id" as x23, x22."cof_name" as x6
    ┇   from (
    ┇     select x24."sales" as "sales", x24."total" as "total", x24."price" as "price", x24."sup_id" as "sup_id", x24."cof_name" as "cof_name"
    ┇     from "coffees" x24
    ┇     order by x24."price"
    ┇     limit 3
    ┇   ) x22
    ┇ ) x5
    ┇ inner join (
    ┇   select x25."sup_id" as x26
    ┇   from "suppliers" x25
    ┇ ) x27
    ┇ on x5.x23 = x27.x26
    ┇ where not (x2.x3 = 'Colombian')
     */
    def q1b = for {
      (c, s) <- q1b_0.sortBy(_._1.price).take(2).filter(_._1.name =!= "Colombian")
      (c2, s2) <- q1b_0
    } yield (c.name, s.city, c2.name)

    val a2 = DBIO.seq(
      q0.result.named("Plain table").map(_.toSet).map { r0 =>
        r0 shouldBe Set(
          ("Colombian", 101, 799, 1, 0),
          ("French_Roast", 49, 799, 2, 0),
          ("Espresso", 150, 999, 3, 0),
          ("Colombian_Decaf", 101, 849, 4, 0),
          ("French_Roast_Decaf", 49, 999, 5, 0)
        )
      },
      q1.result.named("Plain implicit join").map(_.toSet).map { r1 =>
        r1 shouldBe Set(
          (("Colombian", "Groundsville:"), ("Colombian", 101, 799, 1, 0), (101, "Acme, Inc.", "99 Market Street"), 799),
          (("Colombian", "Mendocino:"), ("Colombian", 101, 799, 1, 0), (49, "Superior Coffee", "1 Party Place"), 799),
          (("Colombian", "Meadows:"), ("Colombian", 101, 799, 1, 0), (150, "The High Ground", "100 Coffee Lane"), 799),
          (("Colombian_Decaf", "Groundsville:"), ("Colombian_Decaf", 101, 849, 4, 0), (101, "Acme, Inc.", "99 Market Street"), 3396),
          (("Colombian_Decaf", "Mendocino:"), ("Colombian_Decaf", 101, 849, 4, 0), (49, "Superior Coffee", "1 Party Place"), 3396),
          (("Colombian_Decaf", "Meadows:"), ("Colombian_Decaf", 101, 849, 4, 0), (150, "The High Ground", "100 Coffee Lane"), 3396)
        )
      },
      ifCap(rcap.pagingNested) {
        q1b.result.named("Explicit join with condition").map { r1b =>
          r1b.toSet shouldBe Set(
            ("French_Roast", "Mendocino", "Colombian"),
            ("French_Roast", "Mendocino", "French_Roast"),
            ("French_Roast", "Mendocino", "Colombian_Decaf")
          )
        }
      }
    )

    // More elaborate query
    val q2 = for {
      c <- coffees.filter(_.price < 900).map(_.*)
      s <- suppliers if s.id === c._2
    } yield (c._1, s.name)

    // Lifting scalar values
    val q3 = coffees.flatMap { c =>
      val cf = Query(c).filter(_.price === 849)
      cf.flatMap { cf =>
        suppliers.filter(_.id === c.supId).map { s =>
          (c.name, s.name, cf.name, cf.total, cf.totalComputed)
        }
      }
    }

    // Lifting scalar values, with extra tuple
    val q3b = coffees.flatMap { c =>
      val cf = Query((c, 42)).filter(_._1.price < 900)
      cf.flatMap { case (cf, num) =>
        suppliers.filter(_.id === c.supId).map { s =>
          (c.name, s.name, cf.name, cf.total, cf.totalComputed, num)
        }
      }
    }

    // Map to tuple, then filter
    def q4 = for {
      c <- coffees.map(c => (c.name, c.price, 42)).sortBy(_._1).take(2).filter(_._2 < 800)
    } yield (c._1, c._3)

    // Map to tuple, then filter, with self-join
    val q4b_0 = coffees.map(c => (c.name, c.price, 42)).filter(_._2 < 800)
    val q4b = for {
      c <- q4b_0
      d <- q4b_0
    } yield (c, d)

    val a3 = DBIO.seq(
      q2.result.named("More elaborate query").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", "Acme, Inc."),
          ("French_Roast", "Superior Coffee"),
          ("Colombian_Decaf", "Acme, Inc.")
        )
      },
      q3.result.named("Lifting scala values").map(_.toSet).map {
        _ shouldBe Set(("Colombian_Decaf", "Acme, Inc.", "Colombian_Decaf", 0, 3396))
      },
      q3b.result.named("Lifting scalar values, with extra tuple").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", "Acme, Inc.", "Colombian", 0, 799, 42),
          ("French_Roast", "Superior Coffee", "French_Roast", 0, 1598, 42),
          ("Colombian_Decaf", "Acme, Inc.", "Colombian_Decaf", 0, 3396, 42)
        )
      },
      ifCap(rcap.pagingNested) {
        q4.result.named("Map to tuple, then filter").map { r4 =>
          r4.toSet shouldBe Set(("Colombian", 42))
        }
      },
      q4b.result.map(_.toSet).map {
        _ shouldBe Set(
          (("Colombian", 799, 42), ("Colombian", 799, 42)),
          (("Colombian", 799, 42), ("French_Roast", 799, 42)),
          (("French_Roast", 799, 42), ("Colombian", 799, 42)),
          (("French_Roast", 799, 42), ("French_Roast", 799, 42))
        )
      }
    )

    // Implicit self-join
    val q5_0 = coffees.sortBy(_.price).take(2)
    val q5 = for {
      c1 <- q5_0
      c2 <- q5_0
    } yield (c1, c2)

    // Explicit self-join with condition
    val q5b = for {
      t <- q5_0 join q5_0 on (_.name === _.name)
    } yield (t._1, t._2)

    // Unused outer query result, unbound TableQuery
    /*
    ┇ select x2."sup_id", x2."sup_name", x2."street"
    ┇ from "coffees" x3, "suppliers" x2
     */
    val q6 = coffees.flatMap(c => suppliers)

    val a4 = DBIO.seq(
      q5.result.map(_.toSet).map { r5 =>
        r5 shouldBe Set(
          (("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)),
          (("Colombian", 101, 799, 1, 0), ("French_Roast", 49, 799, 2, 0)),
          (("French_Roast", 49, 799, 2, 0), ("Colombian", 101, 799, 1, 0)),
          (("French_Roast", 49, 799, 2, 0), ("French_Roast", 49, 799, 2, 0))
        )
      },
      q5b.result.named("Explicit self-join with condition").map(_.toSet).map { r5b =>
        r5b shouldBe Set(
          (("Colombian", 101, 799, 1, 0), ("Colombian", 101, 799, 1, 0)),
          (("French_Roast", 49, 799, 2, 0), ("French_Roast", 49, 799, 2, 0))
        )
      },
      q6.result.named("Unused outer query result, unbound TableQuery").map(_.toSet).map { r6 =>
        r6 shouldBe Set(
          (101, "Acme, Inc.", "99 Market Street"),
          (49, "Superior Coffee", "1 Party Place"),
          (150, "The High Ground", "100 Coffee Lane")
        )
      }
    )

    // Simple union
    val q7a = for {
      c <- coffees.filter(_.price < 800) union coffees.filter(_.price > 950)
    } yield (c.name, c.supId, c.total)

    // Union
    val q7 = for {
      c <- coffees.filter(_.price < 800).map((_, 1)) union coffees.filter(_.price > 950).map((_, 2))
    } yield (c._1.name, c._1.supId, c._2)

    // Transitive push-down without union
    val q71 = for {
      c <- coffees.filter(_.price < 800).map((_, 1))
    } yield (c._1.name, c._1.supId, c._2)

    val a5 = DBIO.seq(
      q7a.result.named("Simple union").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", 101, 0),
          ("French_Roast", 49, 0),
          ("Espresso", 150, 0),
          ("French_Roast_Decaf", 49, 0)
        )
      },
      q7.result.named("Union").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", 101, 1),
          ("French_Roast", 49, 1),
          ("Espresso", 150, 2),
          ("French_Roast_Decaf", 49, 2)
        )
      },
      q71.result.named("Transitive push-down without union").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", 101, 1),
          ("French_Roast", 49, 1)
        )
      }
    )

    // Union with filter on the outside
    val q7b = q7 filter (_._1 =!= "Colombian")

    // Outer join
    val q8 = for {
      (c1, c2) <- coffees.filter(_.price < 900) joinLeft coffees.filter(_.price < 800) on (_.name === _.name)
    } yield (c1.name, c2.map(_.name))

    // Nested outer join
    val q8b = for {
      t <- coffees.sortBy(_.sales).take(1) joinLeft coffees.sortBy(_.sales).take(2) on (_.name === _.name) joinLeft
           coffees.sortBy(_.sales).take(4) on (_._1.supId === _.supId)
    } yield (t._1, t._2)

    val a6 = DBIO.seq(
      q7b.result.named("Union with filter on the outside").map(_.toSet).map {
        _ shouldBe Set(
          ("French_Roast", 49, 1),
          ("Espresso", 150, 2),
          ("French_Roast_Decaf", 49, 2)
        )
      },
      q8.result.named("Outer join").map(_.toSet).map {
        _ shouldBe Set(
          ("Colombian", Some("Colombian")),
          ("French_Roast", Some("French_Roast")),
          ("Colombian_Decaf", None)
        )
      },
      q8b.result.named("Nested outer join").map(_.toSet).map {
        _ shouldBe Set(
          ((("Colombian", 101, 799, 1, 0), Some(("Colombian", 101, 799, 1, 0))), Some(("Colombian", 101, 799, 1, 0))),
          ((("Colombian", 101, 799, 1, 0), Some(("Colombian", 101, 799, 1, 0))), Some(("Colombian_Decaf", 101, 849, 4, 0)))
        )
      }
    )

    db.seq(
      setup,
      a1,
      a2,
      a3,
      a4,
      a5,
      a6,
      schema.drop
    )
  }

  test("old composition") {
    class Users(tag: Tag) extends Table[(Int, String, String)](tag, "users") {
      def id = column[Int]("id")
      def first = column[String]("first")
      def last = column[String]("last")
      def * = id ~ first ~ last
    }
    lazy val users = TableQuery[Users]

    class Orders(tag: Tag) extends Table[(Int, Int)](tag, "orders") {
      def userId = column[Int]("userId")
      def orderId = column[Int]("orderId")
      def * = userId ~ orderId
    }
    lazy val orders = TableQuery[Orders]

    val q2 = for {
      u <- users.sortBy(u => (u.first, u.last.desc))
      o <- orders filter { o => o.userId === u.id }
    } yield u.first ~ u.last ~ o.orderId

    val q3 = for (u <- users filter (_.id === 42)) yield u.first ~ u.last

    val q4 = (for {
      (u, o) <- users join orders on (_.id === _.userId)
    } yield (u.last, u.first ~ o.orderId)).sortBy(_._1).map(_._2)

    val q6a = (for {
      o <- orders if o.orderId === (for {o2 <- orders if o2.userId === o.userId} yield o2.orderId).max
    } yield o.orderId).sorted

    val q6b = (for {
      o <- orders if o.orderId === (for {o2 <- orders if o2.userId === o.userId} yield o2.orderId).max
    } yield o.orderId ~ o.userId).sortBy(_._1)

    val q6c = (for {
      o <- orders if o.orderId === (for {o2 <- orders if o2.userId === o.userId} yield o2.orderId).max
    } yield o).sortBy(_.orderId).map(o => o.orderId ~ o.userId)

    val schema = users.schema ++ orders.schema

    db.seq(
      schema.drop.asTry,
      schema.create,
      q3.result,
      q4.result,
      q6a.result,
      q6b.result,
      q6c.result,
      schema.drop
    )
  }

  test("advanced fusion") {
    class TableA(tag: Tag) extends Table[Int](tag, "TableA") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val tableA = TableQuery[TableA]

    class TableB(tag: Tag) extends Table[(Int, Int)](tag, "TableB") {
      def id = column[Int]("id")
      def start = column[Int]("start")
      def * = (id, start)
    }
    lazy val tableB = TableQuery[TableB]

    class TableC(tag: Tag) extends Table[Int](tag, "TableC") {
      def start = column[Int]("start")
      def * = start
    }
    lazy val tableC = TableQuery[TableC]

    /*
    ┇ select x2."id", x2."start", x3."start"
    ┇ from "TableA" x4, "TableB" x2, "TableC" x3
    ┇ where (x2."id" = x4."id") and (x3."start" <= (x4."id" + 1))
     */
    val queryErr2 = for {
      a <- tableA
      b <- tableB if b.id === a.id
      start = a.id + 1
      c <- tableC if c.start <= start
    } yield (b, c)

    val schema = tableA.schema ++ tableB.schema ++ tableC.schema

    db.seq(
      schema.drop.asTry,
      schema.create,

      queryErr2.result,

      schema.drop
    )
  }

  test("subquery") {
    class A(tag: Tag) extends Table[Int](tag, "subquery_a") {
      def id = column[Int]("id")
      def * = id
    }
    lazy val as = TableQuery[A]

    db.exec(
      as.schema.drop.asTry >>
      as.schema.create >>
      (as += 42) >>
      as.filter(_.id === 42.bind).length.result.map(_ shouldEqual 1) >> {
        val q1 = Compiled { (n: Rep[Int]) =>
          as.filter(_.id === n).map(a => as.length)
        }
        q1(42).result.map(_ shouldEqual Seq(1))
      } >> {
        val q2 = as.filter(_.id in as.sortBy(_.id).map(_.id))
        q2.result.map(_ shouldEqual Seq(42))
      } >>

      as.schema.drop
    )
  }

  test("expansion") {
    class A(tag: Tag) extends Table[(Int, String)](tag, "expansion_a") {
      def id = column[Int]("id")
      def a = column[String]("a")
      def b = column[String]("b")
      def * = (id, a)
      // NOTE: 기본 projection이 (id, a) 이므로, create 시에는 (id, a, b)  모두 지정할 수 있도록 재정의한다.
      override def create_* = collectFieldSymbols((id, a, b).shaped.toNode)
    }
    lazy val as = TableQuery[A]

    db.seq(
      as.schema.drop.asTry,
      as.schema.create,
      as.map(a => (a.id, a.a, a.b)) ++= Seq(
        (1, "a1", "b1"),
        (2, "a2", "b2"),
        (3, "a3", "b3")
      )
    )

    val q1 = as.map(identity).filter(_.b === "b3")
    q1.exec.toSet shouldEqual Set((3, "a3"))

    /*
    ┇ select x2.x3, x4."a"
    ┇ from (
    ┇   select x5."id" as x3, x5."b" as x6
    ┇   from "expansion_a" x5
    ┇   order by x5."a"
    ┇ ) x2
    ┇ inner join (
    ┇   select x7."b" as x8
    ┇   from "expansion_a" x7
    ┇ ) x9
    ┇ on x2.x6 = x9.x8, "expansion_a" x4
     */
    val q2a = as.sortBy(_.a) join as on (_.b === _.b)
    val q2 = for {
      (c, s) <- q2a
      c2 <- as
    } yield (c.id, c2.a)

    q2.exec.toSet shouldEqual Set((1, "a1"), (1, "a2"), (1, "a3"), (2, "a1"), (2, "a2"), (2, "a3"), (3, "a1"), (3, "a2"), (3, "a3"))

    as.schema.drop.exec
  }

}
