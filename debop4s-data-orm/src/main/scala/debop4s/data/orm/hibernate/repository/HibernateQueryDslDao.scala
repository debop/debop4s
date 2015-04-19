package debop4s.data.orm.hibernate.repository

import com.mysema.query.jpa.hibernate._
import com.mysema.query.types.EntityPath
import debop4s.data.orm.ReadOnlyConnection
import debop4s.data.orm.model.HConnectPageImpl
import org.hibernate.{LockOptions, Session, SessionFactory}
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest

import scala.annotation.varargs
import scala.util.control.NonFatal

/**
 * HibernateQueryDslDao
 * @author debop created at 2014. 5. 20.
 */
class HibernateQueryDslDao(val sessionFactory: SessionFactory) {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private def session: Session = sessionFactory.getCurrentSession

  private def sessionStateless = sessionFactory.openStatelessSession()

  def getQuery: HibernateQuery = new HibernateQuery(session)

  def from(path: EntityPath[_]): HibernateQuery = getQuery.from(path)

  @varargs
  def from(paths: EntityPath[_]*): HibernateQuery = getQuery.from(paths: _*)

  def deleteFrom(path: EntityPath[_]): HibernateDeleteClause =
    new HibernateDeleteClause(session, path)

  def updateFrom(path: EntityPath[_]): HibernateUpdateClause =
    new HibernateUpdateClause(session, path)


  def deleteStateless(path: EntityPath[_])(action: HibernateDeleteClause => Unit): Long = {
    try {
      val stateless = sessionFactory.openStatelessSession()
      val deleteClause = new HibernateDeleteClause(stateless, path)
      action(deleteClause)
      deleteClause.execute()
    } catch {
      case NonFatal(e) =>
        log.error(s"삭제 작업 시 예외가 발생했습니다.", e)
        -1L
    }
  }

  def updateStateless(path: EntityPath[_])(action: HibernateUpdateClause => Unit): Long = {
    try {
      val stateless = sessionFactory.openStatelessSession()
      val updateStateless = new HibernateUpdateClause(stateless, path)
      action(updateStateless)
      updateStateless.execute()
    } catch {
      case NonFatal(e) =>
        log.error(s"삭제 작업 시 예외가 발생했습니다.", e)
        -1L
    }
  }

  @ReadOnlyConnection
  def load[T](clazz: Class[T], id: java.io.Serializable): T =
    session.load(clazz, id).asInstanceOf[T]

  @ReadOnlyConnection
  def load[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions) =
    session.load(clazz, id, lockOptions)

  @ReadOnlyConnection
  def get[T](clazz: Class[T], id: java.io.Serializable) =
    session.get(clazz, id)

  @ReadOnlyConnection
  def get[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions) =
    session.get(clazz, id, lockOptions)

  @ReadOnlyConnection
  def findAll[T](path: EntityPath[T]) = getQuery.from(path).list(path)

  @ReadOnlyConnection
  def findAll[T](path: EntityPath[T], query: HibernateQuery, offset: Int, limit: Int) =
    query.offset(offset).limit(limit).list(path)

  @ReadOnlyConnection
  def getPage[T](path: EntityPath[T], query: HibernateQuery, pageNo: Int, pageSize: Int) = {
    val total = query.count()
    val offset = (pageNo - 1) * pageSize
    val limit = pageSize
    val items = findAll(path, query, offset, limit)

    new HConnectPageImpl(items, new PageRequest(pageNo, pageSize), total)
  }
}
