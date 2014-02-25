package com.github.debop4s.data.tests.config

import com.github.debop4s.data.jpa.spring.AbstractHSqlJpaConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * com.github.debop4s.data.tests.spring.JpaHSqlConfiguration
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 11. 오후 10:50
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
class JpaHSqlConfiguration extends AbstractHSqlJpaConfiguration {

    def getMappedPackageNames: Array[String] =
        Array("com.github.debop4s.data")
}
