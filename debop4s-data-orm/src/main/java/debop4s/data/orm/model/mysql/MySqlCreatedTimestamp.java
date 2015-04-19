package debop4s.data.orm.model.mysql;

import javax.persistence.Column;

/**
 * MySQL 에서는 레코드의 생성 일자를 기본값으로 가지게 한다.
 * <p/>
 * NOTE: JPA 를 사용하여 [[javax.persistence.PrePersist]] 를 사용합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:54
 */
@Deprecated
public interface MySqlCreatedTimestamp {

    /** MySQL 에서 레코드별로 최신 갱신일자를 표현합니다. */
    @Column(updatable = false, insertable = false, columnDefinition = "timestamp default current_timestamp")
    java.util.Date getCreatedTimestamp();
}
