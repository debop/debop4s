package debop4s.data.orm.jtests.mapping.inheritance.unionsubclass;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 상속관계의 엔테티들을 독립적인 테이블로 만든다.
 * 주의할 점은 Identifier 는 상속된 모든 class에 대해 고유한 값을 가져야 한다. (테이블 범위의 identity는 사용하면 안된다)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:55
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
// @SequenceGenerator(name = "UnionSubclassBilling_Seq", sequenceName = "UnionSubclassBilling_Seq")
public abstract class UnionSubclassBillingBase extends HibernateEntityBase<String> {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Setter(AccessLevel.PROTECTED)
    private String id;

    @Column(name = "Owner", nullable = false)
    private String owner;

    @Override
    public int hashCode() {
        return Hashs.compute(owner);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("owner", owner);
    }

    private static final long serialVersionUID = -3188325006333534528L;
}
