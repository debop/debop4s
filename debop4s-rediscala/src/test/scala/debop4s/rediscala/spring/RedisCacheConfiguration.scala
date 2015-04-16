package debop4s.rediscala.spring

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}


@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = Array(classOf[UserRepository]))
class RedisCacheConfiguration {

  @Bean
  def redisCacheManager(): RedisCacheManager =
    RedisCacheManager()

}
