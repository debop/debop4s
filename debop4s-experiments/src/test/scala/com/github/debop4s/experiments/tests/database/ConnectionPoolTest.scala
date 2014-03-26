package com.github.debop4s.experiments.tests.database

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import org.joda.time._
import scalikejdbc.SQLInterpolation._
import scalikejdbc._

/**
 * ConnectionPoolTest
 * @author Sunghyouk Bae
 */
class ConnectionPoolTest extends AbstractExperimentTest {

    case class Member(id: Long, name: Option[String], createdAt: DateTime)

    object Member extends SQLSyntaxSupport[Member] {
        override def tableName: String = "members"

        def apply(rs: WrappedResultSet): Member =
            new Member(rs.long("id"), rs.stringOpt("name"), rs.dateTime("created_at"))

        def apply(m: SyntaxProvider[Member])(rs: WrappedResultSet): Member =
            apply(m.resultName)(rs)

        def apply(m: ResultName[Member])(rs: WrappedResultSet): Member =
            new Member(rs.long(m.id), rs.stringOpt(m.name), rs.dateTime(m.createdAt))
    }

    test("First sample") {
        Class.forName("org.h2.Driver")
        ConnectionPool.singleton("jdbc:h2:mem:hello", "sa", "")

        val db = DB(ConnectionPool.borrow())

        using(DB(ConnectionPool.borrow())) { db =>
            db.newTx.begin()
            db.withinTx { implicit session =>
                sql"""
                create table members (
                    id serial not null primary key,
                    name varchar(64),
                    created_at timestamp not null
                )
                """.execute().apply()

                Seq("Alice", "Bob", "Chris") foreach { name =>
                    sql"insert into members(name, created_at) values($name, current_timestamp)".update().apply()
                }
            }
            db.tx.commit()

            db.readOnly { implicit session =>
                val entities: List[Map[String, Any]] = sql"select * from members".map(_.toMap()).list().apply()
                val members: List[Member] = sql"select * from members".map(rs => Member(rs)).list().apply()

                assert(members != null)
                assert(members.size == 3)

                val m = Member.syntax("m")
                val members2 = withSQL {select.from(Member as m)}.map(Member(m)).list().apply()

                assert(members2 != null)
                assert(members2.size == 3)
            }
        }
    }
}
