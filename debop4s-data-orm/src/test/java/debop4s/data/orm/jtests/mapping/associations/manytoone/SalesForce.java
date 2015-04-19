package debop4s.data.orm.jtests.mapping.associations.manytoone;

import com.google.common.collect.Sets;
import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Set;

@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Proxy
@Getter
@Setter
public class SalesForce extends HibernateEntityBase<Integer> {

    private static final long serialVersionUID = 3406957027655954657L;

    public SalesForce() {
    }

    public SalesForce(String corporation) {
        this.corporation = corporation;
    }

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String corporation;

    @OneToMany(mappedBy = "salesForce", cascade = { CascadeType.ALL })
    Set<SalesGuy> salesGuys = Sets.newHashSet();

    @Override
    public int hashCode() {
        return Hashs.compute(corporation);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("corporation", corporation);
    }
}
