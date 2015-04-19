package debop4s.data.orm.jtests.mapping.inheritance.joinedsubclass;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 부모 클래스용 Table, 자식 클래스용 TABLE 모두 있다. 즉 class 상속 구조와 table 구조가 1:1 매핑이 되는 구조다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 11:09
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public abstract class JoinedSubclassPerson extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "PersonId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "PersonName", nullable = false, length = 128)
    private String name;

    @Column(name = "RegidentNo", nullable = false, length = 128)
    private String regidentNo;

    @Override
    public int hashCode() {
        return Hashs.compute(name, regidentNo);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("regidentNo", regidentNo);
    }

    private static final long serialVersionUID = -1943764307365649694L;
}
