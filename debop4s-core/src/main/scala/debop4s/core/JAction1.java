package debop4s.core;

/**
 * 인자 1개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 27.
 */
public interface JAction1<T> {
    /**
     * 작업을 수행합니다.
     *
     * @param arg 인자
     */
    void perform(T arg);
}