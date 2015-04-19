package debop4s.data.orm.jtests.mapping.queries;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Helicopter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 8. 오전 12:04
 */
@Entity
// NOTE: Assigned Id 를 가지는 Entity는 캐시하지 마세요!!!
// @org.hibernate.annotations.Cache(region="orm.queries", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Helicopter extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

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

    private static final long serialVersionUID = -1934543818014285640L;
}
