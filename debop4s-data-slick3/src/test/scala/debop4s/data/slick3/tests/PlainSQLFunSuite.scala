package debop4s.data.slick3.tests

import debop4s.core.concurrent._

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import slick.jdbc.{StaticQuery => Q, SQLActionBuilder, GetResult}
import slick.profile.{SqlAction, SqlStreamingAction}

import scala.collection.mutable

/**
 * PlainSQLFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PlainSQLFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, name: String)

  implicit val getUserResult = GetResult(r => new User(r.<<, r.<<))

  test("plain sql") {
    def getUsers(id: Option[Int]) = {
      if (id.isDefined)
        sql"select id, name from plainsql_users where id=${ id.get }".as[User]
      else
        sql"select id, name from plainsql_users".as[User]
    }
    def insertUser(id: Int, name: String) = sqlu"insert into plainsql_users values($id, $name)"

    val createTable = sqlu"create table plainsql_users(id int not null primary key,name varchar(255))"

    val populateUsers = Seq(insertUser(1, "szeiger"), insertUser(0, "admin"), insertUser(2, "guest"), insertUser(3, "foo"))

    val allIDs = sql"select id from plainsql_users".as[Int]
    def userForID(id: Int) =
      sql"select id, name from plainsql_users where id=$id".as[User]
    def userForIdAndName(id: Int, name: String) =
      sql"select id, name from plainsql_users where id=$id and name=$name".as[User]

    commit {
      sqlu"drop table plainsql_users".asTry >>
      createTable.map(_ shouldEqual 0)
    }

    commit { DBIO.seq(populateUsers: _*) }

    readonly(allIDs) foreach { id => log.debug(s"id=$id") }
    readonly(allIDs).toSet shouldEqual Set(0, 1, 2, 3)

    readonly(userForID(2)).head shouldEqual User(2, "guest")
    readonly(userForID(2)) shouldEqual Seq(User(2, "guest"))

    readonly { getUsers(Some(2)) } shouldEqual Seq(User(2, "guest"))
    readonly { getUsers(None) }.toSet shouldEqual Set(User(0, "admin"), User(1, "szeiger"), User(2, "guest"), User(3, "foo"))

    val s5 = mutable.Set[User]()
    foreach(db.stream(getUsers(None))) { user =>
      s5 += user
    }.stay

    s5 shouldEqual Set(User(0, "admin"), User(1, "szeiger"), User(2, "guest"), User(3, "foo"))

    commit { sqlu"drop table plainsql_users" }
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

    commit {
      DBIO.seq(
        drop.asTry,
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

}
