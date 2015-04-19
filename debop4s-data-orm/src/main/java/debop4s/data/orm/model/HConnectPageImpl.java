package debop4s.data.orm.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * HConnectPageImpl
 *
 * @author sunghyouk.bae@gmail.com
 */
public class HConnectPageImpl<T> extends PageImpl<T> {

    /**
     * Constructor of {@code PageImpl}.
     *
     * @param content  the content of this page, must not be {@literal null}.
     * @param pageable the paging information, can be {@literal null}.
     * @param total    the total amount of items available
     */
    public HConnectPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    /**
     * Creates a new {@link PageImpl} with the given content.
     * This will result in the created {@link org.springframework.data.domain.Page} being identical
     * to the entire {@link List}.
     *
     * @param content must not be {@literal null}.
     */
    public HConnectPageImpl(List<T> content) {
        super(content);
    }

    public boolean isLastPage() {
        return isLast();
    }

    private static final long serialVersionUID = -2585896900746334586L;
}
