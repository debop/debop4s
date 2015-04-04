package debop4s.data.slick3.tests

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3._
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * PlainSQLFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PlainSQLFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, name: String)

  implicit val getUserResult = GetResult(r => new User(r.<<, r.<<))

  test("simple") {
    // TODO
  }

  test("interpolation") {
    def userForID(id: Int) = sql"select id, name from USERS where id=$id".as[User]
    def userForIdAndName(id: Int, name: String) = sql"select id, name from USERS where id=$id and name=$name".as[User]

    val foo = "foo"
    val s1 = sql"select id from USERS where name = ${ "szeiger" }".as[Int]
    val s2 = sql"select id from USERS where name = '#${ "guest" }'".as[Int]
    val s3 = sql"select id from USERS where name = $foo".as[Int]
    val s4 = sql"select id from USERS where name = '#$foo'".as[Int]

    s1.statements.head shouldEqual "select id from USERS where name = ?"
    s2.statements.head shouldEqual "select id from USERS where name = 'guest'"
    s3.statements.head shouldEqual "select id from USERS where name = ?"
    s4.statements.head shouldEqual "select id from USERS where name = 'foo'"

    val create: DBIO[Int] = sqlu"create table USERS(ID int not null primary key, NAME varchar(255))"
    val drop: DBIO[Int] = sqlu"drop table USERS"

    db.seq(
      create.map(_ shouldEqual 0),
      DBIO.fold((for {
        (id, name) <- List((1, "szeiger"), (0, "admin"), (2, "guest"), (3, "foo"))
      } yield sqlu"insert into USERS values($id, $name)"), 0)(_ + _).map(_ shouldEqual 4),
      sql"select id from USERS".as[Int].map(_.toSet shouldEqual Set(0, 1, 2, 3)),
      userForID(2).map(_.head shouldEqual User(2, "guest")),
      s1.map(_ shouldEqual Seq(1)),
      s2.map(_ shouldEqual Seq(2)),
      userForIdAndName(2, "guest").map(_.head shouldEqual User(2, "guest")),
      userForIdAndName(2, "foo").map(_.headOption shouldEqual None),
      drop.map(_ shouldEqual 0)
    )
  }

}
