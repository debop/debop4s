package debop4s.rediscala.jtests.spring;

import debop4s.rediscala.spring.RedisCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RedisCacheConfiguration
 *
 * @author Sunghyouk Bae
 */
@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = { UserRepository.class })
public class RedisCacheConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager() {
        return new RedisCacheManager();
    }
}
