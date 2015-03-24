package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * JdbcMapperFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMapperFunSuite extends AbstractSlickFunSuite {

  test("mapped entity") {
    case class User(id: Option[Int], first: String, last: String)
    case class Foo[T](value: T)

    class Users(tag: Tag) extends Table[User](tag, "jdbc_mapper_users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def last = column[String]("last")

      def * = (id.?, first, last) <>(User.tupled, User.unapply)

      def baseProjection = (first, last)
      def forUpdate = baseProjection.shaped <>
                      ( { case (f, l) => User(None, f, l) }, { u: User => Some((u.first, u.last)) })

      // 다른 객체로 casting 할 때 사용합니다.
      def asFoo = forUpdate <>((u: User) => Foo(u), (f: Foo[User]) => Some(f.value))
    }

    object users extends TableQuery(new Users(_)) {
      val byID = this.findBy(_.id)
    }

    withSession { implicit session =>
      Try { users.ddl.drop }
      users.ddl.create

      users.map(_.baseProjection).insert("Homer", "Simpson")
      users.insertAll(
                       User(None, "Marge", "Bouvier"),
                       User(None, "Carl", "Carlson")
                     )
      users.map(_.asFoo) += Foo(User(None, "Lenny", "Leonard"))

      val lastNames = Set("Bouvier", "Ferdinand")
      users.filter(_.last inSet lastNames).length.run shouldEqual 1

      val updateQ = users.filter(_.id === 2.bind).map(_.forUpdate)
      println("Update: " + updateQ.updateStatement)
      updateQ.update(User(None, "Marge", "Simpson"))

      Query(users.filter(_.id === 1.bind).exists).first shouldEqual true
      users.filter(_.id === 1.bind).exists.run shouldEqual true

      users.filter(_.id between(1, 2)) foreach println
      println("ID 3 ->" + users.byID(3).run)

      users.filter(_.id between(1, 2)).list.toSet shouldEqual
      Set(User(Some(1), "Homer", "Simpson"), User(Some(2), "Marge", "Simpson"))

      users.filter(_.id between(1, 2)).map(_.asFoo).list.toSet shouldEqual
      Set(Foo(User(None, "Homer", "Simpson")), Foo(User(None, "Marge", "Simpson")))

      users.byID(3).run.head shouldEqual User(Some(3), "Carl", "Carlson")

      // select x2."id", x2."first", x2."last" from "jdbc_mapper_users" x3, "jdbc_mapper_users" x2
      val q1 = for {
        u <- users
        u2 <- users
      } yield u2

      val r1 = q1.run.head
      r1.isInstanceOf[User] shouldEqual true
    }
  }

  test("update") {
    case class Data(a: Int, b: Int)

    class T(tag: Tag) extends Table[Data](tag, "jdbc_mapper_update") {
      def a = column[Int]("A")
      def b = column[Int]("B")
      def * = (a, b) <>(Data.tupled, Data.unapply)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.insertAll(Data(1, 2), Data(3, 4), Data(5, 6))

      val updateQ = ts.filter(_.a === 1.bind)
      updateQ.update(Data(7, 8))

      val updateQ2 = ts.filter(_.a === 3.bind).map(identity)
      updateQ2.update(Data(9, 10))

      ts.list.toSet shouldEqual Set(Data(7, 8), Data(9, 10), Data(5, 6))
    }
  }

  test("JPA Embeddable이나 Hibernate Component 처럼 여러 컬럼을 하나의 Component로 변환하기") {

  }

}
