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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.OrderDetail
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_OrderDetail")
@org.hibernate.annotations.Cache(region = "data.composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OrderDetail extends HibernateEntityBase<OrderDetailIdentifier> {

    protected OrderDetail() {
    }

    public OrderDetail(Order order, Product product) {
        this.id = new OrderDetailIdentifier(order, product);
    }

    public OrderDetail(OrderDetailIdentifier id) {
        this.id = id;
    }

    @EmbeddedId
    @Setter(AccessLevel.PROTECTED)
    private OrderDetailIdentifier id;

    private BigDecimal unitPrice;
    private Integer quantity;
    private Float discount;


    @Override
    public int hashCode() {
        return Hashs.compute(unitPrice, quantity, discount);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("unitPrice", unitPrice)
                    .add("quantity", quantity)
                    .add("discount", discount);
    }

    private static final long serialVersionUID = 6958616166017033341L;
}
