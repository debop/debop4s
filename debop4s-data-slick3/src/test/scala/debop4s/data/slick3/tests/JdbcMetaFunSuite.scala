package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
import slick.jdbc.meta._

import scala.util.Try

/**
 * 메타데이터 정보 얻기
 * @author sunghyouk.bae@gmail.com
 */
class JdbcMetaFunSuite extends AbstractSlickFunSuite {

  class Users(tag: Tag) extends Table[(Int, String, Option[String])](tag, "users_xx") {
    def id = column[Int]("id", O.PrimaryKey)
    def first = column[String]("first", O Default "NFN", O SqlType "VARCHAR(64)")
    def last = column[Option[String]]("last", O SqlType "VARCHAR(255)")
    def * = (id, first, last)
  }
  lazy val users = TableQuery[Users]

  class Orders(tag: Tag) extends Table[(Int, Int, String, Boolean, Option[Boolean])](tag, "orders_xx") {
    def userId = column[Int]("userId")
    def orderId = column[Int]("orderId", O.PrimaryKey)
    def product = column[String]("product")
    def shipped = column[Boolean]("shipped", O Default false)
    def rebate = column[Option[Boolean]]("rebate", O Default Some(false))
    def * = (userId, orderId, product, shipped, rebate)
    def userFK = foreignKey("fk_orders_user", userId, users)(_.id)
  }
  lazy val orders = TableQuery[Orders]

  test("meta sync operation") {
    val schema = users.schema ++ orders.schema
    log.info("Schema used to create tables:")
    schema.createStatements.foreach { s => log.debug("\t" + s) }

    db.exec {
      schema.drop.asTry >>
      schema.create
    }

    // TODO: Action 과 ResultSetAction 에 대한 Extensions 를 추가해야 겠다.
    log.info("Type info from Database Meta Data: ")
    MTypeInfo.getTypeInfo.exec foreach { typ => log.debug("\t" + typ) }

    db.exec {
      ifCap(tcap.jdbcMetaGetFunctions) {
        log.info("Functions from Database Meta Data: ")
        MFunction.getFunctions(MQName.local("%")).flatMap { (fs: Vector[MFunction]) =>
          DBIO.sequence(fs.map(_.getFunctionColumns()))
        }
      }
    }

    log.info("Functions from Database Meta Data: ")
    ifCap(tcap.jdbcMetaGetFunctions) {
      val getFunctionsAction = MFunction.getFunctions(MQName.local("%"))
      val fs = getFunctionsAction.exec
      fs.foreach { f =>
        log.debug("\t" + f)
        val cs = f.getFunctionColumns().exec
        cs.foreach { c => log.debug("\t\t" + c) }
      }
      getFunctionsAction
    }


    log.info("UDTs from Database Meta Data: ")
    MUDT.getUDTs(MQName.local("%")).exec foreach { u => log.debug("\t" + u) }

    log.info("Procedures from Database Meta Data:")
    MProcedure.getProcedures(MQName.local("%")).exec foreach { p =>
      log.debug("\t" + p)
      p.getProcedureColumns().exec.foreach { c => log.debug("\t\t" + c) }
    }

    log.info("Association Schema from Database Meta Data:")
    MTable.getTables(None, None, None, None)
    .exec
    .filter(t => Set("users_xx", "orders_xx") contains t.name.name)
    .foreach { t =>
      log.debug("\t" + t)
      t.getColumns.exec.foreach { c =>
        log.debug("\t\t" + c)
        c.getColumnPrivileges.exec foreach { p => log.debug("\t\t\t" + p) }
      }

      t.getVersionColumns.exec foreach { v => log.debug("\t\t\t" + v) }
      t.getPrimaryKeys.exec foreach { pk => log.debug("\t\t\t" + pk) }
      t.getImportedKeys.exec foreach { ik => log.debug("\t\t\t" + ik) }
      t.getExportedKeys.exec foreach { ek => log.debug("\t\t\t" + ek) }

      Try {
        t.getIndexInfo().exec foreach { ii => log.debug("\t\t\t" + ii) }
      }
      t.getTablePrivileges.exec foreach { p => log.debug("\t\t\t" + p) }

      t.getBestRowIdentifier(MBestRowIdentifierColumn.Scope.Session).exec foreach { c =>
        log.debug("\t\t\t Row identifier for session: " + c)
      }
    }

    log.info("Schema from Database Meta Data:")
    MSchema.getSchemas.exec foreach { s => log.debug("\t" + s) }

    log.info("Client Info Properties from Database Meta Data:")
    MClientInfoProperty.getClientInfoProperties.exec.foreach { c => log.debug("\t" + c) }

    MTable.getTables(None, None, None, None).exec.map(_.name.name) should contain allOf("users_xx", "orders_xx")

    schema.drop.exec
  }
}
