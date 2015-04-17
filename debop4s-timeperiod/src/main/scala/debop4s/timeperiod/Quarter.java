package debop4s.timeperiod;

/**
 * 분기 (Quarter)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오전 11:31
 */
public enum Quarter {

    First(1), Second(2), Third(3), Fourth(4);

    private final int value;

    public int getValue() {
        return value;
    }

    Quarter(int value) {
        this.value = value;
    }

    public static Quarter valueOf(final int quarter) {
        switch (quarter) {
            case 1:
                return First;
            case 2:
                return Second;
            case 3:
                return Third;
            case 4:
                return Fourth;
        }
        throw new IllegalArgumentException("Invalid quarter number. [1-4], quarter=" + quarter);
    }
}
