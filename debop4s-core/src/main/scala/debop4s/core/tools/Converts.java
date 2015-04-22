package debop4s.core.tools;

import debop4s.core.utils.Strings;
import jodd.typeconverter.Convert;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 여러가지 수형에 대한 변환을 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @see debop4s.core.utils.NumberConverts
 * @since 13. 10. 7. 오후 5:38
 * @deprecated use {@link jodd.typeconverter.Convert}를 사용하세요.
 */
@Slf4j
public final class Converts {

    private Converts() { }

    /**
     * Integer 의 값을 반환합니다. null 이면 0 을 반환합니다.
     *
     * @param v 검사할 값
     * @return int 값 또는 0
     */
    public static int getValue(final Integer v) {
        return (v == null) ? 0 : v;
    }

    /**
     * 객체를 int 값으로 변환합니다. null 이거나 변환 실패시에는 0 을 반환합니다.
     *
     * @param v 변환할 객체
     * @return int 값
     */
    public static int getInt(final Object v) {
        return getInt(v, 0);
    }

    public static int getInt(final Object v, int defaultValue) {
        try {
            return Convert.toIntValue(v, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 객체를 long 값으로 변환합니다. null 이거나 변환 실패시에는 0 을 반환합니다.
     *
     * @param v 변환할 객체
     * @return long 값
     */
    public static long getLong(final Object v) {
        return getLong(v, 0L);
    }

    public static long getLong(final Object v, long defaultValue) {
        try {
            return Convert.toLongValue(v, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(final Object v) {
        return getDouble(v, 0.0D);
    }

    public static double getDouble(final Object v, double defaultValue) {
        try {
            return Convert.toDoubleValue(v, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float getFloat(final Object v) {
        return getFloat(v, 0f);
    }

    public static float getFloat(final Object v, float defaultValue) {
        try {
            return Convert.toFloatValue(v, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static short getShort(final Object v) {
        return getShort(v, (short) 0);
    }

    public static short getShort(final Object v, short defaultValue) {
        try {
            return Convert.toShortValue(v, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Long 의 값을 반환합니다. null 이면 0 을 반환합니다.
     *
     * @param v 검사할 값
     * @return long 값 또는 0
     */
    public static long getValue(final Long v) {
        return (v == null) ? 0 : v;
    }


    /**
     * Float 의 값을 반환합니다. null 이면 0 을 반환합니다.
     *
     * @param v 검사할 값
     * @return float 값 또는 0
     */
    public static float getValue(final Float v) {
        return (v == null) ? 0.0f : v;
    }

    /**
     * Double 의 값을 반환합니다. null 이면 0 을 반환합니다.
     *
     * @param v 검사할 값
     * @return double 값 또는 0
     */
    public static double getValue(final Double v) {
        return (v == null) ? 0.0 : v;
    }

    /**
     * Short 의 값을 반환합니다. null 이면 0 을 반환합니다.
     *
     * @param v 검사할 값
     * @return short 값 또는 0
     */
    public static short getValue(final Short v) {
        return (v == null) ? 0 : v;
    }

    /**
     * 객체를 문자열로 표현합니다.
     *
     * @param x 객체
     * @return 객체 정보
     */
    public static String toString(final Object x) {
        return x == null ? "" : x.toString();
    }

    /**
     * int 값을 문자열로 표현합니다.
     *
     * @param v int 값
     * @return int 값의 문자열
     */
    public static String toString(final int v) {
        return Integer.toString(v);
        // return Integer.valueOf(v).toString();
    }

    /**
     * long 값을 문자열로 표현합니다.
     *
     * @param v long 값
     * @return long 값의 문자열
     */
    public static String toString(final long v) {
        return Long.toString(v);
        // return Long.valueOf(v).toString();
    }

    /**
     * Integer 값을 문자열로 표현합니다. null 이면 빈 문자열을 반환합니다.
     *
     * @param v int 값
     * @return Integer 값의 문자열
     */
    public static String toString(final Integer v) {
        return (v == null) ? "" : String.valueOf(v);
    }

    /**
     * Long 값을 문자열로 표현합니다. null 이면 빈 문자열을 반환합니다.
     *
     * @param v int 값
     * @return Long 값의 문자열
     */
    public static String toString(final Long v) {
        return (v == null) ? "" : String.valueOf(v);
    }

    /**
     * 문자열을 파싱하여 Integer로 변환합니다. 빈문자열이면 null 을 반환합니다.
     *
     * @param s 문자열
     * @return Integer 값
     */
    public static Integer toInteger(final String s) {
        return Strings.isEmpty(s) ? 0 : Integer.parseInt(s);
    }

    /**
     * 문자열을 파싱하여 Long로 변환합니다. 빈문자열이면 null 을 반환합니다.
     *
     * @param s 문자열
     * @return Long 값
     */
    public static Long toLong(final String s) {
        return Strings.isEmpty(s) ? 0 : Long.parseLong(s);
    }


    /**
     * 지정한 Long 값을 Timestamp 값으로 간주하여 {@link DateTime} 으로 변환합니다.
     *
     * @param v Timestamp의 milliseconds 값
     * @return DateTime
     */
    public static DateTime toDateTime(final long v) {
        return new DateTime(v);
    }

    /**
     * 지정한 Long 값을 Timestamp 값으로 간주하여 {@link DateTime} 으로 변환합니다.
     *
     * @param v Timestamp의 milliseconds 값
     * @return DateTime
     */
    public static DateTime toDateTime(final Long v) {
        return (v == null)
                ? new DateTime(0, DateTimeZone.UTC)
                : new DateTime(v);
    }

    /**
     * 문자열이 Long이나 Int 값일 때에 값을 파싱하여 DateTime 으로 변환합니다.
     *
     * @param numberStr 숫자를 표현한 문자열
     * @return 변환된 DateTime 인스턴스
     */
    public static DateTime toDateTime(final String numberStr) {
        return (Strings.isEmpty(numberStr))
                ? new DateTime(0, DateTimeZone.UTC)
                : new DateTime(toLong(numberStr));
    }
}
