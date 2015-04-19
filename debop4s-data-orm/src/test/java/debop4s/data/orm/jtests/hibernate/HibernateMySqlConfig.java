package debop4s.data.orm.jtests.hibernate;

import debop4s.data.orm.hibernate.spring.AbstractHibernateMySqlConfiguration;
import debop4s.data.orm.jtests.mapping.Employee;
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateMySqlConfig extends AbstractHibernateMySqlConfiguration {

    @Override
    public Properties hibernateProperties() {

        Properties props = super.hibernateProperties();

        props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

        props.put(Environment.USE_SECOND_LEVEL_CACHE, false);
        props.put(Environment.USE_QUERY_CACHE, false);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf");

        return props;
    }

    @Override
    public String[] getMappedPackageNames() {
        return new String[] {
                Employee.class.getPackage().getName()
        };
    }

}
