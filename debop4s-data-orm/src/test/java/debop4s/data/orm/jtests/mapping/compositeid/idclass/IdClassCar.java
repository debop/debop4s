package debop4s.data.orm.jtests.mapping.compositeid.idclass;

import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

/**
 * debop4s.data.orm.s.mapping.compositeid.idclass.IdClassCar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 15. 오전 10:55
 */
@Entity
@IdClass(CarIdentifier.class)
@org.hibernate.annotations.Cache(region = "data.composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class IdClassCar implements Serializable {

    @Id
    private String brand;

    @Id
    private int year;

    private String serialNo;

    @Override
    public int hashCode() {
        return (brand != null) ? Hashs.compute(brand, year) : Hashs.compute(serialNo);
    }

    private static final long serialVersionUID = -6383440806969309800L;
}
