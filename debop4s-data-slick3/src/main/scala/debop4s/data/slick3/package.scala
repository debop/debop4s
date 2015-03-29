package debop4s.data

import debop4s.core.concurrent._
import debop4s.data.slick3.SlickContext.driver.api._


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

    def seq[E <: Effect](actions: DBIOAction[_, NoStream, E]*): Unit = {
      db.run(DBIO.seq[E](actions: _*)).await
    }

  }

}
