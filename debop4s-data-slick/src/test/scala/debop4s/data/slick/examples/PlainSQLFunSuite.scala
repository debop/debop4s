package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._

import scala.collection.mutable
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import scala.util.Try

/**
 * PlainSQLFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class PlainSQLFunSuite extends AbstractSlickFunSuite {

  case class User(id: Int, name: String)
  implicit val getUserResult = GetResult(r => new User(r.<<, r.<<))

  test("plain sql") {
    def getUsers(id: Option[Int]) = {
      val q: Q[Unit, User] = Q[User] + "select id, name from plainsql_users"
      id map { q + " WHERE id=" +? _ } getOrElse q
    }
    def insertUser(id: Int, name: String) = Q.u + "insert into plainsql_users values(" +? id + "," +? name + ")"

    val createTable = Q[Int] +
                      """
                        |create table plainsql_users(
                        |   id int not null primary key,
                        |   name varchar(255)
                        |)
                      """.stripMargin

    val populateUsers = Seq(insertUser(1, "szeiger"), insertUser(0, "admin"), insertUser(2, "guest"), insertUser(3, "foo"))

    val allIDs = Q[Int] + "select id from plainsql_users"
    val userForID = Q[Int, User] + "select id, name from plainsql_users where id=?"
    val userForIdAndName = Q[(Int, String), User] + "select id, name from plainsql_users where id=? and name=?"

    withSession { implicit session =>
      Try {
        Try { sqlu"drop table plainsql_users;".execute }

        session.withTransaction {
          // SQL 문장을 print 하기 위해 first 를 사용했는데, 일반적으로 execute 를 쓰면 된다.
          println("Creating user table: " + createTable.first)
        }
        session.withTransaction {
          println("Insert users:")
          // SQL 문장을 print 하기 위해 first 를 사용했는데, 일반적으로 execute 를 쓰면 된다.
          populateUsers.foreach { i => LOG.debug(" " + i.first) }
        }
      }

      LOG.debug("All IDs:")
      allIDs.list.foreach { id => LOG.debug(s"id=$id") }
      allIDs.list.toSet shouldEqual Set(0, 1, 2, 3)

      LOG.debug("All IDs with foreach: ")
      val s1 = mutable.Set[Int]()
      allIDs foreach { id =>
        s1 += id
      }
      s1 shouldEqual Set(0, 1, 2, 3)

      val res = userForID(2).first
      LOG.debug(s"User for ID 2: $res")
      res shouldEqual User(2, "guest")

      LOG.debug("User 2 with foreach:")
      val s2 = mutable.Set[User]()
      userForID(2) foreach { user =>
        s2 += user
      }
      s2 shouldEqual Set(User(2, "guest"))

      LOG.debug("User 2 with foreach:")
      val s3 = mutable.Set[User]()
      getUsers(Some(2)) foreach { user =>
        s3 += user
      }
      s3 shouldEqual Set(User(2, "guest"))

      LOG.debug("All users with foreach: ")
      val s4 = mutable.Set[User]()
      getUsers(None) foreach { user =>
        s4 += user
      }
      s4 shouldEqual Set(User(1, "szeiger"), User(2, "guest"), User(0, "admin"), User(3, "foo"))

      LOG.debug("All users with iterator foreach: ")
      val s5 = mutable.Set[User]()
      getUsers(None).iterator foreach { user =>
        s5 += user
      }
      s5 shouldEqual Set(User(1, "szeiger"), User(2, "guest"), User(0, "admin"), User(3, "foo"))
    }
  }

  test("interpolation") {
    def userForID(id: Int) = sql"select id, name from plainsql_users where id = $id".as[User]
    def userForIdAndName(id: Int, name: String) = {
      sql"select id, name from plainsql_users where id=$id and name=$name".as[User]
    }

    withSession { implicit session =>
      Try { sqlu"drop table plainsql_users;".execute }
      Try {
        sqlu"create table plainsql_users(id int not null primary key,name varchar(255))".execute
      }

      sqlu"delete from plainsql_users".execute

      val total = ( for {
        (id, name) <- List((1, "szeiger"), (0, "admin"), (2, "guest"), (3, "foo"))
      } yield sqlu"insert into plainsql_users values($id, $name)".first ).sum

      total shouldEqual 4

      sql"select id from plainsql_users".as[Int].buildColl[Set] shouldEqual Set(0, 1, 2, 3)

      val res = userForID(2).first
      res shouldEqual User(2, "guest")

      val s1 = sql"select id from plainsql_users where name = ${ "szeiger" }".as[Int]
      val s2 = sql"select id from plainsql_users where name = '#${ "guest" }'".as[Int]

      s1.getStatement shouldEqual "select id from plainsql_users where name = ?"
      s2.getStatement shouldEqual "select id from plainsql_users where name = 'guest'"

      s1.list shouldEqual List(1)
      s2.list shouldEqual List(2)

      userForIdAndName(2, "guest").first shouldEqual User(2, "guest")
      userForIdAndName(2, "foo").firstOption shouldEqual None
    }
  }
}
