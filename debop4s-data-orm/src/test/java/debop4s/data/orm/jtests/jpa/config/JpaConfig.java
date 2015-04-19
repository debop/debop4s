package debop4s.data.orm.jtests.jpa.config;

import debop4s.data.orm.jpa.spring.AbstractJpaH2Configuration;
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackageClasses = { JpaAccountRepository.class })
@EnableTransactionManagement
public class JpaConfig extends AbstractJpaH2Configuration {

    @Override
    public String[] getMappedPackageNames() {
        return new String[] {
                JpaAccount.class.getPackage().getName()
        };
    }

    @Override
    public Properties jpaProperties() {

        Properties props = super.jpaProperties();

        props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "");
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf");

        return props;
    }
}
