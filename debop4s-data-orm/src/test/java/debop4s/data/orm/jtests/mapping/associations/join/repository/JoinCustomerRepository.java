package debop4s.data.orm.jtests.mapping.associations.join.repository;

import debop4s.data.orm.jtests.mapping.associations.join.JoinCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link JoinCustomer} 용 Repository 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 1:42
 */
@Repository
public interface JoinCustomerRepository
        extends JpaRepository<JoinCustomer, Long>, QueryDslPredicateExecutor<JoinCustomer> {

    JoinCustomer findByName(String name);

    List<JoinCustomer> findByNameLike(String name);

    JoinCustomer findByEmail(String email);
}
