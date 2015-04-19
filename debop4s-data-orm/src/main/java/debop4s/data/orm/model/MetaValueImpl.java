package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;

/**
 * 메타 정보를 표현하는 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:07
 */
@Getter
@Setter
public class MetaValueImpl extends ValueObjectBase implements MetaValue {

    private String value;
    private String label;
    private String description;
    private String exAttr;

    public MetaValueImpl() {
    }

    public MetaValueImpl(Object value) {
        this.value = (value == null) ? "" : value.toString();
    }

    public MetaValueImpl(MetaValue metaValue) {
        if (metaValue != null) {
            this.value = metaValue.getValue();
            this.label = metaValue.getLabel();
            this.description = metaValue.getDescription();
            this.exAttr = metaValue.getExAttr();
        }
    }

    @Override
    public int hashCode() {
        return Hashs.compute(value);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("value", value)
                    .add("label", label);
    }

    private static final long serialVersionUID = -8813830901621804567L;
}
