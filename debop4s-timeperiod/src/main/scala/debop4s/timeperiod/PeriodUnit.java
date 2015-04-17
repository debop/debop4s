package debop4s.timeperiod;

/**
 * 기간의 종류를 나타냅니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오전 12:02
 */
public enum PeriodUnit {

    /** 총 기간 */
    All("All"),
    /** 년 */
    Year("Year"),
    /** 반기 */
    Halfyear("Halfyear"),
    /** 분기 */
    Quarter("Quarter"),
    /** 월 */
    Month("Month"),
    /** 주 */
    Week("Week"),
    /** 일 */
    Day("Day"),
    /** 시 */
    Hour("Hour"),
    /** 분 */
    Minute("Minute"),
    /** 초 */
    Second("Second"),
    /** 밀리초 */
    Millisecond("Millisecond");

    private final String periodKind;

    public String getValue() {
        return periodKind;
    }

    PeriodUnit(final String periodKind) {
        this.periodKind = periodKind;
    }
}
