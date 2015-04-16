package debop4s.core.reflect;

import debop4s.core.Guard;
import debop4s.core.tools.ArrayTool;
import debop4s.core.utils.Strings;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 리플렉션을 이용하여, 객체를 생성시키는 Utility Class 입니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 12
 */
@Slf4j
public final class Activators {

    private Activators() {}

    /**
     * className에 해당하는 클래스를 인스턴싱합니다.
     *
     * @param className 클래스 명
     * @return 인스턴스 object
     */
    public static Object createInstance(final String className) {

        log.trace("create instance... className=[{}]", className);

        try {
            return createInstance(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 지정된 수형의 새로운 인스턴스를 생성합니다.
     *
     * @param clazz 생성할 수형
     * @return 지정한 수형의 새로운 인스턴스, 생성 실패시에는 null을 반환합니다.
     */
    public static <T> T createInstance(final Class<T> clazz) {
        Guard.shouldNotBeNull(clazz, "clazz");

        log.trace("수형 [{}] 의 새로운 인스턴스를 생성합니다...", clazz.getName());

        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.warn(clazz.getName() + " 수형을 생성하는데 실패했습니다.", e);
            return null;
        }
    }


    /**
     * Create instance.
     *
     * @param clazz    the clazz
     * @param initArgs the init args
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(final Class<T> clazz, final Object... initArgs) {
        Guard.shouldNotBeNull(clazz, "clazz");

        log.trace("[{}] 수형의 객체를 생성합니다. initArgs=[{}]", clazz.getName(), initArgs);

        if (ArrayTool.isEmpty(initArgs))
            return createInstance(clazz);

        try {
            List<Class<?>> parameterTypes = new ArrayList<>();
            for (Object arg : initArgs) {
                Class<?> argClass = arg.getClass();
                if (argClass.equals(Integer.class)) argClass = Integer.TYPE;
                if (argClass.equals(Long.class)) argClass = Long.TYPE;
                parameterTypes.add(argClass);
            }
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(ArrayTool.asArray(parameterTypes, Object.class));
                return (constructor != null)
                        ? constructor.newInstance(initArgs)
                        : null;
            } catch (Throwable ignored) {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> ctor : constructors) {
                    if (ctor.getParameterTypes().length == parameterTypes.size())
                        return (T) ctor.newInstance(initArgs);
                }
            }
        } catch (Exception e) {
            log.error(clazz.getName() + " 수형을 생성하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 지정한 수형의 생성자 정보를 반환합니다.
     *
     * @param clazz          수형
     * @param parameterTypes 생성자에 제공할 인자의 수형들
     * @return 생성자 정보
     */
    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {

        log.trace("[{}] 수형의 생성자를 구합니다. parameterTypes=[{}]",
                  clazz.getName(), Strings.listToString(parameterTypes));

        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
