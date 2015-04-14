package debop4s.core;

/**
 * 인자 0개를 받고, 결과를 반환하는 메소드를 가진 인터페이스
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 27.
 */
public interface JFunction<R> {

    /**
     * 수행할 함수
     *
     * @return 수행 결과
     */
    R execute();
}
