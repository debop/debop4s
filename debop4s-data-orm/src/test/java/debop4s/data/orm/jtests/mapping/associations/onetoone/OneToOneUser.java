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

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneUser
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 10:00
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToOneUser extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String email;
    private Integer ranking;
    private Boolean admin;

    @OneToOne
    @JoinColumn(name = "SHIPPING_ADDRESS_ID")
    private OneToOneAddress shippingAddress = new OneToOneAddress();

    @Override
    public int hashCode() {
        return Hashs.compute(username, email);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("username", username)
                    .add("password", password);
    }

    private static final long serialVersionUID = -4454676558217950443L;
}
