package debop4s.data.orm.jtests.mapping.property.enumerated;

/**
 * AgeType
 *
 * @author sunghyouk.bae@gmail.com
 */
public enum AgeType {

    A0(0), A10(10), A20(20), A30(30), A40(40);

    private final int value;

    AgeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int getId() {
        return this.value;
    }

    public static AgeType valueOf(int value) throws Exception {
        switch (value) {
            case 0:
                return A0;
            case 10:
                return A10;
            case 20:
                return A20;
            case 30:
                return A30;
            case 40:
                return A40;
            default:
                throw new Exception("지원하지 않는 값입니다." + value);
        }
    }
}
