package debop4s.data.slick3.active

import debop4s.data.slick3.model.{IntEntity, Versionable}
import debop4s.data.slick3.schema.SlickComponent
import shapeless._

/**
 * ActiveDatabase
 * @author sunghyouk.bae@gmail.com
 */
object ActiveDatabase extends SlickComponent with ActiveQueryExtensions with ActiveSchema

trait ActiveQueryExtensions {
  self: SlickComponent with ActiveSchema =>

  import driver.api._

  implicit class SupplierExtensions(val model: Supplier) extends ActiveRecord[Supplier] {

    override def tableQuery = suppliers

    //    override def delete: Int = {
    //      (for {
    //        _ <- beers.filter(_.supplierId === self.id.bind).delete
    //        count <- suppliers.filter(_.id === self.id.bind).delete
    //      } yield count)
    //      .transactionally
    //      .exec
    //      .asInstanceOf[Int]
    //    }
  }

  implicit class BeerExtensions(val model: Beer) extends ActiveRecord[Beer] {

    override def tableQuery = beers

    def supplier: DBIO[Option[Supplier]] = suppliers.findOptionById(model.supplierId)

    def friendBeers: DBIO[Seq[Beer]] = {
      beers.filter(b => b.supplierId === model.supplierId.bind && b.id =!= model.id.bind).result
    }

    //    def save: Beer = beers.save(self)
    //    def delete: Boolean = beers.deleteEntity(self)
    //
    //    def supplier: Option[Supplier] =
    //      suppliers.findOptionById(self.supplierId)
    //
    //    def friendBeers: Seq[Beer] = {
    //      //      val q = for {
    //      //        (s, b) <- suppliers join beers on (_.id === _.supplierId) if s.id === self.supplierId.bind
    //      //      } yield b
    //      //
    //      //      db.result(q.to[Seq]).asInstanceOf[Seq[Beer]]
    //
    //      val q2 = beers.filter(b => b.supplierId === self.supplierId.bind && b.id =!= self.id.bind)
    //      q2.to[Seq].exec.asInstanceOf[Seq[Beer]]
    //    }
  }

}

trait ActiveSchema {
  self: SlickComponent =>

  import driver.api._

  // NOTE: Entity의 id 값은 항상 제일 뒤에 선언해야 합니다. (특히 Postgres 에서는)
  case class Beer(name: String,
                  price: Double,
                  supplierId: Int,
                  var id: Option[Int] = None) extends IntEntity

  case class Supplier(name: String,
                      var version: Long = 0,
                      var id: Option[Int] = None) extends Versionable {
    override type Id = Int
  }

  class Suppliers(tag: Tag) extends VersionableEntityTable[Supplier](tag, "active_suppliers") {
    def id = column[Int]("sup_id", O.PrimaryKey, O.AutoInc)
    def version = column[Long]("version", O.Default(0L))
    def name = column[String]("sup_name", O.Length(254, true))

    def * = (name, version, id.?) <>(Supplier.tupled, Supplier.unapply)

    def getBeers = beers.filter(_.supplierId === id)
  }
  lazy val suppliers = new VersionableEntityTableQuery[Supplier, Suppliers](
    cons = tag => new Suppliers(tag),
    idLens = lens[Supplier] >> 'id,
    versionLens = lens[Supplier] >> 'version
  )

  class Beers(tag: Tag) extends EntityTable[Beer](tag, "active_beers") {
    def id = column[Int]("beer_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("beer_name", O.Length(254, true))
    def price = column[Double]("beer_price")
    def supplierId = column[Int]("supplier_id")

    def * = (name, price, supplierId, id.?) <>(Beer.tupled, Beer.unapply)

    def supplierFK = foreignKey("fk_beers_suppliers", supplierId, suppliers)(_.id)
  }

  val idFunc = (beer: Beer) => beer.id

  lazy val beers = EntityTableQuery[Beer, Beers](
    cons = tag => new Beers(tag),
    idLens = lens[Beer] >> 'id
  )

  lazy val schema = suppliers.schema ++ beers.schema

  def createSchema(): DBIO[Unit] = {
    LOG.info(s"Create Active Database schema...")

    LOG.debug(s"Schema Drop:\n${ schema.dropStatements.mkString("\n") }")
    LOG.debug(s"Schema Create:\n${ schema.createStatements.mkString("\n") }")

    schema.drop.asTry >> schema.create
  }

  def dropSchema(): DBIO[Unit] = {
    LOG.info(s"Drop Active Database schema...")
    LOG.debug(s"Schema Drop:\n${ schema.dropStatements.mkString("\n") }")

    schema.drop
  }
}


