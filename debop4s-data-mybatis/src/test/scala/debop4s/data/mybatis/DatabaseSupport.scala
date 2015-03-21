package debop4s.data.mybatis

import debop4s.data.mybatis.config.Configuration
import debop4s.data.mybatis.repository.UserRepository
import debop4s.data.mybatis.session.{ Session, SessionManager }

/**
 * 테스트 시에 사용할 데이터베이스에 대한 특성을 제공합니다.
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait DatabaseSupport {

  def withReadOnly(db: SessionManager)(block: Session => Unit): Unit = {
    db.readOnly { implicit session =>
      DatabaseSchema.prepare
      block(session)
    }
  }

  /**
  * Provides [[SessionManager]] instances.
  */
  object Database {
    val config = Configuration("mybatis.xml")

    config.addSpace("test") { space =>
      space ++= DatabaseSchema
      space ++= UserRepository
    }

    lazy val default = config.createPersistenceContext
  }

}
