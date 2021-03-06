package debop4s.core;

/**
 * 인자 3개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 27.
 */
public interface JAction3<T1, T2, T3> {

    /**
     * Perform method.
     *
     * @param arg1 the arg 1
     * @param arg2 the arg 2
     * @param arg3 the arg 3
     */
    public void perform(T1 arg1, T2 arg2, T3 arg3);
}
