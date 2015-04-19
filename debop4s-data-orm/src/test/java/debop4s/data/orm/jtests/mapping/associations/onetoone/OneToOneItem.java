package debop4s.data.orm.jtests.mapping.associations.onetoone;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneItem
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:59
 */
@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToOneItem extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ItemId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "ItemName")
    private String name;

    @Column(name = "ItemDesc")
    private String description;

    @Column(name = "ItemInitialPrice")
    private BigDecimal initialPrice;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = 2623628032064684726L;
}
