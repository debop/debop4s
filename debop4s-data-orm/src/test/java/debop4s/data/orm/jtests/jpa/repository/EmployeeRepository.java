package debop4s.data.orm.jtests.jpa.repository;

import debop4s.data.orm.jtests.mapping.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link Employee} 를 위한 Repository 입니다.
 * Scala Trait 으로는 debop4s.data.orm jpa 의 기능을 사용할 수 없습니다. 꼭 Java 로 사용하세요.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 21. 오후 2:58
 */
@Transactional
public interface EmployeeRepository extends JpaRepository<Employee, Long>, QueryDslPredicateExecutor<Employee> {

    @Query("select e from Employee e where e.empNo=:empNo")
    Employee findByEmpNo(@Param("empNo") String empNo);

    @Query("select e from Employee e where e.empNo=:empNo and e.email=:email")
    Employee findByEmpNoAndEmail(@Param("empNo") String empNo,
                                 @Param("email") String email);

}
