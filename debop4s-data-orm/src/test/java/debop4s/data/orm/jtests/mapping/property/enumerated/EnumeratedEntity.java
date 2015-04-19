package debop4s.data.orm.jtests.mapping.property.enumerated;

import debop4s.core.ToStringHelper;
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
 * EnumeratedEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 9:41
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.property", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class EnumeratedEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    // Enumerated 를 쓰면 Enum 값을 원하는 수형으로 DB에 저장하고, 반환받을 수 있다. 대부분 String 을 사용한다.
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "OrdinalValue")
    private OrdianlEnum intValue;

    // Enumerated 를 쓰면 Enum 값을 원하는 수형으로 DB에 저장하고, 반환받을 수 있다. 대부분 String 을 사용한다.
    @Enumerated(EnumType.STRING)
    @Column(name = "StringValue")
    private StringEnum stringValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "AgeType")
    private AgeType ageType = AgeType.A0;

    @Override
    public int hashCode() {
        return Hashs.compute(intValue, stringValue, ageType);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("intValue", intValue)
                    .add("stringValue", stringValue)
                    .add("ageType", ageType);
    }

    private static final long serialVersionUID = -7115390117836457805L;
}
