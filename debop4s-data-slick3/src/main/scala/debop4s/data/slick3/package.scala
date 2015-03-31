package debop4s.data

import debop4s.core.concurrent._
import debop4s.data.slick3.SlickContext.driver.api._
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import slick.lifted.QueryBase

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal


/**
 * package
 * @author sunghyouk.bae@gmail.com
 */
package object slick3 {

  implicit class DatabaseExtensions(db: SlickContext.driver.backend.DatabaseDef) {

    /** 동기 방식으로 action 을 수행합니다. */
    def exec[R](action: DBIOAction[R, NoStream, Nothing]): R = {
      db.run(action).await
    }

    def result(query: Query[_, _, Seq]) = {
      db.run(query.result).await
    }

    /**
     * action 을 순서대로 처리합니다.
     * {{{
     *   db.seq(action1, action2, action3)
     *   // or
     *   action1 >> action2 >> action3
     *   // or
     *   action1 andThen action2 andThen action3
     * }}}
     */
    def seq[E <: Effect](actions: DBIOAction[_, NoStream, E]*): Unit = {
      db.run(DBIO.seq[E](actions: _*)).await
    }

    /**
     * `Seq[ DBIO[R] ]` 를 `DBIO[ Seq[R] ]` 로 변환하여 db에서 실행한 후 Seq[R] 을 반환합니다.
     * {{{
     *   db.sequence(
     *    schema.create,
     *    q1.result,
     *    q2.result
     *   )
     *
     *   // returns Seq(Unit, q1.result, q2.result)
     * }}}
     */
    def sequence[R, E <: Effect](in: DBIOAction[R, NoStream, E]*)
                                (implicit cbf: CanBuildFrom[Seq[DBIOAction[R, NoStream, E]], R, Seq[R]]): Seq[R] = {
      db.run(DBIO.sequence(in)).await
    }

    /**
     * action 들을 병렬로 수행합니다.
     * {{{
     *   db.par(action1, action2, action3)
     * }}}
     */
    def execPar[E <: Effect](actions: DBIOAction[_, NoStream, E]*) = {
      val results = actions.par.map { action =>
        db.run(action)
      }.seq
      results.awaitAll
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

    def foreach(f: T => Any): Future[Unit] = {
      val pr = Promise[Unit]()

      try p.subscribe(new Subscriber[T] {
        override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)
        override def onComplete(): Unit = pr.success(())
        override def onError(throwable: Throwable): Unit = pr.failure(throwable)
        override def onNext(t: T): Unit = f(t)
      }) catch { case NonFatal(e) => pr.failure(e) }

      pr.future
    }
  }


}
