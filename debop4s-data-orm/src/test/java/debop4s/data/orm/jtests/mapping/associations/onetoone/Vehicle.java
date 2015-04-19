package debop4s.data.orm.jtests.mapping.associations.onetoone;

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
 * debop4s.data.orm.jtests.associations.onetoone.Vehicle
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 4. 1
 */
@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Vehicle extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String brand;

    @Override
    public int hashCode() {
        return Hashs.compute(brand);
    }

    private static final long serialVersionUID = -271107474046505945L;
}
