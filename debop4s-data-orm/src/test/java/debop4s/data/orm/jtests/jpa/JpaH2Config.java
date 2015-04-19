package debop4s.data.orm.jtests.jpa;

import debop4s.data.orm.jpa.spring.AbstractJpaH2Configuration;
import debop4s.data.orm.jtests.jpa.config.JpaAccount;
import debop4s.data.orm.jtests.mapping.Employee;
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * JPA for H2
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 1:06
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = { JpaTestBase.class, Employee.class })
@EnableTransactionManagement
public class JpaH2Config extends AbstractJpaH2Configuration {

    @Override
    public String[] getMappedPackageNames() {
        return new String[] {
                Employee.class.getPackage().getName(),
                JpaAccount.class.getPackage().getName()
        };
    }

    @Override
    public Properties jpaProperties() {

        Properties props = super.jpaProperties();

        props.put(Environment.HBM2DDL_AUTO, "create-drop"); // create | spawn | spawn-drop | update | validate | none

        // 2nd cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "");
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf");

        return props;
    }
}
