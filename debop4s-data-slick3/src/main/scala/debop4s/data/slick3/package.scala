package debop4s.data

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.data.slick3.SlickContext._
import debop4s.data.slick3.SlickContext.driver.api._
import org.reactivestreams.{Publisher, Subscriber, Subscription}

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

  //  implicit def commit[T](dbAction:DBIO[T])(implicit timeout:Duration = 5 minute): T = {
  //    dbAction.commit(timeout)
  //  }

  implicit class DBIOExtensions[T](dbAction: DBIO[T]) {

    def query(implicit timeout: Duration = 30 seconds): T = {
      runAction(dbAction)
    }

    def commit(implicit timeout: Duration = 5 minutes): T = {
      runAction(dbAction.transactionally)
    }

    def rollback(implicit ec: ExecutionContext, timeout: Duration = 5 minutes): T = {
      val db = SlickContext.defaultDB
      val session = db.createSession()
      session.force()
      try {
        db.run(dbAction).await(timeout)
      } finally {
        db.withSession { session => session.conn.rollback() }
        db.close()
      }
    }

    def runOnDb(implicit ec: ExecutionContext, timeout: Duration = 5 minutes): T = {
      rollback(ec, timeout)
    }

    private def runAction(dbAction: DBIO[T])(implicit timeout: Duration): T = {
      val db = SlickContext.defaultDB

      // keey the database in memory with an extra connection
      db.createSession().force()
      try {
        db.run(dbAction).await(timeout)
      } finally {
        db.close()
      }
    }
  }

  implicit class DatabaseExtensions(db: SlickContext.driver.backend.DatabaseDef) {

    /** 동기 방식으로 action 을 수행합니다. */
    def exec[R](action: DBIOAction[R, NoStream, Nothing]): R = {
      action.exec(db)
      // db.run(action).await
    }

    def result[E, U, Seq[_]](query: Query[E, U, Seq]): Seq[_] = {
      query.exec(db)
      // db.run(query.result).await
    }

    //    def result[E, U, Set[_]](query: Query[E, U, Set]) = {
    //      db.run(query.result).await
    //    }

    def result[T](query: Rep[T]): T = {
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
      // db.run(DBIO.seq[E](actions: _*)).await
    }

    def withPinnedSession[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      DBIO.seq(actions: _*).withPinnedSession.exec(db)
      // db.run(DBIO.seq[E](actions: _*).withPinnedSession).await
    }

    def withTransaction[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      DBIO.seq(actions: _*).transactionally.exec(db)
      // db.run(DBIO.seq[E](actions: _*).transactionally).await
    }

    def withTransaction[R](block: Session => R) = {
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
    def sequence[R, E <: Effect](in: DBIOAction[R, NoStream, E]*)
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
      db.run(DBIO.seq(actions: _*)).await
    }
  }

  implicit class DBIOStreamActionExtensions[T](action: DBIOAction[_, Streaming[T], Nothing]) {

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

  implicit class PublisherExtensions[T](p: Publisher[T]) {

    /**
     * 동기 방식으로 reactive stream 을 읽어드여 Vector 로 빌드합니다.
     */
    def materialize: Future[Vector[T]] = {
      val builder = Vector.newBuilder[T]
      val pr = Promise[Vector[T]]()
      try p.subscribe(new Subscriber[T] {
        override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
        override def onComplete(): Unit = pr.success(builder.result())
        override def onError(throwable: Throwable): Unit = pr.failure(throwable)
        override def onNext(t: T): Unit = builder += t
      }) catch { case NonFatal(e) => pr.failure(e) }

      pr.future
    }

    /** Asynchronously consume a Reactive Stream and materialize it as a Vector, requesting new
      * elements one by one and transforming them after the specified delay. This ensures that the
      * transformation does not run in the synchronous database context but still preserves
      * proper sequencing. */
    def materializeAsync[R](tr: T => Future[R],
                            delay: Duration = Duration(100L, TimeUnit.MILLISECONDS)): Future[Vector[R]] = {
      val exe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]())
      val ec = ExecutionContext.fromExecutor(exe)
      val builder = Vector.newBuilder[R]
      val pr = Promise[Vector[R]]()
      var sub: Subscription = null

      def async[T](thunk: => T): Future[T] = {
        val f = Future {
          Thread.sleep(delay.toMillis)
          thunk
        }(ec)
        f.onFailure { case t =>
          pr.tryFailure(t)
          sub.cancel()
        }(ec)
        f
      }
      try
        p.subscribe(new Subscriber[T] {
          def onSubscribe(s: Subscription): Unit = async {
            sub = s
            sub.request(1L)
          }
          def onComplete(): Unit = async(pr.trySuccess(builder.result()))
          def onError(t: Throwable): Unit = async(pr.tryFailure(t))
          def onNext(t: T): Unit = async {
            tr(t).onComplete {
              case Success(r) =>
                builder += r
                sub.request(1L)
              case Failure(t) =>
                pr.tryFailure(t)
                sub.cancel()
            }(ec)
          }
        }) catch {
        case NonFatal(ex) => pr.tryFailure(ex)
      }
      val f = pr.future
      f.onComplete(_ => exe.shutdown())(ec)
      f
    }

    /**
     * reactive stream 을 읽어 각 row 를 처리합니다.
     * @param f 각 row를 처리할 함수
     * @return Future[Unit]
     */
    def foreach(f: T => Any): Future[Unit] = {
      val pr = Promise[Unit]()

      try {
        p.subscribe(new Subscriber[T] {
          override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
          override def onComplete(): Unit = pr.success(())
          override def onError(throwable: Throwable): Unit = pr.failure(throwable)
          override def onNext(t: T): Unit = f(t)
        })
      } catch {
        case NonFatal(e) => pr.failure(e)
      }

      pr.future
    }
  }


}
