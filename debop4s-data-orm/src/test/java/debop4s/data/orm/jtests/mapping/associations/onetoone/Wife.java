package debop4s.data.orm.jtests.mapping.associations.onetoone;

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
 * debop4s.data.orm.jtests.associations.onetoone.Wife
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
public class Wife extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Column(name = "wifeId")
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "wife")
    private Husband husband;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    private static final long serialVersionUID = 768379718952692603L;
}
