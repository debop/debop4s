package debop4s.data.orm;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;

/**
 * Query 등에서 사용할 Named Parameter의 기본 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:42
 */
@Getter
@Setter
abstract public class NamedParameterBase extends ValueObjectBase implements NamedParameter {

    private final String name;
    private Object value;

    public NamedParameterBase(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("value", value);
    }

    private static final long serialVersionUID = -5640275306788648337L;
}
