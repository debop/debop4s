package debop4s.core;

/**
 * 인자 2개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 27.
 */
public interface JAction2<T1, T2> {

    /**
     * Perform method.
     *
     * @param arg1 the arg 1
     * @param arg2 the arg 2
     */
    public void perform(T1 arg1, T2 arg2);
}
