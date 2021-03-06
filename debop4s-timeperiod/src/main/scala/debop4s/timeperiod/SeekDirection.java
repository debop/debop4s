package debop4s.timeperiod;

/**
 * 검색 방향
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 11. 오후 12:02
 */
public enum SeekDirection {

    /** 미래로 (시간 값을 증가 시키는 방향) */
    Forward(1),

    /** 과거로 (시간 값을 감소 시키는 방향) */
    Backward(-1);

    private final int value;

    public int getValue() {
        return value;
    }

    SeekDirection(final int value) {
        this.value = value;
    }

    public static SeekDirection valueOf(final int value) {
        switch (value) {
            case 1:
                return Forward;
            case -1:
                return Backward;
            default:
                throw new IllegalArgumentException("not supported value. value=" + value);
        }
    }

}
