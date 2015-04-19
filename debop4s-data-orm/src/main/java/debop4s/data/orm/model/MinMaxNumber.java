package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

/**
 * 최대, 최소 값을 가지는 Component 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
@Embeddable
@Access(AccessType.FIELD)
@Getter
@Setter
public class MinMaxNumber<T extends Number> extends ValueObjectBase {

    private T min;
    private T max;

    public MinMaxNumber() {}

    public MinMaxNumber(T min, T max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(min, max);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("min", min)
                    .add("max", max);
    }

    private static final long serialVersionUID = -8470678262510496311L;
}
