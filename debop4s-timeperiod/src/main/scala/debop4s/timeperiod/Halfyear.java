package debop4s.timeperiod;

/**
 * 반기 (Halfyear)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오후 12:03
 */
public enum Halfyear {

    /** 상반기 */
    First(1),

    /** 하반기 */
    Second(2);

    private final int value;

    public int getValue() {
        return value;
    }

    Halfyear(final int value) {
        this.value = value;
    }

    public static Halfyear valueOf(final int halfyear) {
        if (halfyear == 1)
            return First;
        else if (halfyear == 2)
            return Second;
        else
            throw new IllegalArgumentException("Halfyear 는 1,2 값만 가질 수 있습니다. halfyear=" + halfyear);
    }
}
