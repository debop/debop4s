package debop4s.core.collections;

import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import debop4s.core.utils.ToStringHelper;
import lombok.Getter;

import java.util.List;

/**
 * 상하한을 가진 목록을 표현합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 9. 오전 2:16
 */
@Getter
public class RangedListImpl<T, R extends Comparable<R>> extends ValueObjectBase implements RangedList<T, R> {

    private final List<T> list;
    private final R lowerBound;
    private final R upperBound;

    public RangedListImpl(List<T> list, R lowerBound, R upperBound) {
        this.list = list;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public int getSize() {
        return (list == null) ? -1 : list.size();
    }

    @Override
    public int hashCode() {
        return Hashs.compute(lowerBound, upperBound, list);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("lowerBound", lowerBound)
                    .add("upperBound", upperBound)
                    .add("list", list);
    }

    private static final long serialVersionUID = 3970942501124027399L;
}

