package debop4s.data.hibernate.repository

import com.mysema.query.jpa.hibernate.{HibernateUpdateClause, HibernateDeleteClause, HibernateQuery}
import com.mysema.query.types.EntityPath
import org.hibernate.{LockOptions, Session, SessionFactory}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{PageRequest, PageImpl}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import scala.annotation.varargs

/**
 * debop4s.data.hibernate.repository.QueryDslDao
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 5:10
 */
@Repository
@Transactional(readOnly = true)
class QueryDslDao {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val sessionFactory: SessionFactory = null

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
      case e: Throwable =>
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
      case e: Throwable =>
        log.error(s"삭제 작업 시 예외가 발생했습니다.", e)
        -1L
    }
  }


  def load[T](clazz: Class[T], id: java.io.Serializable): T =
    session.load(clazz, id).asInstanceOf[T]

  def load[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions) =
    session.load(clazz, id, lockOptions)

  def get[T](clazz: Class[T], id: java.io.Serializable) =
    session.get(clazz, id)

  def get[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions) =
    session.get(clazz, id, lockOptions)

  def findAll[T](path: EntityPath[T]) = getQuery.from(path).list(path)

  def findAll[T](path: EntityPath[T], query: HibernateQuery, offset: Int, limit: Int) =
    query.offset(offset).limit(limit).list(path)

  def getPage[T](path: EntityPath[T], query: HibernateQuery, pageNo: Int, pageSize: Int) = {
    val total = query.count()
    val offset = (pageNo - 1) * pageSize
    val limit = pageSize
    val items = findAll(path, query, offset, limit)

    new PageImpl(items, new PageRequest(pageNo, pageSize), total)
  }


}
