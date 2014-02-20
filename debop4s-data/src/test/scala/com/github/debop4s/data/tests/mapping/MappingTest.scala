package com.github.debop4s.data.tests.mapping

import com.github.debop4s.data.tests.AbstractDataTest
import com.github.debop4s.data.tests.config.JpaHSqlConfiguration
import javax.persistence.{EntityManager, PersistenceContext}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scala.jdbc.core.JdbcTemplate
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * com.github.debop4s.data.tests.mapping.MappingTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 11. 오후 10:54
 */
@ContextConfiguration(classes = Array(classOf[JpaHSqlConfiguration]),
                         loader = classOf[AnnotationConfigContextLoader])
class MappingTest extends AbstractDataTest {

    @PersistenceContext val em: EntityManager = null
    @Autowired val jdbcTemplate: JdbcTemplate = null

    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    test("configuration setup test") {
        assert(emf != null)
        assert(em != null)
        assert(jdbcTemplate != null)
    }
}
