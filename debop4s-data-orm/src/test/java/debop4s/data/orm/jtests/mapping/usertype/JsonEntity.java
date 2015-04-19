package debop4s.data.orm.jtests.mapping.usertype;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.jtests.mapping.Employee;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * debop4s.data.orm.s.mapping.usertype.JsonEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 29. 오후 12:44
 */
@Entity
@Cache(region = "data.usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JsonEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    /**
     * ScalaEmployee 정보를 Json Serialize 해서 className 컬럼에는 Employee의 실제 class name을 저장하고,
     * jsonText에는 employee 객체를 json serialize 한 text를 저장합니다.
     * <p/>
     * 로드 시에는 두 값을 이용하여 deserialize 하여, 반환합니다.
     */
    @Columns(columns = { @Column(name = "className"), @Column(name = "jsonText", length = 2000) })
    @Type(type = "debop4s.data.orm.hibernate.usertype.JacksonUserType")
    private Employee employee;

    @Override
    public int hashCode() {
        return Hashs.compute(employee);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("employee", employee);
    }

    private static final long serialVersionUID = 2820804056937831772L;
}
