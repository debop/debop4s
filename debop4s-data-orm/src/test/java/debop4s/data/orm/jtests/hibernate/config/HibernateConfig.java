package debop4s.data.orm.jtests.hibernate.config;

import debop4s.data.orm.hibernate.spring.AbstractHibernateHSqlConfiguration;
import org.hibernate.cache.rediscala.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig extends AbstractHibernateHSqlConfiguration {

    @Override
    public String[] getMappedPackageNames() {
        return new String[] { Account.class.getPackage().getName() };
    }

    @Override
    public Properties hibernateProperties() {
        Properties props = super.hibernateProperties();
        props.put(Environment.HBM2DDL_AUTO, "create");

        // 2nd cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.conf");

        return props;
    }
}
