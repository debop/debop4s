package debop4s.data

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.data.slick3.SlickContext._
import debop4s.data.slick3.SlickContext.driver.api._
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.slf4j.LoggerFactory

import scala.collection.generic.CanBuildFrom
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.postfixOps
import scala.util.control.NonFatal
import scala.util.{Failure, Success}


/**
 * package
 * @author sunghyouk.bae@gmail.com
 */
package object slick3 {

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  implicit class DatabaseExtensions(db: SlickContext.driver.backend.DatabaseDef) {

    /** 동기 방식으로 action 을 수행합니다. */
    def exec[@miniboxed R](action: DBIOAction[R, NoStream, Nothing]): R = {
      action.exec(db)
      // db.run(action).await
    }

    def result[E, U, Seq[_]](query: Query[E, U, Seq]): Seq[_] = {
      query.exec(db)
    }

    def result[@miniboxed T](query: Rep[T]): T = {
      query.exec(db)
      //db.run(query.result).await
    }

    /**
     * action 을 순서대로 처리합니다.
     * {{{
     *   Seq(action1, action2, action3).seq
     *   // or
     *   action1 >> action2 >> action3
     *   // or
     *   action1 andThen action2 andThen action3
     * }}}
     */
    def seq[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      DBIO.seq(actions: _*).exec(db)
    }

    def withPinnedSession[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      DBIO.seq(actions: _*).withPinnedSession.exec(db)
    }

    def withTransaction[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      DBIO.seq(actions: _*).transactionally.exec(db)
    }

    def withTransaction[@miniboxed R](block: Session => R) = {
      using(db.createSession()) { s =>
        s.withTransaction { block(s) }
      }
    }

    /**
     * `Seq[ DBIO[R] ]` 를 `DBIO[ Seq[R] ]` 로 변환하여 db에서 실행한 후 Seq[R] 을 반환합니다.
     * {{{
     *   Seq(
     *    schema.create,
     *    q1.result,
     *    q2.result
     *   ).seqeunce
     *
     *   // returns Seq(Unit, q1.result, q2.result)
     * }}}
     */
    def sequence[@miniboxed R, E <: Effect](in: DBIOAction[R, NoStream, E]*)
                                (implicit cbf: CanBuildFrom[Seq[DBIOAction[R, NoStream, E]], R, Seq[R]]): Seq[R] = {
      DBIO.sequence(in).exec(db)
      // db.run(DBIO.sequence(in)).await
    }

    /**
     * action 들을 병렬로 수행합니다.
     * {{{
     *   Seq(action1, action2, action3).execPar
     * }}}
     */
    // TODO: 이 함수는 꼭 SlickComponent 에도 제공하자!!! 특히 autoCommitParallel 또는 asParallel 로
    def execPar[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      val results: Seq[Future[Any]] = actions.par.map { action =>
        db.run(action)
      }.seq
      results.awaitAll
    }
  }

  implicit class DBIOActionExtensions[R](action: DBIOAction[R, NoStream, _]) {
    /**
     * {{{
     *   val query = users.map(u=>(u.id, u.name))
     *   val userList = query.exec
     * }}}
     * @return
     */
    def exec(implicit db: SlickContext.driver.backend.DatabaseDef = defaultDB): R = {
      db.run(action).await
    }
  }

  implicit class DBIOActionSeqExtensions[R](actions: Seq[DBIOAction[R, NoStream, _]]) {
    /**
     * {{{
     *   val query = users.map(u=>(u.id, u.name))
     *   val userList = query.exec
     * }}}
     * @return
     */
    def exec(implicit db: SlickContext.driver.backend.DatabaseDef = defaultDB) = {
      db.run(DBIO.seq(actions: _*)).stay
    }
  }

  implicit class DBIOStreamActionExtensions[T](action: DBIOAction[_, Streaming[T], _]) {

    def stream(implicit db: SlickContext.driver.backend.DatabaseDef = defaultDB) = {
      db.stream(action)
    }
  }

  implicit class RepExtensions[T](r: Rep[T]) {
    /**
     * {{{
     *   val count = users.length.exec
     * }}}
     * @return
     */
    def exec(implicit db: SlickContext.driver.backend.DatabaseDef = defaultDB): T = {
      db.run(r.result).await
    }
  }
}
