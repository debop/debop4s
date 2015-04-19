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
 * debop4s.data.orm.jtests.associations.onetoone.Cavalier
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 4. 1
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Cavalier extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String name;

    private Float weight = 0f;

    @OneToOne
    @JoinColumn(name = "horseId")
    private Horse horse;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("horse", horse);
    }

    private static final long serialVersionUID = 3280052344054691650L;
}
