package debop4s.core;

/**
 * 정렬 방법
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 12. 19.
 */
public enum SortDirection {

    /** 순차 정렬 */
    ASC("ASC"),

    /** 역순 정렬 */
    DESC("DESC");

    private final String value;

    public String getValue() {
        return value;
    }

    SortDirection(String value) {
        this.value = value;
    }

}
