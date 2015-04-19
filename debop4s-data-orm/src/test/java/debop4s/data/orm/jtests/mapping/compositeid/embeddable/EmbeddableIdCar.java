package debop4s.data.orm.jtests.mapping.compositeid.embeddable;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * debop4s.data.orm.s.mapping.compositeid.embeddable.EmbeddableIdCar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 15. 오전 10:50
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class EmbeddableIdCar extends HibernateEntityBase<EmbeddableCarIdentifier> {

    protected EmbeddableIdCar() {
    }

    public EmbeddableIdCar(EmbeddableCarIdentifier id) {
        this.id = id;
    }

    @EmbeddedId
    @Column(name = "carId")
    private EmbeddableCarIdentifier id;

    private String serialNo;

    @Override
    public int hashCode() {
        return Hashs.compute(serialNo);
    }

    private static final long serialVersionUID = -3446480658474625554L;
}
