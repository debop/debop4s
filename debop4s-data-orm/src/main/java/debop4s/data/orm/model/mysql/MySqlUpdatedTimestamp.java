package debop4s.data.orm.model.mysql;

import javax.persistence.Column;

/**
 * MySQL 에서는 레코드의 최신 갱신 일자를 직접 지원한다.
 * 이를 정의한 인터페이스를 사용하게 되면, 굳이 Interceptor 를 통해 UpdateTimestamp 를 변경할 필요도 없다.
 * <p/>
 * NOTE: JPA 를 사용하여 [[javax.persistence.PrePersist]] 를 사용합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:55
 */
@Deprecated
public interface MySqlUpdatedTimestamp {

    /** MySQL 에서 레코드별로 최신 갱신일자를 표현합니다. */
    @Column(updatable = false, insertable = false, columnDefinition = "timestamp default current_timestamp")
    java.util.Date getUpdatedTimestamp();
}
