package debop4s.data.orm.jtests.mapping.simple;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * debop4s.data.orm.s.mapping.simple.ScalaLifecycleEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 11:22
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.simple", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class LifecycleEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // Generated 는 DB에서 값을 관리하므로, Hibernate에서 특정 작업 후 그 값을 갱신하도록 refresh를 강제화한다는 뜻이다.
    // MySqlUpdatedTimestamp, MySqlUpdatedTimestamp 를 참고하여, DB 컬럼에 정의해줘야 한다.

    // JPA 에서는 다음과 같이 하는 것이 좋다.
    @PrePersist
    private void onPrePersist() {
        if (createdAt == null) createdAt = new Date();
        updatedAt = new Date();
    }

    // @Generated(GenerationTime.INSERT)
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    // @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    private static final long serialVersionUID = 2291846685337546917L;
}
