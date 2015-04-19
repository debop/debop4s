package debop4s.data.orm.jtests.jpa.mysql;

import debop4s.data.orm.ReadOnlyConnection;
import debop4s.data.orm.jtests.mapping.simple.SimpleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * SimpleEntityRepository
 *
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@Repository
@Transactional
public interface SimpleEntityRepository
        extends JpaRepository<SimpleEntity, Long>, QueryDslPredicateExecutor<SimpleEntity> {

    @ReadOnlyConnection
    @Query("select se from SimpleEntity se where se.id = :id")
    SimpleEntity findById(@Param("id") Long id);
}
