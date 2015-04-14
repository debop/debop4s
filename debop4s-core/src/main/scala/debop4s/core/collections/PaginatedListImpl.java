package debop4s.core.collections;

import debop4s.core.ValueObjectBase;
import debop4s.core.utils.ToStringHelper;
import lombok.Getter;

import java.util.List;

/**
 * {@link PaginatedList}의 구현체입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 9. 오전 2:10
 */
@Getter
public class PaginatedListImpl<T> extends ValueObjectBase implements PaginatedList<T> {

    private final List<T> list;
    private final int pageNo;
    private final int pageSize;
    private final long totalItemCount;
    private final long pageCount;

    public PaginatedListImpl(List<T> list, int pageNo, int pageSize, long totalItemCount) {
        this.list = list;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalItemCount = totalItemCount;

        pageCount = (totalItemCount / pageSize) + ((totalItemCount % pageSize > 0) ? 1 : 0);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("pageNo", pageNo)
                    .add("pageSize", pageSize)
                    .add("totalItemCount", totalItemCount)
                    .add("pageCount", pageCount)
                    .add("list", list);
    }

    private static final long serialVersionUID = 687071957543951232L;
}
