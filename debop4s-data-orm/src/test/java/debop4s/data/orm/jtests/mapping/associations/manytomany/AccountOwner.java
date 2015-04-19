package debop4s.data.orm.jtests.mapping.associations.manytomany;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class AccountOwner extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "OwnerId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "SSN", length = 32)
    private String SSN;

    // many-to-many 에서는 둘 중 하나는 mappedBy 를 지정해야 한다.
    //
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BankAccountOwners",
            joinColumns = { @JoinColumn(name = "OwnerId") },
            inverseJoinColumns = { @JoinColumn(name = "AccountId") })
    private Set<BankAccount> bankAccounts = new HashSet<BankAccount>();

    @Override
    public int hashCode() {
        return Hashs.compute(SSN);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("SSN", SSN);
    }

    private static final long serialVersionUID = 5968947797193508080L;
}
