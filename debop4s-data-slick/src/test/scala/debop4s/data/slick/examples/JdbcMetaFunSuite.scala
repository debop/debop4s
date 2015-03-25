package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.slick.jdbc.meta._
import scala.util.Try

/**
 * Database Meta 정보 추출을 다루는 예제
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMetaFunSuite extends AbstractSlickFunSuite {

  class Users(tag: Tag) extends Table[(Int, String, Option[String])](tag, "meta_users_xx") {
    def id = column[Int]("id", O.PrimaryKey)
    def first = column[String]("first", O.Default("NFN"), O.DBType("varchar(64)"))
    def last = column[Option[String]]("last")
    def * = (id, first, last)
  }
  lazy val users = TableQuery[Users]

  class Orders(tag: Tag) extends Table[(Int, Int, String, Boolean, Option[Boolean])](tag, "meta_orders_xx") {
    def userId = column[Int]("userId")
    def orderId = column[Int]("orderId")
    def product = column[String]("product")
    def shipped = column[Boolean]("shipped", O.Default(false))
    def rebate = column[Option[Boolean]]("rebate", O.Default(Some(false)))

    def * = (userId, orderId, product, shipped, rebate)
    def userFK = foreignKey("fk_meta_orders_users", userId, users)(_.id)
  }
  lazy val orders = TableQuery[Orders]

  test("meta") {
    val ddl = users.ddl ++ orders.ddl
    println("DDL used to create tables: ")
    for (s <- ddl.createStatements)
      println("\t" + s)

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      println("Type info from Database Meta Data: ")
      MTypeInfo.getTypeInfo.foreach { typ => println("\t" + typ) }

      Try {
        println("Functions from Database Meta Data: ")
        MFunction.getFunctions(MQName.local("%")).foreach { f =>
          println("\t" + f)
          f.getFunctionColumns().foreach { c => println("\t\t" + c) }
        }
      }

      println("UDTs from Database Meta Data:")
      MUDT.getUDTs(MQName.local("%")).foreach { u => println("\t" + u) }

      println("Procedures from Database Meta Data:")
      MProcedure.getProcedures(MQName.local("%")).foreach { p =>
        println("\t" + p)
        p.getProcedureColumns().foreach { c =>
          println("\t\t" + c)
        }
      }

      println("Association Schema from Database Meta Data:")
      for (t <- MTable.getTables(None, None, None, None).list if Set("meta_users_xx", "meta_orders_xx") contains t.name.name) {
        println("\t" + t)
        t.getColumns.foreach { c =>
          println("\t\t" + c)
          c.getColumnPrivileges foreach { p => println("\t\t\t" + p) }
        }
        t.getVersionColumns foreach { v => println("\t\t\t" + v) }
        t.getPrimaryKeys foreach { pk => println("\t\t\t" + pk) }
        t.getImportedKeys foreach { ik => println("\t\t\tImported " + ik) }
        t.getExportedKeys foreach { ek => println("\t\t\tExported ", ek) }

        Try {
          t.getIndexInfo() foreach { ii => println("\t\t\t" + ii) }
        }
        t.getTablePrivileges foreach { p => println("\t\t\t" + p) }

        t.getBestRowIdentifier(MBestRowIdentifierColumn.Scope.Session) foreach { c =>
          println("\t\t\t Row identifier for session: " + c)
        }
      }

      print("Schema from Database Meta Data:")
      MSchema.getSchemas foreach { s => println("\t" + s) }

      println("Client Info Properties from Database Meta Data:")
      MClientInfoProperty.getClientInfoProperties foreach { c => println("\t" + c) }

      MTable.getTables(None, None, None, None).list.map(_.name.name) should contain allOf("meta_users_xx", "meta_orders_xx")
    }
  }

}
