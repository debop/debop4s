package scalike.examples.hello.models

import org.joda.time.DateTime
import scalikejdbc._

case class Company(id: Long,
                   name: String,
                   url: Option[String] = None,
                   createdAt: DateTime,
                   deletedAt: Option[DateTime] = None) {

  //  def save()(implicit session:DBSession = Company.autoSession): Company = Company.save(this)(session)
  //  def destroy()(implicit session:DBSession = Company.autoSession) { Company.destroy(id)(session) }
}

object Company extends SQLSyntaxSupport[Company] {

  def apply(c: SyntaxProvider[Company])(rs: WrappedResultSet): Company = apply(c.resultName)(rs)
  def apply(c: ResultName[Company])(rs: WrappedResultSet): Company =
    new Company(
                 id = rs.get(c.id),
                 name = rs.get(c.name),
                 url = rs.get(c.url),
                 createdAt = rs.get(c.createdAt),
                 deletedAt = rs.get(c.deletedAt)
               )

  val c = Company.syntax("c")
  private val isNotDeleted = sqls.isNull(c.deletedAt)

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Company] = withSQL {
    select
    .from(Company as c)
    .where.eq(c.id, id)
    .and.append(isNotDeleted)
  }.map(Company(c)).single().apply()

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Company] = withSQL {
    select
    .from(Company as c)
    .where.append(isNotDeleted).and.append(sqls"${where}")
    .orderBy(c.id)
  }.map(Company(c)).list().apply()

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    update(Company).set(column.deletedAt -> DateTime.now).where.eq(column.id, id)
  }.update().apply()

}
