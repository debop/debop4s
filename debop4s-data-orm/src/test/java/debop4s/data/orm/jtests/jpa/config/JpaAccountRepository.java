package debop4s.data.orm.jtests.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface JpaAccountRepository extends JpaRepository<JpaAccount, Long> {

    @Query(value = "select a from JpaAccount a where a.name = :name")
    JpaAccount findByName(@Param("name") String name);
}
