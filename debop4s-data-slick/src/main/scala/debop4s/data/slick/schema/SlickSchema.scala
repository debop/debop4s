package debop4s.data.slick.schema

/**
 * Dtabase Schema 작업을 수행하는 trait 입니다.
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickSchema {
  this: SlickProfile =>

  import driver.simple._

  /** 자동 증가형 Id 컬럼을 가진 Table Schema 를 표현하는 trait */
  trait TableWithId[Id] {
    def id: Column[Id]
  }

  /**
   * Identifer 컬럼을 가진 테이블을 정의할 때 사용하는 클래스입니다.
   */
  abstract class IdTable[E, Id](tag: Tag, schemaName: Option[String], tableName: String)
                               (implicit colType: BaseColumnType[Id])
    extends Table[E](tag, schemaName, tableName) with TableWithId[Id] {

    def this(tag: Tag, tableName: String)(implicit colType: BaseColumnType[Id]) = this(tag, None, tableName)
  }

  /** Version 정보를 가진 Table 에 대한 Schema 를 정의하는 trait 입니다. */
  trait TableWithVersion {
    def version: Column[Long]
  }

  /** Version 정보를 가진 Table 에 대한 Schema 를 정의하는 클래스입니다. */
  abstract class VersionTable[E](tag: Tag, schemaName: Option[String], tableName: String)
                                (implicit colType: BaseColumnType[Long])
    extends Table[E](tag, schemaName, tableName) with TableWithVersion {

    def this(tag: Tag, tableName: String)(implicit colType: BaseColumnType[Long]) = this(tag, None, tableName)
  }

  trait TableWithIdAndVersion[Id] extends TableWithId[Id] with TableWithVersion

  /** Id와 Version 정보를 가진 Table 에 대한 Schema 를 정의하는 클래스입니다. */
  abstract class IdVersionTable[E, Id](tag: Tag, schemaName: Option[String], tableName: String)
                                      (implicit colType: BaseColumnType[Id])
    extends Table[E](tag, schemaName, tableName) with TableWithIdAndVersion[Id] {

    def this(tag: Tag, tableName: String)(implicit colType: BaseColumnType[Id]) = this(tag, None, tableName)
  }
}
