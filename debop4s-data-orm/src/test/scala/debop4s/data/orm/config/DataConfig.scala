package debop4s.data.orm.config

import com.typesafe.config.Config
import debop4s.config.server.{DatabaseSupport, HibernateSupport}


case class DataConfig(override val config: Config)
  extends DatabaseSupport
  with HibernateSupport