package debop4s.data.orm.jtests.jpa.mysql;

import debop4s.data.orm.jpa.spring.AbstractJpaMySqlReplicationConfiguration;
import debop4s.data.orm.jtests.jpa.config.JpaAccount;
import debop4s.data.orm.jtests.mapping.Employee;
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * 실제 제품에서는 AbstractJpaMySqlConfiguration을 상속 받으세요.
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = { SimpleEntityRepository.class })
@ComponentScan(basePackageClasses = { SimpleEntityService.class })
public class JpaMySqlReplicationConfig extends AbstractJpaMySqlReplicationConfiguration {

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

        props.put(Environment.HBM2DDL_AUTO, "update"); // create | spawn | spawn-drop | update | validate | none

        // 2nd cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, false);
        props.put(Environment.USE_QUERY_CACHE, false);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf");

        return props;
    }

}
