package debop4s.core.spring;

/**
 * 개발 단계별 구분을 위한 Profile 정보입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 23. 오후 1:14
 */
public enum JProfiles {

    /** Local */
    LOCAL("LOCAL"),

    /** Develop */
    DEVELOP("DEVELOP"),

    /** Test */
    TEST("TEST"),

    /** Production */
    PRODUCTION("PRODUCTION");

    private final String value;

    public String getValue() {
        return value;
    }

    JProfiles(final String value) {
        this.value = value;
    }
}
