package debop4s.redis.spring

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}

/**
 * debop4s.redis.spring.RedisCacheConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오전 10:59
 */
@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = Array(classOf[UserRepository]))
class RedisCacheConfiguration {

    @Bean
    def redisCacheManager(): RedisCacheManager =
        RedisCacheManager()

}
