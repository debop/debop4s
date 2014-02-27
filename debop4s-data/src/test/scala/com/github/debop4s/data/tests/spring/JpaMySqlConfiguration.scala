package com.github.debop4s.data.tests.spring

import com.github.debop4s.data.jpa.spring.AbstractJpaMySqlConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * JpaMySqlConfiguration
 * Created by debop on 2014. 2. 27.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
class JpaMySqlConfiguration extends AbstractJpaMySqlConfiguration {

    override def getMappedPackageNames: Array[String] =
        Array("com.github.debop4s.data")

}
