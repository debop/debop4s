package debop4s.core;

/**
 * Byte 배열의 정보를 문자열로 표현할 때 사용할 형식 (Base64 또는 HexDecimal 방식)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 28. 오후 5:08
 */
public enum BinaryStringFormat {

    /** Base64 인코딩 방식으로 문자열로 표현 */
    Base64("Base64"),

    /** 16진수 방식으로 문자열로 표현 */
    HexDecimal("HexDecimal");

    private final String value;

    public String getValue() {
        return value;
    }

    BinaryStringFormat(final String stringFormat) {
        this.value = stringFormat;
    }
}

