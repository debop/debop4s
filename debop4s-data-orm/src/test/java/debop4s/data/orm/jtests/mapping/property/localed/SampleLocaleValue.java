package debop4s.data.orm.jtests.mapping.property.localed;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.LocaleValue;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * debop4s.data.orm.s.mapping.property.localed.SampleLocaleValue
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 10:00
 */
@Embeddable  // 이게 꼭 있어야 합니다.
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SampleLocaleValue extends ValueObjectBase implements LocaleValue {

    public SampleLocaleValue() {
    }

    public SampleLocaleValue(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Column(name = "SampleLocaleEntityTitle")
    private String title;

    @Column(name = "SampleLocaleEntityDesc")
    private String description;

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

    private static final long serialVersionUID = -4690278513548815090L;
}
