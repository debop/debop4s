package debop4s.data.slick3.schema

import debop4s.data.slick3._
import debop4s.data.slick3.model.Identifiable
import shapeless.Lens
import slick.ast.BaseTypedType
import slick.dbio.{FailureAction, SuccessAction}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Slick Query 에 대한 확장 메소드를 제공하는 trait 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
trait SlickQueryExtensions {
  self: SlickComponent =>

  import driver.api._

  trait DeleteAll {
    this: TableQuery[_ <: Table[_]] =>

    def deleteAll(): DBIO[Int] = {
      this.filter(_ => LiteralColumn(true)).delete
    }
  }

  /**
   * Query method 를 제공하는 Extensions 입니다.
   */
  abstract class ActiveTableQuery[M, T <: Table[M]](cons: Tag => T) extends TableQuery(cons) {
    def count(): DBIO[Int] = this.size.result

    def save(model: M)(implicit ec: ExecutionContext): DBIO[M]
    def update(model: M)(implicit ec: ExecutionContext): DBIO[M]
    def delete(model: M)(implicit ec: ExecutionContext): DBIO[Unit]
  }


  /**
   * Id를 가지는 정보 (Entity) 를 위한 쿼리 확장 메소드를 제공합니다.
   * @tparam M  Entity 수형
   */
  abstract class TableWithIdQuery[M <: Identifiable, T <: EntityTable[M]](cons: Tag => T, idLens: Lens[M, Option[M#Id]])
                                                                         (implicit ev: BaseTypedType[M#Id])
    extends ActiveTableQuery[M, T](cons) {

    private def tryExtractId(model: M): DBIO[M#Id] = {
      idLens.get(model) match {
        case Some(id) => SuccessAction(id)
        case None => FailureAction(new RowNotFoundException(model))
      }
    }

    def filterById = this.findBy(x => x.id)

    def findById(id: M#Id): DBIO[M] = filterById(id).result.head

    def findOptionById(id: M#Id): DBIO[Option[M]] = filterById(id).result.headOption

    def add(model: M): DBIO[M#Id] = {
      this.returning(this.map(_.id)) += model
    }

    override def save(model: M)(implicit ec: ExecutionContext): DBIO[M] = {
      idLens.get(model) match {
        case Some(id) => update(id, model)
        case None => add(model).map { id => idLens.set(model)(Option(id)) }
      }
    }

    override def update(model: M)(implicit ec: ExecutionContext): DBIO[M] = {
      tryExtractId(model).flatMap { id => update(id, model) }
    }

    def update(id: M#Id, model: M)(implicit ec: ExecutionContext): DBIO[M] = {
      val triedUpdate = filterById(id).update(model).mustAffectOneSingleRow.asTry

      triedUpdate.map {
        case Success(_) => model
        case Failure(NoRowsAffectedException) => throw new RowNotFoundException(model)
        case Failure(e) => throw e
      }
    }

    override def delete(model: M)(implicit ec: ExecutionContext): DBIO[Unit] = {
      tryExtractId(model).flatMap { id =>
        deleteById(id)
      }
    }

    def deleteById(id: M#Id)(implicit ec: ExecutionContext): DBIO[Unit] = {
      filterById(id).delete.mustAffectOneSingleRow.map(_ => Unit)
    }

    def deleteAll()(implicit ec: ExecutionContext): DBIO[Unit] = {
      this.filter(_ => LiteralColumn(true)).delete.map(_ => Unit)
    }
  }

  implicit class UpdateActionExtensionMethods(dbAction: DBIO[Int]) {

    def mustAffectOneSingleRow(implicit ec: ExecutionContext): DBIO[Int] = {
      dbAction.flatMap {
        case 1 => dbAction
        case 0 => DBIO.failed(NoRowsAffectedException)
        case n if n > 1 => DBIO.failed(new TooManyRowsAffectedException(affectedRowCount = n, expectedRowCount = 1))
      }
    }

    def mustAffectAtLeastOneRow(implicit ec: ExecutionContext): DBIO[Int] = {
      dbAction.flatMap {
        case n if n >= 1 => dbAction
        case 0 => DBIO.failed(NoRowsAffectedException)
      }
    }
  }


}
