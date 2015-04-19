package debop4s.data.orm.jtests.mapping.compositeid.manytoone;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.OrderDetailIdentifier
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:06
 */
@Embeddable
@Getter
public class OrderDetailIdentifier extends ValueObjectBase {

    protected OrderDetailIdentifier() {
    }

    public OrderDetailIdentifier(Order order, Product product) {
        assert (order != null);
        assert (product != null);

        this.order = order;
        this.product = product;
    }

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @Override
    public int hashCode() {
        return Hashs.compute(order, product);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("orderId", order.getId())
                    .add("productId", product.getId());
    }

    private static final long serialVersionUID = -7914201856753998776L;
}
