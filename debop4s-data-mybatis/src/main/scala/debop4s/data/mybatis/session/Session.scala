package debop4s.data.mybatis.session

import java.util

import org.apache.ibatis.session.SqlSession

/**
 * Session
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
class Session(val sqls: SqlSession) {

  def selectOne[R](statement: String): R =
    sqls.selectOne(statement).asInstanceOf[R]

  def selectOne[P, R](statement: String, parameter: P): R =
    sqls.selectOne(statement, parameter).asInstanceOf[R]

  def selectList[R](statement: String): util.List[R] =
    sqls.selectList(statement)

  def selectList[P, R](statement: String, parameter: P): util.List[R] =
    sqls.selectList(statement, parameter)

  def selectList[P, R](statement: String, parameter: P, rowBounds: RowBounds): util.List[R] =
    sqls.selectList(statement, parameter, rowBounds.unwrap)

  def selectMap[K, V](statement: String, mapKey: String): util.Map[K, V] =
    sqls.selectMap(statement, mapKey)

  def selectMap[P, K, V](statement: String, parameter: P, mapKey: String): util.Map[K, V] =
    sqls.selectMap(statement, parameter, mapKey)

  def selectMap[P, K, V](statement: String, parameter: P, mapKey: String, rowBounds: RowBounds): util.Map[K, V] =
    sqls.selectMap(statement, parameter, mapKey, rowBounds.unwrap)

  def select[P](statement: String, handler: ResultHandler): Unit =
    sqls.select(statement, handler)

  def select[P](statement: String, parameter: P, handler: ResultHandler): Unit =
    sqls.select(statement, parameter, handler)

  def select[P](statement: String, parameter: P, rowBounds: RowBounds, handler: ResultHandler): Unit =
    sqls.select(statement, parameter, rowBounds.unwrap, handler)

  def insert(statement: String): Int =
    sqls.insert(statement)

  def insert[P](statement: String, parameter: P): Int =
    sqls.insert(statement, parameter)

  def update(statement: String): Int =
    sqls.update(statement)

  def update[P](statement: String, parameter: P): Int =
    sqls.update(statement, parameter)

  def delete(statement: String): Int =
    sqls.delete(statement)

  def delete[P](statement: String, parameter: P): Int =
    sqls.delete(statement, parameter)

  def commit(force: Boolean = false): Unit = sqls.commit(force)

  def rollback(force: Boolean = false): Unit = sqls.rollback(force)

  def clearCache(): Unit = sqls.clearCache()

  def flushStatements(): util.List[BatchResult] = sqls.flushStatements()
}
