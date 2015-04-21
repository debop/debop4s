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
    LOG.info("Schema used to create tables:")
    schema.createStatements.foreach { s => LOG.debug("\t" + s) }

    db.exec {
      schema.drop.asTry >>
      schema.create
    }

    // TODO: Action 과 ResultSetAction 에 대한 Extensions 를 추가해야 겠다.
    LOG.info("Type info from Database Meta Data: ")
    MTypeInfo.getTypeInfo.exec foreach { typ => LOG.debug("\t" + typ) }

    db.exec {
      ifCap(tcap.jdbcMetaGetFunctions) {
        LOG.info("Functions from Database Meta Data: ")
        MFunction.getFunctions(MQName.local("%")).flatMap { (fs: Vector[MFunction]) =>
          DBIO.sequence(fs.map(_.getFunctionColumns()))
        }
      }
    }

    LOG.info("Functions from Database Meta Data: ")
    ifCap(tcap.jdbcMetaGetFunctions) {
      val getFunctionsAction = MFunction.getFunctions(MQName.local("%"))
      val fs = getFunctionsAction.exec
      fs.foreach { f =>
        LOG.debug("\t" + f)
        val cs = f.getFunctionColumns().exec
        cs.foreach { c => LOG.debug("\t\t" + c) }
      }
      getFunctionsAction
    }


    LOG.info("UDTs from Database Meta Data: ")
    MUDT.getUDTs(MQName.local("%")).exec foreach { u => LOG.debug("\t" + u) }

    LOG.info("Procedures from Database Meta Data:")
    MProcedure.getProcedures(MQName.local("%")).exec foreach { p =>
      LOG.debug("\t" + p)
      p.getProcedureColumns().exec.foreach { c => LOG.debug("\t\t" + c) }
    }

    LOG.info("Association Schema from Database Meta Data:")
    MTable.getTables(None, None, None, None)
    .exec
    .filter(t => Set("users_xx", "orders_xx") contains t.name.name)
    .foreach { t =>
      LOG.debug("\t" + t)
      t.getColumns.exec.foreach { c =>
        LOG.debug("\t\t" + c)
        c.getColumnPrivileges.exec foreach { p => LOG.debug("\t\t\t" + p) }
      }

      t.getVersionColumns.exec foreach { v => LOG.debug("\t\t\t" + v) }
      t.getPrimaryKeys.exec foreach { pk => LOG.debug("\t\t\t" + pk) }
      t.getImportedKeys.exec foreach { ik => LOG.debug("\t\t\t" + ik) }
      t.getExportedKeys.exec foreach { ek => LOG.debug("\t\t\t" + ek) }

      Try {
        t.getIndexInfo().exec foreach { ii => LOG.debug("\t\t\t" + ii) }
      }
      t.getTablePrivileges.exec foreach { p => LOG.debug("\t\t\t" + p) }

      t.getBestRowIdentifier(MBestRowIdentifierColumn.Scope.Session).exec foreach { c =>
        LOG.debug("\t\t\t Row identifier for session: " + c)
      }
    }

    LOG.info("Schema from Database Meta Data:")
    MSchema.getSchemas.exec foreach { s => LOG.debug("\t" + s) }

    LOG.info("Client Info Properties from Database Meta Data:")
    MClientInfoProperty.getClientInfoProperties.exec.foreach { c => LOG.debug("\t" + c) }

    MTable.getTables(None, None, None, None).exec.map(_.name.name) should contain allOf("users_xx", "orders_xx")

    schema.drop.exec
  }
}
