package debop4s.data.orm.jtests.hibernate.config;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@org.hibernate.annotations.Cache(region = "data.jpa", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Account extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private double cashBalance;

    @Column(name = "AccountName", nullable = false, length = 32)
    private String name;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("id", id)
                    .add("name", name);
    }

    private static final long serialVersionUID = 6114713887472244072L;
}
