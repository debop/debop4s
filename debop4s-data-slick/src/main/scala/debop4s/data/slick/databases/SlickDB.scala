package debop4s.data.slick.databases

import debop4s.config.server.DatabaseSetting
import debop4s.data.slick.utils.SlickUtils


object SlickDB {
  def apply(dbSetting: DatabaseSetting): SlickDB = new SlickDB(dbSetting)
}

/**
 * Slick 으로 사용하는 DB 를 나타냅니다.
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
class SlickDB(dbSetting: DatabaseSetting) extends ExternalJdbcDB(dbSetting) {

  override val driver: Driver =
    SlickUtils.drivers.getOrElse(dbSetting.driverClass, sys.error("No suitable driver was found"))

}
