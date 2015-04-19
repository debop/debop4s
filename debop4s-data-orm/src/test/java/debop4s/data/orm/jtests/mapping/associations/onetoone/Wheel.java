package debop4s.data.orm.jtests.mapping.associations.onetoone;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * debop4s.data.orm.jtests.associations.onetoone.Wheel
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
public class Wheel extends HibernateEntityBase<Integer> {

    @Id
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private String name;

    private double diameter;

    @OneToOne(cascade = CascadeType.PERSIST)
    @PrimaryKeyJoinColumn
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @MapsId
    private Vehicle vehicle;

    @Override
    public int hashCode() {
        return Hashs.compute(name, diameter);
    }

    private static final long serialVersionUID = 6778314207510296426L;
}
