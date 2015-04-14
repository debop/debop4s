package debop4s.core;

/**
 * 인자 4개를 받고, 결과를 반환하는 메소드를 가진 인터페이스
 *
 * @param <T1> 함수 인자 1의 수형
 * @param <T2> 함수 인자 2의 수형
 * @param <T3> 함수 인자 3의 수형
 * @param <T4> 함수 인자 4의 수형
 * @param <R>  함수 반환 값의 수형
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 27.
 */
public interface JFunction4<T1, T2, T3, T4, R> {
    /**
     * 수행할 함수
     *
     * @param arg1 함수 인자 1
     * @param arg2 함수 인자 2
     * @param arg3 함수 인자 3
     * @param arg4 함수 인자 4
     * @return 수행 결과
     */
    R execute(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
}
