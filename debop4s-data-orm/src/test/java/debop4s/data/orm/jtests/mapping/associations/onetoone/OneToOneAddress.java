package debop4s.data.orm.jtests.mapping.associations.onetoone;

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
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneAddress
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:58
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToOneAddress extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "AddressId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @OneToOne(mappedBy = "shippingAddress")
    private OneToOneUser user;

    private String street;
    private String city;
    private String zipcode;

    @Override
    public int hashCode() {
        return Hashs.compute(street, zipcode, city);
    }

    private static final long serialVersionUID = -6712810759977926621L;
}
