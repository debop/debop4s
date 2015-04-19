package debop4s.data.orm.jtests.mapping.associations.onetomany.list;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToManyChild extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;

    private Date birthday;

    public OneToManyChild() {
    }

    public OneToManyChild(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("birthday", birthday);
    }

    private static final long serialVersionUID = -1808487944984128315L;
}
