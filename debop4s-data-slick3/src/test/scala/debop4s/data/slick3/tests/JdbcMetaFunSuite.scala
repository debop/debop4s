package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
import slick.jdbc.meta._

import scala.concurrent.ExecutionContext.Implicits.global
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

    db.exec { schema.drop.asTry >> schema.create }

    // TODO: Action 과 ResultSetAction 에 대한 Extensions 를 추가해야 겠다.
    LOG.info("Type info from Database Meta Data: ")
    db.exec(MTypeInfo.getTypeInfo) foreach { typ => LOG.debug("\t" + typ) }

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
      val fs = db.exec { getFunctionsAction }
      fs.foreach { f =>
        LOG.debug("\t" + f)
        val cs = db.exec { f.getFunctionColumns() }
        cs.foreach { c =>
          LOG.debug("\t\t" + c)
        }
      }
      getFunctionsAction
    }


    LOG.info("UDTs from Database Meta Data: ")
    db.exec(MUDT.getUDTs(MQName.local("%"))) foreach { u => LOG.debug("\t" + u) }

    LOG.info("Procedures from Database Meta Data:")
    db.exec(MProcedure.getProcedures(MQName.local("%"))).foreach { p =>
      LOG.debug("\t" + p)
      db.exec(p.getProcedureColumns()).foreach { c =>
        LOG.debug("\t\t" + c)
      }
    }

    LOG.info("Association Schema from Database Meta Data:")
    db.exec(MTable.getTables(None, None, None, None))
    .filter(t => Set("users_xx", "orders_xx") contains t.name.name)
    .foreach { t =>
      LOG.debug("\t" + t)
      db.exec(t.getColumns).foreach { c =>
        LOG.debug("\t\t" + c)
        db.exec(c.getColumnPrivileges) foreach { p => LOG.debug("\t\t\t" + p) }
      }

      db.exec(t.getVersionColumns) foreach { v => LOG.debug("\t\t\t" + v) }
      db.exec(t.getPrimaryKeys) foreach { pk => LOG.debug("\t\t\t" + pk) }
      db.exec(t.getImportedKeys) foreach { ik => LOG.debug("\t\t\t" + ik) }
      db.exec(t.getExportedKeys) foreach { ek => LOG.debug("\t\t\t" + ek) }

      Try {
        db.exec(t.getIndexInfo()) foreach { ii => LOG.debug("\t\t\t" + ii) }
      }
      db.exec(t.getTablePrivileges) foreach { p => LOG.debug("\t\t\t" + p) }

      db.exec(t.getBestRowIdentifier(MBestRowIdentifierColumn.Scope.Session)) foreach { c =>
        LOG.debug("\t\t\t Row identifier for session: " + c)
      }
    }

    LOG.info("Schema from Database Meta Data:")
    db.exec(MSchema.getSchemas) foreach { s => LOG.debug("\t" + s) }

    LOG.info("Client Info Properties from Database Meta Data:")
    db.exec(MClientInfoProperty.getClientInfoProperties).foreach { c => LOG.debug("\t" + c) }

    db.exec(MTable.getTables(None, None, None, None)).map(_.name.name) should contain allOf("users_xx", "orders_xx")

    db.exec { schema.drop }
  }
}
