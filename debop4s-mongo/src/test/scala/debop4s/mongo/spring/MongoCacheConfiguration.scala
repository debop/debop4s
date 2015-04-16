package debop4s.mongo.spring

import debop4s.mongo.config.MongoConfigBase
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}

@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = Array(classOf[UserRepository]))
class MongoCacheConfiguration extends MongoConfigBase {

  override def getDatabaseName: String = "debop4s-mongo"

  @Bean
  def mongoCacheManager: MongoCacheManager =
    new MongoCacheManager(mongoTemplate(), 60)
}
