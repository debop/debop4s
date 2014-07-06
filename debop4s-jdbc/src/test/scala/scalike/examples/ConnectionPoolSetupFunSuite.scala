package scalike.examples

import org.joda.time.DateTime
import org.scalatest.{FunSuite, Matchers}
import org.slf4j.LoggerFactory
import scalikejdbc._
import scalikejdbc.config.DBsWithEnv


/**
 * ConnectionPoolSetupFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class ConnectionPoolSetupFunSuite extends FunSuite with Matchers {

  private val log = LoggerFactory.getLogger(getClass)

  DBsWithEnv("develop").setupAll()

  implicit val session = AutoSession

  sql"""
          create table connectionPools (
            id serial not null primary key,
            name varchar(64),
            created_at timestamp not null
          )
    """.execute().apply()

  Seq("Alice", "Bob", "Chris") foreach { name =>
    sql"insert into connectionPools(name, created_at) values($name, ${DateTime.now})".update().apply()
  }


  test("setup test") {

    val name = "Alice"
    val memberId = DB readOnly { implicit session =>
      sql"select id from connectionPools where name=${name}"
      .map(rs => rs.long("id"))
      .single()
      .apply()
    }
    log.debug(s"Alice id=${memberId.get}")
  }

}
