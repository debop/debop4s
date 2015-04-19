package debop4s.data.orm.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@MappedSuperclass
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class UuidEntity extends HibernateEntityBase<String> {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Setter(AccessLevel.PROTECTED)
    private String id;

    private static final long serialVersionUID = 6134813899024746100L;
}
