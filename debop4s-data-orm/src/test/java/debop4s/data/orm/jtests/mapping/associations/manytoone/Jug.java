package debop4s.data.orm.jtests.mapping.associations.manytoone;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Proxy
@Getter
@Setter
public class Jug extends HibernateEntityBase<Long> {

    public Jug() {
    }

    public Jug(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "jugName", length = 100)
    private String name;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = -4245920780803418949L;
}
