package debop4s.core.tools;

import debop4s.core.Guard;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * Reflection 관련 Utility Class 입니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 12
 */
@Slf4j
public final class JavaReflects {

    private JavaReflects() {}

    /**
     * 객체가 Generic 형식일 경우, 형식인자(kind parameter)들을 가져옵니다.
     *
     * @param x 검사할 객체
     * @return 객체가 Generic 형식인 경우, 형식인자의 배열, Generic이 아니면 빈 배열을 반환
     */
    public static Type[] getParameterTypes(final Object x) {
        Guard.shouldNotBeNull(x, "x");
        log.debug("객체가 Generic 수형이라면 모든 형식인자들을 가져옵니다. clazz=[{}]", x.getClass().getName());

        try {
            ParameterizedType ptype = (ParameterizedType) x.getClass().getGenericSuperclass();
            assert ptype != null : "지정된 객체가 generic 형식이 아닙니다.";
            return ptype.getActualTypeArguments();
        } catch (Exception e) {
            log.warn("Generic 형식의 객체로부터 인자 수형들을 추출하는데 실패했습니다.", e);
            return new Type[0];
        }
    }

    /**
     * 인스턴스가 Generic 형식이라면 첫번째 Type parameter의 수형을 반환한다.
     *
     * @param x   the x
     * @param <T> 수형
     * @return the generic parameter kind
     */
    public static <T> Class<T> getGenericParameterType(final Object x) {
        return getGenericParameterType(x, 0);
    }

    /**
     * 인스턴스가 Generic 형식이라면 index+1 번째 Type parameter의 수형을 반환한다.
     *
     * @param x     the x
     * @param index the index
     * @param <T>   반환할 수형
     * @return the generic parameter kind
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericParameterType(final Object x, final int index) {
        Guard.shouldNotBeNull(x, "x");
        log.debug("인스턴스 [{}]의 [{}] 번째 제너릭 인자 수형을 찾습니다.", x.getClass().getName(), index);

        Type[] types = getParameterTypes(x);

        if (types != null && types.length > index) {
            return (Class<T>) types[index];
        }
        throw new UnsupportedOperationException("Generic 형식의 객체로부터 인자 수형들을 추출하는데 실패했습니다.");
    }

    /**
     * 수형이 primitive kind 과 호환된다면 Primitive type으로 변경합니다.
     *
     * @param clazz the clazz
     * @return class
     */
    public static Class toPrimitiveType(final Class<?> clazz) {

        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }

        return clazz;
    }
}
