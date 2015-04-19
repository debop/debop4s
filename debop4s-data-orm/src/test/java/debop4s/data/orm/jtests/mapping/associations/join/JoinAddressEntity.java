package debop4s.data.orm.jtests.mapping.associations.join;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @JoinTable 을 이용한 Mapping 은 Entity여야 합니다.
 * @since 13. 7. 3. 오후 2:24
 */
@Entity
@Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class JoinAddressEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String street;
    private String city;
    private String zipcode;

    @Override
    public int hashCode() {
        return Hashs.compute(street, city, zipcode);
    }

    private static final long serialVersionUID = -1251647778411637144L;
}
