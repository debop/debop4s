package debop4s.data.mybatis.session

import org.apache.ibatis.session._

import scala.util.control.NonFatal

/**
 * SessionManager
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
class SessionManager(factory: SqlSessionFactory) {

  type Callback[T] = (Session) => T
  type CloseSessionHook = (SqlSession) => Unit

  private var closeSession: CloseSessionHook = (s: SqlSession) => s.close()

  def closeSessionHook(hook: CloseSessionHook): Unit = {
    if (hook != null)
      closeSession = hook
  }

  def readOnly[T](executorType: ExecutorType = ExecutorType.SIMPLE,
                  level: TransactionIsolationLevel = TransactionIsolationLevel.UNDEFINED)
                 (callback: Callback[T]): T = {
    val sqls = factory.openSession(executorType.unwrap, level.unwrap)
    try {
      val ret = callback(new Session(sqls))
      sqls.rollback()
      ret
    } finally {
      closeSession(sqls)
    }
  }

  def readOnly[T](callback: Callback[T]): T = readOnly()(callback)

  def transaction[T](sqlSession: SqlSession)(callback: Callback[T]): T = {
    try {
      val result = callback(new Session(sqlSession))
      sqlSession.commit()
      result
    } catch {
      case NonFatal(e) =>
        sqlSession.rollback()
        throw e
    } finally {
      closeSession(sqlSession)
    }
  }

  def transaction[T](executorType: ExecutorType = ExecutorType.SIMPLE,
                     level: TransactionIsolationLevel = TransactionIsolationLevel.UNDEFINED)
                    (callback: Callback[T]): T =
    transaction(factory.openSession(executorType.unwrap, level.unwrap))(callback)

  def transaction[T](executorType: ExecutorType = ExecutorType.SIMPLE)
                    (callback: Callback[T]): T =
    transaction(factory.openSession(executorType.unwrap))(callback)

  def transaction[T](executorType: ExecutorType = ExecutorType.SIMPLE, autoCommit: Boolean)
                    (callback: Callback[T]): T =
    transaction(factory.openSession(executorType.unwrap, autoCommit))(callback)

  def transaction[T](autoCommit: Boolean)(callback: Callback[T]): T =
    transaction(factory.openSession(autoCommit))(callback)

  def transaction[T](callback: Callback[T]): T =
    transaction(factory.openSession())(callback)

  def managed[T](executorType: ExecutorType)(callback: Callback[T]): T = {
    val sqls = factory.openSession(executorType.unwrap)
    assert(sqls != null, "session이 열리지 않았습니다.")
    try {
      callback(new Session(sqls))
    } finally {
      closeSession(sqls)
    }
  }

  def managed[T](callback: Callback[T]): T = managed[T](ExecutorType.SIMPLE)(callback)
}
