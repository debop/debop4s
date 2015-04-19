package debop4s.data.orm.jtests.mapping;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import debop4s.data.orm.model.UpdatedTimestampEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "JavaEmployee")
@org.hibernate.annotations.Cache(region = "data.usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
@Getter
@Setter
public class Employee extends HibernateEntityBase<Long> implements UpdatedTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "employee_seq")
    @Column(name = "EmployeeId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "EmpNo", nullable = false, length = 32)
    private String empNo;

    @Column(name = "EmployeeName", nullable = false, length = 32)
    private String name;

    @Column(name = "Email", length = 32)
    private String email;

    private Boolean isLeader = false;

    @Type(type = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
    private DateTime updatedTimestamp;

    /** 엔티티의 최근 갱신 일자를 수정합니다. */
    @PrePersist
    @Override
    public void updateUpdatedTimestamp() {
        updatedTimestamp = DateTime.now();
    }

    @Override
    public int hashCode() {
        return Hashs.compute(empNo, name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("id", id)
                    .add("empNo", empNo)
                    .add("name", name)
                    .add("email", email);
    }

    private static final long serialVersionUID = 6936596398947403315L;
}
