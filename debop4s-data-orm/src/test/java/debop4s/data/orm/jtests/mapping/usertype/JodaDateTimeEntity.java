package debop4s.data.orm.jtests.mapping.usertype;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.DateTimeRange;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * debop4s.data.orm.s.mapping.usertype.JodaDateTimeEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:55
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JodaDateTimeEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "JodaStart")
    @Type(type = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
    private DateTime start;

    @Column(name = "JodaEnd")
    @Type(type = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
    private DateTime end;


    /** UTC DateTime 과 TimeZone 으로 분리해서 저장하고, 로드 시에는 통합합니다. */
    @Columns(columns = { @Column(name = "startTime"), @Column(name = "startTimeZone") })
    @Type(type = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
    private DateTime startTZ;

    @Columns(columns = { @Column(name = "endTime"), @Column(name = "endTimeZone") })
    @Type(type = "debop4s.data.orm.hibernate.usertype.jodatime.TimestampAndTimeZone")
    private DateTime endTZ;

    // 복합 수형인 경우 컬럼들을 명시해줘야 합니다.
    //
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "startTime", column = @Column(name = "rangeStart1")),
                          @AttributeOverride(name = "endTime", column = @Column(name = "rangeEnd1")) })
    private DateTimeRange range1;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "startTime", column = @Column(name = "rangeStart2")),
                          @AttributeOverride(name = "endTime", column = @Column(name = "rangeEnd2"))
    })
    private DateTimeRange range2;

    @Override
    public int hashCode() {
        return Hashs.compute(start, end, startTZ, endTZ);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("start", start)
                    .add("end", end)
                    .add("startTZ", startTZ)
                    .add("endTZ", endTZ);
    }

    private static final long serialVersionUID = -1636595656626680292L;
}
