package debop4s.core.collections;

import java.util.List;

/**
 * Paging 된 정보를 표현합니다.
 * Spring의 Page[T] 와 유사합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 9. 오전 2:08
 */
public interface PaginatedList<T> {

    /**
     * 페이징된 엔티티의 리스트
     *
     * @return 페이징된 엔티티의 리스트
     */
    List<T> getList();

    /**
     * 페이지 번호 (1부터 시작)
     *
     * @return 페이지 번호
     */
    int getPageNo();

    /**
     * 페이지 크기
     *
     * @return 페이지 크기
     */
    int getPageSize();

    /**
     * 저장소에 저장된 엔티티의 실제 총 갯수
     *
     * @return 엔티티의 총 갯수
     */
    long getTotalItemCount();

    /**
     * 페이지 총 수
     *
     * @return 페이지 총 수
     */
    long getPageCount();
}
