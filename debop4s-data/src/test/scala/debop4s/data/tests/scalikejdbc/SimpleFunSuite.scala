package debop4s.data.tests.scalikejdbc

import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}
import org.slf4j.LoggerFactory
import scalikejdbc._

/**
 * SimpleFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class SimpleFunSuite extends FunSuite with Matchers with BeforeAndAfterAll {

  private lazy val log = LoggerFactory.getLogger(getClass)

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "sa", "")

  implicit val session = AutoSession

  override protected def beforeAll() {
    sql"""
        create table members(
          id serial not null primary key,
          name varchar(64),
          created_at timestamp not null
        )
    """.execute().apply()

    Seq("Alice", "Bob", "Chris") foreach { name =>
      sql"insert into members(name, created_at) values($name, current_timestamp)".update().apply()
    }
  }

  test("initialize") {
    val name = "Alice"
    val memberId = DB readOnly { implicit session =>
      sql"select id from members where name=$name"
      .map(rs => rs.long("id"))
      .single()
      .apply()
    }

    log.debug(s"load Alice, memberId=${memberId.get}")

    val m = Member.syntax
    val ids = withSQL {
      select()
    }
  }

  test("load all") {

    val entities: List[Member] = sql"select * from members".map(rs => Member(rs)).list().apply()

    entities.size shouldEqual 3

    entities.foreach { m =>
      log.debug(m.toString)
    }
  }

  test("query dsl") {

    val m = Member.syntax("m")
    val name = "Alice"
    val alice: Option[Member] = withSQL {
      select.from(Member as m).where.eq(m.name, name)
    }.map(rs => Member(rs)).single().apply()

    alice.orNull should not be null
  }
}

case class Member(id: Long, name: Option[String], createdAt: DateTime)

object Member extends SQLSyntaxSupport[Member] {

  override def tableName = "Members"

  def apply(rs: WrappedResultSet): Member =
    new Member(rs.long("id"),
                rs.stringOpt("name"),
                rs.jodaDateTime("created_at"))
}
