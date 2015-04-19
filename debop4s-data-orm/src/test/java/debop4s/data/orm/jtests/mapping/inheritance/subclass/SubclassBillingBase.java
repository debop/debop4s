package debop4s.data.orm.jtests.mapping.inheritance.subclass;

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
 * 한 테이블에 Super-Sub class 들 모두 저장됩니다. (subclass)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:52
 */
@Entity
@Table(name = "SubclassBilling")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "BillingType", discriminatorType = DiscriminatorType.STRING)
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public abstract class SubclassBillingBase extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "BillingId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "Owner")
    private String owner;

    @Override
    public int hashCode() {
        return Hashs.compute(owner);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("ownwer", owner);
    }

    private static final long serialVersionUID = 1220620528805401962L;
}
