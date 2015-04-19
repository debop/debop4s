package debop4s.data.orm.jtests.mapping.associations.onetomany.set;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Getter
@Setter
public class Bid extends HibernateEntityBase<Long> {

    public Bid() {
    }

    public Bid(BiddingItem item, BigDecimal amount) {
        this.item = item;
        this.item.getBids().add(this);
        this.amount = amount;
    }

    @Id
    @GeneratedValue
    @Column(name = "BidId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ItemId")
    private BiddingItem item;

    @Column(nullable = false)
    private BigDecimal amount;

    // Mapping 하지 않을 정보
    @Transient
    private Timestamp timestamp;

    @Override
    public int hashCode() {
        return Hashs.compute(amount);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("amount", amount)
                    .add("item", item);
    }

    private static final long serialVersionUID = -6632625757262620556L;
}
