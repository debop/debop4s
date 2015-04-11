package debop4s.data.slick3.schema

import debop4s.data.slick3._
import debop4s.data.slick3.model.{SlickEntity, Versionable}
import slick.dbio.Effect.Write

/**
 * Slick Query 에 대한 확장 메소드를 제공하는 trait 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
trait SlickQueryExtensions {
  this: SlickComponent =>

  import driver.api._

  /**
   * Query method 를 제공하는 Extensions 입니다.
   * @param query `Table[M]` 에 대한 쿼리 객체 (에: TableQuery[User])
   * @tparam M Model 의 수형 (예: User)
   */
  abstract class BaseTableExtensions[M](query: TableQuery[_ <: Table[M]]) {
    def count: Int = query.length.exec
    def exists: Boolean = query.exists.exec
    def list: List[M] = query.exec.toList

    def page(pageIndex: Int = 0, pageSize: Int = 10): List[M] =
      query.drop(pageIndex * pageSize).take(pageSize).to[List].exec

    def save(model: M): M
    def saveAll(models: M*): List[M]
    def saveBatch(models: M*): Unit
    def deleteEntity(model: M): Boolean
  }


  /**
   * Id를 가지는 정보 (Entity) 를 위한 쿼리 확장 메소드를 제공합니다.
   * @param query `IdTable[E]` 에 대한 쿼리 객체
   * @tparam E  Entity 수형
   * @tparam Id Entity Identifier 의 수형
   */
  abstract class BaseIdTableExtensions[E, Id: BaseColumnType](query: TableQuery[_ <: Table[E] with TableWithId[Id]])
    extends BaseTableExtensions(query) {

    def extractId(entity: E): Option[Id]

    def withId(entity: E, id: Id): E

    def filterById(id: Id) = query.filter(_.id === id.bind)

    def filterByIdOption(idOpt: Option[Id]) =
      idOpt map { id => filterById(id) } getOrElse sys.error(s"idOpt=[$idOpt] should not be None.")

    def filterByIdIn(ids: Id*) = query.filter(_.id inSet ids)

    protected def autoInc = query returning query.map(_.id)

    def addAction(entity: E): driver.DriverAction[_, NoStream, Write] =
      autoInc into { case (e, id) => id } forceInsert entity

    def add(entity: E): Id =
      addAction(entity).exec.asInstanceOf[Id]


    def updateAction(entity: E): driver.DriverAction[_, NoStream, Write] =
      filterByIdOption(extractId(entity)).update(entity)

    def saveAction(entity: E): driver.DriverAction[_, NoStream, Write] = {
      extractId(entity) match {
        case Some(id) => filterById(id).update(entity)
        case None => addAction(entity)
      }
    }

    override def save(entity: E): E = {
      extractId(entity) match {
        case Some(id) => filterById(id).update(entity).exec; entity
        case None => withId(entity, add(entity))
      }
    }

    /**
     * 지정된 엔티티들을 저장 또는 갱신합니다.
     * @param entities
     * @return
     */
    override def saveAll(entities: E*): List[E] = {
      entities.map(save).toList
    }

    override def saveBatch(entities: E*): Unit = {
      entities.map { entity =>
        extractId(entity) match {
          case Some(id) => filterById(id).update(entity)
          case None => addAction(entity)
        }
      }.exec
    }

    def deleteEntityAction(entity: E): Option[driver.DriverAction[Int, NoStream, Write]] = {
      extractId(entity).map(id => deleteByIdAction(id))
    }

    override def deleteEntity(entity: E): Boolean =
      extractId(entity).exists(id => deleteById(id))

    def deleteById(id: Id): Boolean =
      filterById(id).delete.exec == 1

    def deleteByIdAction(id: Id): driver.DriverAction[Int, NoStream, Write] = {
      filterById(id).delete
    }

    def findById(id: Id): E = findOptionById(id).get
    def findOptionById(id: Id): Option[E] = filterById(id).exec.headOption.asInstanceOf[Option[E]]

    def compile = Compiled(query)

    def byId(id: Id) = query.filter(_.id === id.bind)

    /**
     * 특정 컬럼명에 특정 값에 따른 동적 쿼리를 생성합니다.
     * {{{
     *   users.byParam("email", "sunghyouk.bae@gmail.com")
     *        .sortDynamic("email.asc, registDate.desc")
     *        .paging(0, 10)
     * }}}
     * @param column 컬럼 명
     * @param value 조회하고자하는 컬럼의 값
     * @return
     */
    def byParam(column: String, value: Rep[String]) =
      query.withFilter(table => table.column[String](column) === value)
  }

  abstract class IdTableExtensions[E <: SlickEntity[Id], Id: BaseColumnType](query: TableQuery[_ <: Table[E] with TableWithId[Id]])
    extends BaseIdTableExtensions(query) {

    override def extractId(entity: E): Option[Id] = entity.id
    override def withId(entity: E, id: Id): E = entity.withId(id).asInstanceOf[E]

    def deleteAll(entities: E*): Boolean = {
      val ids = entities.flatMap(_.id).toSet
      filterByIdIn(ids.toSeq: _*).delete.exec == ids.size
    }

    def deleteAllById(ids: Id*) = {
      val idsets = ids.toSet
      filterByIdIn(idsets.toSeq: _*).delete.exec == idsets.size
    }
  }

  abstract class VersionableTableExtensions[E <: SlickEntity[Id] with Versionable, Id: BaseColumnType]
  (query: TableQuery[_ <: Table[E] with TableWithIdAndVersion[Id]])
    extends IdTableExtensions(query) {

    override def save(entity: E): E = {
      val currentVersion = entity.version
      val newEntity = entity.withVersion(currentVersion + 1).asInstanceOf[E]

      extractId(newEntity) match {
        case Some(id) =>
          val q = query.filter(_.id === id.bind).filter(_.version === currentVersion.bind)
          if (q.length.exec != 1)
            throw new StaleObjectStateException(entity)
          q.update(newEntity).exec
          newEntity
        case None =>
          withId(newEntity, add(newEntity))
      }
    }
  }

  class StaleObjectStateException[T <: Versionable](versionable: T)
    extends RuntimeException(s"Optimistic locking error = object in stale state: $versionable")

}
