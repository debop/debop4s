package debop4s.timeperiod;

/**
 * 한주를 구성하는 요일을 나타냅니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오후 12:42
 */
public enum DayOfWeek {

    Monday(1, "월"),

    Tuesday(2, "화"),

    Wednesday(3, "수"),

    Thursday(4, "목"),

    Friday(5, "금"),

    Saturday(6, "토"),

    Sunday(7, "일");

    /** 요일의 Int형 값을 반환한다 */
    private final int value;

    public int getValue() {
        return value;
    }

    /** 요일 이름을 반환한다 */
    public final String description;

    public String getDescription() {
        return description;
    }

    DayOfWeek(final int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Integer 값으로 요일을 반환한다.
     *
     * @param dayOfWeek the day of week
     * @return the day of week
     */
    public static DayOfWeek valueOf(final int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return Monday;
            case 2:
                return Tuesday;
            case 3:
                return Wednesday;
            case 4:
                return Thursday;
            case 5:
                return Friday;
            case 6:
                return Saturday;
            case 7:
                return Sunday;
        }
        throw new IllegalArgumentException("요일에 해당하는 숫자가 아닙니다. (1~7), dayOfWeek=" + dayOfWeek);
    }
}
