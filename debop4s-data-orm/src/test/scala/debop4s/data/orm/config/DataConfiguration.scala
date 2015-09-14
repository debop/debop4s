package debop4s.data.orm.config

import com.typesafe.config.ConfigFactory
import debop4s.data.orm.config.servers.JpaH2Configuration
import org.springframework.context.annotation._
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableAsync
@Import(Array(classOf[JpaH2Configuration]))
class DataConfiguration {

  def environment: String = {
    System.getProperty("profile", "local").toLowerCase
  }

  @Bean
  def dataConfig: DataConfig = {
    val config = ConfigFactory.load(s"config/$environment")
    DataConfig(config.getConfig("debop4s"))
  }
}
