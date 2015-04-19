package debop4s.data.orm.jtests.mapping.associations.manytoone;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;


@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Proxy
@Getter
@Setter
public class SalesGuy extends HibernateEntityBase<Integer> {

    private static final long serialVersionUID = -6669906572407171181L;

    protected SalesGuy() {
    }

    public SalesGuy(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    private SalesForce salesForce;

    @Override
    public int hashCode() {
        return Hashs.compute(name, salesForce);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }
}
