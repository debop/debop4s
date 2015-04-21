package debop4s.data.slick3.schema

import debop4s.data.slick3.model.{Versionable, Identifiable}


/**
 * SlickSchema
 * @author sunghyouk.bae@gmail.com
 */
trait SlickSchema {
  this: SlickProfile =>

  import driver.api._

  /** 자동 증가형 Id 컬럼을 가진 Table Schema 를 표현하는 trait */
  trait IdColumn[Id] {
    def id: Rep[Id]
  }

  /**
   * Identifer 컬럼을 가진 테이블을 정의할 때 사용하는 클래스입니다.
   */
  abstract class IdTable[E, Id](tag: Tag, schemaName: Option[String], tableName: String)
                               (implicit colType: BaseColumnType[Id])
    extends Table[E](tag, schemaName, tableName) with IdColumn[Id] {

    def this(tag: Tag, tableName: String)(implicit colType: BaseColumnType[Id]) =
      this(tag, None, tableName)
  }

  /** Version 정보를 가진 Table 에 대한 Schema 를 정의하는 trait 입니다. */
  trait VersionColumn {
    def version: Rep[Long]
  }

  /** Id와 Version 정보를 가진 Table 에 대한 Schema 를 정의하는 클래스입니다. */
  abstract class IdVersionTable[M, Id](tag: Tag, schemaName: Option[String], tableName: String)
                                      (implicit val colType: BaseColumnType[Id])
    extends IdTable[M, Id](tag, schemaName, tableName)(colType) with VersionColumn {

    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[Id]) =
      this(tag, None, tableName)
  }

  type EntityTable[M <: Identifiable] = IdTable[M, M#Id]

  type VersionableEntityTable[M <: Versionable] = IdVersionTable[M, M#Id]

}
