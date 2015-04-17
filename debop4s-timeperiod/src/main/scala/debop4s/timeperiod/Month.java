package debop4s.timeperiod;

/**
 * 월 (Month) 종류
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오전 10:48
 */
public enum Month {

    January(1),
    Feburary(2),
    March(3),
    April(4),
    May(5),
    June(6),
    July(7),
    August(8),
    September(9),
    October(10),
    November(11),
    December(12);

    private final int value;

    public int getValue() {
        return value;
    }

    Month(final int value) {
        this.value = value;
    }
}
