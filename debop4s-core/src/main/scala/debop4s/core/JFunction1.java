package debop4s.core;

/**
 * 인자 1개를 받고, 결과를 반환하는 메소드를 가진 인터페이스
 *
 * @param <T> 함수 인자의 수형
 * @param <R> 함수 반환 값의 수형
 */
public interface JFunction1<T, R> {

    /**
     * 수행할 함수
     *
     * @param arg 인자
     * @return 수행결과
     */
    R execute(T arg);
}
