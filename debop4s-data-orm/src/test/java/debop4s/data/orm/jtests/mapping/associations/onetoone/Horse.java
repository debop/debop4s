package debop4s.data.orm.jtests.mapping.associations.onetoone;

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

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.Horse
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 7. 오후 11:22
 */
@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Horse extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String name;

    private Float weight = 0f;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = 8864127089966537825L;
}
