package debop4s.data.orm.jtests.mapping.associations.onetoone;

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
import java.util.Date;

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneShipment
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 10:00
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Table(name = "OneToOneShipment")
@SecondaryTable(name = "OneToOneShipmentItem")
@Getter
@Setter
public class OneToOneShipment extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ShipmentId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "ShipmentState")
    private String state;

    @Column(name = "ShipmentCreateOn")
    private Date createOn;

    @ManyToOne
    @JoinColumn(table = "OneToOneShipmentItem", name = "ItemId")
    private OneToOneItem auction = new OneToOneItem();

    @Override
    public int hashCode() {
        return Hashs.compute(state, createOn);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("state", state)
                    .add("createOn", createOn);

    }

    private static final long serialVersionUID = -6655877765265671807L;
}
