package debop4s.data.orm.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * IntEntity
 *
 * @author sunghyouk.bae@gmail.com
 */
@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@Getter
public class IntEntity extends HibernateEntityBase<Integer> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    private static final long serialVersionUID = 6269381048464930325L;
}
