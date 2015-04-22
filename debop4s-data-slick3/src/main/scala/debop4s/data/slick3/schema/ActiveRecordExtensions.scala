package debop4s.data.slick3.schema

import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

/**
 * ActiveRecordExtensions
 * @author sunghyouk.bae@gmail.com
 */
trait ActiveRecordExtensions {
  self: SlickQueryExtensions with SlickProfile =>

  trait ActiveRecord[M] {

    type TableQuery = ActiveTableQuery[M, _]

    def tableQuery: TableQuery
    def model: M

    def save()(implicit ec: ExecutionContext): DBIO[M] = tableQuery.save(model)

    def update()(implicit ec: ExecutionContext): DBIO[M] = tableQuery.update(model)

    def delete()(implicit ec: ExecutionContext): DBIO[Unit] = tableQuery.delete(model)
  }

}
