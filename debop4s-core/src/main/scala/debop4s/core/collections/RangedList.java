package debop4s.core.collections;

import java.util.List;

/**
 * 범위가 존재하는 List
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 9. 오전 2:15
 */
public interface RangedList<T, R extends Comparable<R>> {

    List<T> getList();

    R getLowerBound();

    R getUpperBound();

    int getSize();
}
