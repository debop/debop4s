package debop4s.data.orm.jtests.mapping.associations.unidirectional;

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
import java.util.HashSet;
import java.util.Set;

/**
 * debop4s.data.orm.s.mapping.associations.unidirectional.Cloud
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 7. 오후 9:07
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Access(AccessType.FIELD)
public class Cloud extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String kind;

    private Double length;

    @OneToMany
    @JoinTable
    private Set<SnowFlake> producedSnowFlakes = new HashSet<SnowFlake>();

    @Override
    public int hashCode() {
        return Hashs.compute(kind, length);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("kind", kind)
                    .add("length", length);
    }

    private static final long serialVersionUID = 407227970320543328L;
}
