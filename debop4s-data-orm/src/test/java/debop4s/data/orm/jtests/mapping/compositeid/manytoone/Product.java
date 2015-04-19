package debop4s.data.orm.jtests.mapping.compositeid.manytoone;

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
import java.util.HashSet;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.Product
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_Product")
@org.hibernate.annotations.Cache(region = "data.composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Product extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "productId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "productName")
    private String name;

    @OneToMany(mappedBy = "id.product", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = -4725360631652953447L;
}
