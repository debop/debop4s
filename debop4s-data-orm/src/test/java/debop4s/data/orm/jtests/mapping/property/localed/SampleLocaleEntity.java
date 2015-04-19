package debop4s.data.orm.jtests.mapping.property.localed;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.LocaleEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 다국어 지원을 위해, Locale별 정보를 가지는 엔티티에 대한 예제입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 9:58
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.property", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SampleLocaleEntity extends LocaleEntityBase<Long, SampleLocaleValue> {


    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String title;
    private String description;

    @CollectionTable(name = "SampleLocaleEntityLocale", joinColumns = { @JoinColumn(name = "SampleLocaleEntityId") })
    @MapKeyClass(Locale.class)
    @ElementCollection(targetClass = SampleLocaleValue.class, fetch = FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    private Map<Locale, SampleLocaleValue> localeMap = new HashMap<Locale, SampleLocaleValue>();

    @Override
    public Map<Locale, SampleLocaleValue> getLocaleMap() {
        return localeMap;
    }

    /**
     * Java에서는 실행 시 Generic 수형을 없애버립니다.
     * scala나 c#은 generic으로 인스턴스를 생성할 수 있지만, Java는 불가능합니다.
     * 그래서 이 값을 꼭 구현해 주셔야 합니다.
     *
     * @return TLocalVal 인스턴스
     */
    @Override
    public SampleLocaleValue createDefaultLocaleValue() {
        return new SampleLocaleValue(title, description);
    }

    @Override
    public int hashCode() {
        return Hashs.compute(title);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("title", title)
                    .add("description", description);
    }

    private static final long serialVersionUID = 4459274867992779918L;
}
