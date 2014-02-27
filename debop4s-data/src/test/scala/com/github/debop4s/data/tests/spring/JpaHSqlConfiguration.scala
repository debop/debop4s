package com.github.debop4s.data.tests.spring

import com.github.debop4s.data.jpa.spring.AbstractJpaHSqlConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * com.github.debop4s.data.tests.spring.HSqlJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 10:30
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
class JpaHSqlConfiguration extends AbstractJpaHSqlConfiguration {

    override def getMappedPackageNames: Array[String] =
        Array("com.github.debop4s.data")
}
