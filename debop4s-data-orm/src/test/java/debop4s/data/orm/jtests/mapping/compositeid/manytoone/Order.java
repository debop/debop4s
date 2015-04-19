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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.Order
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_Order")
@org.hibernate.annotations.Cache(region = "data.composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Order extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "orderId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String number;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @OneToMany(mappedBy = "id.order", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

    @Override
    public int hashCode() {
        return Hashs.compute(number);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("number", number)
                    .add("orderDate", orderDate);
    }

    private static final long serialVersionUID = -478214079111379653L;
}
