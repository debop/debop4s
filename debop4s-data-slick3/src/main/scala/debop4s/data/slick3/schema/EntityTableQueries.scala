package debop4s.data.slick3.schema

import debop4s.data.slick3.model.{Identifiable, Versionable}
import debop4s.data.slick3.{NoRowsAffectedException, StaleObjectStateException}
import shapeless.Lens
import slick.ast.BaseTypedType

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * EntityTableQueries
 * @author sunghyouk.bae@gmail.com
 */
trait EntityTableQueries {
  self: SlickProfile with SlickSchema with SlickQueryExtensions =>

  import driver.api._

  class EntityTableQuery[M <: Identifiable, T <: EntityTable[M]](cons: Tag => T, idLens: Lens[M, Option[M#Id]])
                                                                (implicit ev: BaseTypedType[M#Id])
    extends TableWithIdQuery[M, T](cons, idLens)


  object EntityTableQuery {

    def apply[M <: Identifiable, T <: EntityTable[M]]
    (cons: Tag => T, idLens: Lens[M, Option[M#Id]])(implicit ev: BaseTypedType[M#Id]) =
      new EntityTableQuery[M, T](cons, idLens)
  }

  class VersionableEntityTableQuery[M <: Versionable, T <: VersionableEntityTable[M]]
  (cons: Tag => T, idLens: Lens[M, Option[M#Id]], versionLens: Lens[M, Long])
  (implicit ev: BaseTypedType[M#Id])
    extends EntityTableQuery[M, T](cons, idLens) {

    override protected def update(id: M#Id, versionable: M)(implicit ec: ExecutionContext): DBIO[M] = {

      // extract current version
      val currentVersion = versionLens.get(versionable)

      // build a query selecting entity with current version
      val queryByIdAndVersion = filterById(id).map { query =>
        query.filter(_.version === currentVersion)
      }

      // model with incremented version
      val modelWithNewVersion = versionLens.set(versionable)(currentVersion + 1)

      val tryUpdate = queryByIdAndVersion.update(modelWithNewVersion).mustAffectOneSingleRow.asTry

      // in case of failure, we want a more meamingful exception ie: StaleObjectStateException
      tryUpdate.flatMap {
        case Success(_) => DBIO.successful(modelWithNewVersion)
        case Failure(NoRowsAffectedException) =>
          findById(id).flatMap { currentOnDb =>
            DBIO.failed(new StaleObjectStateException(versionable, currentOnDb))
          }
        case Failure(e) => DBIO.failed(e)
      }
    }

    override def save(versionable: M)(implicit ec: ExecutionContext): DBIO[M] = {
      idLens.get(versionable) match {
        // Id 가 있다면, 갱신한다.
        case Some(id) => update(id, versionable)

        // Id 가 없다면, 새로 추가한다.
        case None =>
          // initialize versioning
          val modelWithVersion = versionLens.set(versionable)(1)
          add(modelWithVersion).map { id => idLens.set(modelWithVersion)(Option(id)) }
      }
    }
  }

  object VersionableEntityTableQuery {

    def apply[M <: Versionable, T <: VersionableEntityTable[M]]
    (cons: Tag => T, idLens: Lens[M, Option[M#Id]], versionLens: Lens[M, Long])
    (implicit ev1: BaseColumnType[M#Id]) = {
      new VersionableEntityTableQuery[M, T](cons, idLens, versionLens)
    }
  }
}
