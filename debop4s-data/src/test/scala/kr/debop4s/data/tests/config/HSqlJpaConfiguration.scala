package kr.debop4s.data.tests.config

import kr.debop4s.data.jpa.config.AbstractHSqlJpaConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * kr.debop4s.data.tests.config.JpaHSqlConfiguration
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 11. 오후 10:50
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
class HSqlJpaConfiguration extends AbstractHSqlJpaConfiguration {

    def getMappedPackageNames: Array[String] =
        Array("kr.debop4s.data")
}
