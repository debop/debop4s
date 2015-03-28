package debop4s.data.slick.active

import debop4s.data.slick.schema.SlickComponent

import scala.util.Try

/**
 * Active Database 를 사용하기 위해 Schema 와 QueryExtensions 를 제공합니다.
 */
object ActiveDatabase extends SlickComponent with QueryExtensions with Schema

/**
 *
 */
trait QueryExtensions {
  this: SlickComponent with Schema =>

  import driver.simple._

  implicit class SupplierQueryExtensions(query: TableQuery[Suppliers]) extends VersionableTableExtensions[Supplier, Int](query)

  implicit class SupplierExtensions(self: Supplier) {
    def save(implicit sess: Session): Supplier = suppliers.save(self)
    def delete(implicit sess: Session): Boolean = {
      val beerIds: Seq[Int] = beers.filter(_.supplierId === self.id.bind).map(_.id).list
      beerIds.foreach { id => beers.deleteById(id) }
      suppliers.deleteEntity(self)
    }
  }

  implicit class BeerQueryExtensions(query: TableQuery[Beers]) extends IdTableExtensions[Beer, Int](query)

  // 여러 함수를 제공할 때, 함수에 (implicit sess:Session)을 정의할 것인가? 클래스에 정의할 것인가?
  // 현재까지는 class 에 정의해도 큰 문제는 없다.
  implicit class BeerExtensions(self: Beer) {

    def save(implicit sess: Session): Beer = beers.save(self)
    def delete(implicit sess: Session): Boolean = beers.deleteEntity(self)

    def supplier(implicit sess: Session): Option[Supplier] = {
      suppliers.findOptionById(self.supplierId)
    }

    /** 지정한 Beer의 Supplier 가 공급하는 Beer 들을 조회합니다. */
    def friendBeers(implicit sess: Session): List[Beer] = {
      val q = for {
        (s, b) <- suppliers innerJoin beers on ( _.id === _.supplierId ) if s.id == self.supplierId.bind
      } yield b
      q.list
    }
  }

}

trait Schema {
  this: SlickComponent =>

  import driver.simple._

  class Suppliers(tag: Tag) extends IdVersionTable[Supplier, Int](tag, "active_suppliers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def version = column[Long]("version", O.Default(0L))
    def name = column[String]("supplierName", O.Length(255, varying = true))

    def * = (id.?, version, name) <>(Supplier.tupled, Supplier.unapply)

    def getBeers = beers.map(_.supplierId === id)
  }

  lazy val suppliers = TableQuery[Suppliers]

  class Beers(tag: Tag) extends IdTable[Beer, Int](tag, "active_beers") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("beerName", O.Length(255, varying = true))
    def price = column[Double]("beerPrice")
    def supplierId = column[Int]("supplierId")

    def * = (id.?, name, supplierId, price) <>(Beer.tupled, Beer.unapply)

    def supplier = foreignKey("fk_beer_supplier", supplierId, suppliers)(_.id, onDelete=ForeignKeyAction.Cascade)
  }

  lazy val beers = TableQuery[Beers]

  def createSchema(implicit session: Session) = {
    LOG.info(s"Create beer and supplier database schema...")

    val ddl = suppliers.ddl ++ beers.ddl

    LOG.debug(s"Schema Drop:\n${ ddl.dropStatements.mkString("\n") }")
    LOG.debug(s"Schema Create:\n${ ddl.createStatements.mkString("\n") }")

    Try { ddl.drop }
    ddl.create
  }
}
