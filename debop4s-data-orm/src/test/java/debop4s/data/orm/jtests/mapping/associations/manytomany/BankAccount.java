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
public class BankAccount extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "AccountId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "AccountNumber", length = 32)
    private String accountNumber;

    // many-to-many 에서는 둘 중 하나는 mappedBy 를 지정해야 한다.
    @ManyToMany(mappedBy = "bankAccounts")
    private Set<AccountOwner> owners = new HashSet<AccountOwner>();

    @Override
    public int hashCode() {
        return Hashs.compute(accountNumber);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("accountNumber", accountNumber);
    }

    private static final long serialVersionUID = 4250383162061467190L;
}
