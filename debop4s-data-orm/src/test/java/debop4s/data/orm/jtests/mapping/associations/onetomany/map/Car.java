package debop4s.data.orm.jtests.mapping.associations.onetomany.map;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Map association 시에 대상을 class 로 할 수도 있고, 단순 수형으로 할 수도 있다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 9:16
 */
@Entity(name = "OneToMany_Car")
@Getter
@Setter
public class Car extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;

    @CollectionTable(name = "OneToMany_Car_Option_Map", joinColumns = { @JoinColumn(name = "carId") })
    @MapKeyClass(String.class) // Map의 Key 의 수형
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)  // Map의 Value의 수형
    private Map<String, String> options = new HashMap<String, String>();


    @CollectionTable(name = "OneToMany_Car_Option_Table", joinColumns = { @JoinColumn(name = "carId") })
    @MapKeyClass(String.class) // Map의 Key 의 수형
    @ElementCollection(targetClass = CarOption.class, fetch = FetchType.EAGER)  // Map의 Value의 수형
    private Map<String, CarOption> carOptions = new HashMap<String, CarOption>();

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = -2577151600025677445L;
}
