package debop4s.data.orm.jtests.mapping.associations.join;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.util.Date;

@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "JoinCustomer")
@SecondaryTable(name = "JoinCustomerAddress", pkJoinColumns = { @PrimaryKeyJoinColumn(name = "CustomerId") })
@Getter
@Setter
public class JoinCustomer extends HibernateEntityBase<Long> {


    @Id
    @GeneratedValue
    @Column(name = "CustomerId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;
    private String email;

    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "street",
                            column = @Column(name = "Street", table = "JoinCustomerAddress")),
                    @AttributeOverride(name = "zipcode",
                            column = @Column(name = "ZipCode", table = "JoinCustomerAddress")),
                    @AttributeOverride(name = "city",
                            column = @Column(name = "City", table = "JoinCustomerAddress")),
            }
    )
    private JoinAddress joinAddress = new JoinAddress();

    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.INSERT)
    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "UpdatedAt", insertable = false, updatable = false)
    private Date updatedAt;

    @Override
    public int hashCode() {
        return Hashs.compute(name, email);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("email", email)
                    .add("createdAt", createdAt)
                    .add("updatedAt", updatedAt);
    }

    private static final long serialVersionUID = -6216466903476899629L;
}
