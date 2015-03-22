package debop4s.data.slick.schema

import debop4s.data.slick.model.{ SlickEntity, Versionable }

/**
 * Slick Query 에 대한 확장 메소드를 제공하는 trait 입니다.
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickQueryExtensions {
  this: SlickSchema with SlickProfile =>

  import driver.simple._

  /**
   * Query method 를 제공하는 Extensions 입니다.
   * @param query `Table[M]` 에 대한 쿼리 객체 (에: TableQuery[User])
   * @tparam M Model 의 수형 (예: User)
   */
  abstract class BaseTableExtensions[M](query: TableQuery[_ <: Table[M]]) {
    def count(implicit session: Session): Int = query.length.run
    def exists(implicit session: Session): Boolean = query.exists.run
    def list(implicit session: Session): List[M] = query.list
    def page(pageIndex: Int = 0, pageSize: Int = 10)(implicit session: Session): List[M] =
      query.drop(pageIndex * pageSize).take(pageSize).run.toList

    def save(model: M)(implicit session: Session): M
    def saveAll(models: M*)(implicit session: Session): List[M]
    def delete(model: M)(implicit session: Session): Boolean
  }

  abstract class BaseIdTableExtensions[E, Id: BaseColumnType](query: TableQuery[_ <: Table[E] with TableWithId[Id]])
    extends BaseTableExtensions(query) {

    def extractId(entity: E): Option[Id]
    def withId(entity: E, id: Id): E
    def filterById(id: Id) = query.filter(_.id === id.bind)
    def filterByIdOption(idOpt: Option[Id]) =
      idOpt map { id => filterById(id) } getOrElse sys.error(s"idOpt=[$idOpt] should not be None.")

    def filterByIdIn(ids: Id*) = query.filter(_.id inSet ids)

    protected def autoInc = query returning query.map(_.id)

    def add(entity: E)(implicit session: Session): Id =
      ( autoInc into { case (entity, id) => id } insert entity ).asInstanceOf[Id]

    override def save(entity: E)(implicit session: Session): E = {
      extractId(entity) match {
        case Some(id) => filterById(id).update(entity); entity
        case None => withId(entity, add(entity))
      }
    }

    override def saveAll(entities: E*)(implicit session: Session): List[E] =
      entities.map(save).toList

    override def delete(entity: E)(implicit session: Session): Boolean =
      extractId(entity).exists(id => deleteById(id))

    def deleteById(id: Id)(implicit session: Session): Boolean =
      filterById(id).delete == 1

    def findById(id: Id)(implicit session: Session): E = findOptionById(id).get
    def findOptionById(id: Id)(implicit session: Session): Option[E] = filterById(id).firstOption

    def compile = Compiled(query)

    def byId(id: Id) = query.filter(_.id === id.bind)

    def byParam(column: String, value: Column[String]) =
      query.withFilter(table => table.column[String](column) == value)
  }

  abstract class IdTableExtensions[E <: SlickEntity[Id], Id: BaseColumnType](query: TableQuery[_ <: Table[E] with TableWithId[Id]])
    extends BaseIdTableExtensions(query) {

    override def extractId(entity: E): Option[Id] = entity.id.asInstanceOf[Option[Id]]
    override def withId(entity: E, id: Id): E = entity.withId(id).asInstanceOf[E]

    def deleteAll(entities: E*)(implicit session: Session): Boolean = {
      val ids = entities.flatMap(_.id).toSet
      filterByIdIn(ids.toSeq: _*).delete == ids.size
    }
  }

  abstract class VersionableTableExtensions[E <: SlickEntity[Id] with Versionable, Id: BaseColumnType]
  (query: TableQuery[_ <: Table[E] with TableWithId[Id] with TableWithVersion])
    extends IdTableExtensions(query) {

    override def save(entity: E)(implicit session: Session): E = {
      val currentVersion = entity.version
      val newEntity = entity.withVersion(currentVersion + 1).asInstanceOf[E]

      extractId(newEntity) match {
        case Some(id) =>
          val q = query.filter(_.id === id.bind).filter(_.version === currentVersion.bind)
          if (q.length.run != 1)
            throw new StaleObjectStateException(entity)
          q.update(newEntity)
          newEntity
        case None =>
          withId(newEntity, add(newEntity))
      }
    }
  }

  class StaleObjectStateException[T <: Versionable](versionable: T)
    extends RuntimeException(s"Optimistic locking error = object in stale state: $versionable")


}
