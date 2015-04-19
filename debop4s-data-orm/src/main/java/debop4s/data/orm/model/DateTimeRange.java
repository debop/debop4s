package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 기간을 나타내는 Component (시작시각 ~ 완료시각)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 11. 오전 9:22
 */
@Embeddable
public class DateTimeRange extends ValueObjectBase {

    private static final long ZeroMillis = 0L;
    private static final long MinMillis = 0L;
    private static final long MillisPerDay = 24 * 60 * 60 * 1000L;
    private static final long MaxMillis = 3652059L * MillisPerDay - 1L;

    private static final DateTime MinPeriodTime = new DateTime(MinMillis);
    private static final DateTime MaxPeriodTime = new DateTime(MaxMillis);

    @Type(type = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
    @Column(name = "startTime")
    private DateTime startTime;

    @Type(type = "debop4s.data.orm.hibernate.usertype.JodaDateTimeUserType")
    @Column(name = "endTime")
    private DateTime endTime;

    public DateTimeRange() {
        this(MinPeriodTime, MaxPeriodTime);
    }

    public DateTimeRange(DateTime startTime, DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTimeRange(DateTimeRange src) {
        this.startTime = src.startTime;
        this.endTime = src.endTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setRange(DateTime startTime, DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(startTime, endTime);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("startTime", startTime)
                    .add("endTime", endTime);
    }

    private static final long serialVersionUID = -2341282445775001388L;
}
