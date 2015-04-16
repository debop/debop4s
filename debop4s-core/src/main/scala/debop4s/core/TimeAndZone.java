package debop4s.core;

import debop4s.core.utils.Hashs;
import debop4s.core.utils.Strings;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Timestamp 와 TimeZone 을 가집니다.
 *
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Getter
@Setter
public class TimeAndZone extends ValueObjectBase {

    /** {@link DateTime} 으로부터 TimeAndZone을 생성합니다. */
    public static TimeAndZone fromDateTime(DateTime dt) {
        return new TimeAndZone(dt.getMillis(), dt.getZone().getID());
    }

    /** TimeAndZone 으로부터 DateTime 으로 변환합니다. */
    public static DateTime toDateTime(TimeAndZone tz) {
        return new DateTime(tz.getTime(), DateTimeZone.forID(tz.getZoneId()));
    }

    /** Timestamp 값 */
    private Long time;

    /** TimeZone Id */
    private String zoneId = "UTC";

    public TimeAndZone() {
        this(0L);

    }

    public TimeAndZone(Long time) {
        this(time, DateTimeZone.UTC.getID());
    }

    public TimeAndZone(Long time, String timeZoneId) {
        this.time = time;
        this.zoneId = timeZoneId;
    }

    public DateTime toDateTime() {
        return Strings.isEmpty(zoneId)
                ? new DateTime(time, DateTimeZone.UTC)
                : new DateTime(time, DateTimeZone.forID(zoneId));
    }

    public void parseValue(DateTime dt) {
        if (dt != null) {
            this.time = dt.getMillis();
            this.zoneId = dt.getZone().getID();
        }
    }

    @Override
    public int hashCode() {
        return Hashs.compute(time, zoneId);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("time", time)
                    .add("zoneId", zoneId);
    }

    private static final long serialVersionUID = -553819846707324408L;
}
