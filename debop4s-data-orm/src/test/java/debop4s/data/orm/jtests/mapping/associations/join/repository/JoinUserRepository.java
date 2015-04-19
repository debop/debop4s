package debop4s.data.orm.jtests.mapping.associations.join.repository;

import debop4s.data.orm.jtests.mapping.associations.join.JoinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinUserRepository
        extends JpaRepository<JoinUser, Long>, QueryDslPredicateExecutor<JoinUser> {
}
