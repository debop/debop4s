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
import javax.persistence.Id;

/**
 * debop4s.data.orm.s.mapping.queries.Hypothesis
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 8. 오전 12:06
 */
@Entity
// NOTE: Assigned Id 를 가지는 Entity는 캐시하지 마세요!!!
// @org.hibernate.annotations.Cache(region = "data.queries", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Hypothesis extends HibernateEntityBase<String> {

    protected Hypothesis() {}

    public Hypothesis(final String id) {
        this.id = id;
    }

    @Id
    @Setter(AccessLevel.PROTECTED)
    private String id;

    private String description;
    private Integer position;

    @Override
    public int hashCode() {
        return Hashs.compute(description, position);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("description", description)
                    .add("position", position);
    }

    private static final long serialVersionUID = -6196513300237212575L;
}
